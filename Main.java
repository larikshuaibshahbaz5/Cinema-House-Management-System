package cinema;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main extends Application {
    private final CinemaHouseManagementSystem system = new CinemaHouseManagementSystem();
    private TextArea outputArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cinema House Management System");
        primaryStage.setScene(createLoginScene(primaryStage));
        primaryStage.show();
    }

    private Scene createLoginScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        VBox loginBox = new VBox(10);
        loginBox.setPadding(new Insets(20));
        loginBox.setAlignment(Pos.CENTER);

        ImageView logoView = createLogoView();
        Label appNameLabel = new Label("Cinema House");
        appNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label welcomeLabel = new Label("Please log in as Admin or Customer");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(event -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Login", "Username and password are required.", Alert.AlertType.WARNING);
                return;
            }
            if (system.login(username, password)) {
                String role = system.getRole(username);
                stage.setScene(createDashboardScene(stage, username, role));
            } else {
                showAlert("Login", "Invalid username or password.", Alert.AlertType.ERROR);
            }
        });

        Button continueAsGuestButton = new Button("Continue as Guest");
        continueAsGuestButton.setMaxWidth(Double.MAX_VALUE);
        continueAsGuestButton.setOnAction(event -> stage.setScene(createDashboardScene(stage, "Guest", "customer")));

        loginBox.getChildren().addAll(logoView, appNameLabel, welcomeLabel, usernameField, passwordField, loginButton, continueAsGuestButton);
        root.setCenter(loginBox);

        return new Scene(root, 420, 380);
    }

    private ImageView createLogoView() {
        File logoFile = new File("Cinema Logo.jpg");
        ImageView logoView = new ImageView();
        if (logoFile.exists()) {
            Image logo = new Image(logoFile.toURI().toString(), 120, 80, true, true);
            logoView.setImage(logo);
            logoView.setFitWidth(120);
            logoView.setFitHeight(80);
        }
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);
        return logoView;
    }

    private Scene createDashboardScene(Stage stage, String username, String role) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setText("Welcome, " + username + "! Choose an action from the menu.");

        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));

        Label header = new Label("Logged in as " + username + " (" + role + ")");
        Button viewMovies = new Button("View Movies");
        viewMovies.setMaxWidth(Double.MAX_VALUE);
        viewMovies.setOnAction(event -> outputArea.setText(system.getMovieListText()));

        Button searchMovie = new Button("Search Movie");
        searchMovie.setMaxWidth(Double.MAX_VALUE);
        searchMovie.setOnAction(event -> searchMovie());

        menu.getChildren().addAll(header, viewMovies, searchMovie);

        if ("admin".equalsIgnoreCase(role)) {
            Button addMovie = new Button("Add Movie");
            addMovie.setMaxWidth(Double.MAX_VALUE);
            addMovie.setOnAction(event -> addMovie());

            Button updateMovie = new Button("Update Movie");
            updateMovie.setMaxWidth(Double.MAX_VALUE);
            updateMovie.setOnAction(event -> updateMovie());

            Button deleteMovie = new Button("Delete Movie");
            deleteMovie.setMaxWidth(Double.MAX_VALUE);
            deleteMovie.setOnAction(event -> deleteMovie());

            Button manageOffers = new Button("Manage Offers");
            manageOffers.setMaxWidth(Double.MAX_VALUE);
            manageOffers.setOnAction(event -> manageOffers());

            menu.getChildren().addAll(addMovie, updateMovie, deleteMovie, manageOffers);
        }

        Button viewShop = new Button("View Shop Items");
        viewShop.setMaxWidth(Double.MAX_VALUE);
        viewShop.setOnAction(event -> outputArea.setText(String.join("\n", system.getShopItems())));

        Button bookSeats = new Button("Book Seats");
        bookSeats.setMaxWidth(Double.MAX_VALUE);
        bookSeats.setOnAction(event -> bookSeats(username));

        Button displaySeats = new Button("Display Available Seats");
        displaySeats.setMaxWidth(Double.MAX_VALUE);
        displaySeats.setOnAction(event -> displaySeats());

        Button cancelBooking = new Button("Cancel Booking");
        cancelBooking.setMaxWidth(Double.MAX_VALUE);
        cancelBooking.setOnAction(event -> cancelBooking(username));

        Button viewBookings = new Button("View My Bookings");
        viewBookings.setMaxWidth(Double.MAX_VALUE);
        viewBookings.setOnAction(event -> outputArea.setText(String.join("\n", system.getUserBookingSummaries(username))));

        menu.getChildren().addAll(viewShop, bookSeats, displaySeats, cancelBooking, viewBookings);

        if ("admin".equalsIgnoreCase(role) || "customer".equalsIgnoreCase(role)) {
            Button logoutButton = new Button("Logout");
            logoutButton.setMaxWidth(Double.MAX_VALUE);
            logoutButton.setOnAction(event -> stage.setScene(createLoginScene(stage)));
            menu.getChildren().add(logoutButton);
        }

        root.setLeft(menu);
        root.setCenter(outputArea);
        BorderPane.setMargin(outputArea, new Insets(10));
        GridPane.setVgrow(outputArea, Priority.ALWAYS);

        return new Scene(root, 900, 520);
    }

    private void searchMovie() {
        Optional<String> maybeName = prompt("Search Movie", "Enter movie name:");
        if (maybeName.isEmpty()) {
            return;
        }
        String result = system.getMovieDetails(maybeName.get());
        if (result == null) {
            showAlert("Search Result", "Movie not found.", Alert.AlertType.INFORMATION);
        } else {
            outputArea.setText(result);
        }
    }

    private void addMovie() {
        Optional<String> optionalId = prompt("Add Movie", "Movie ID:");
        if (optionalId.isEmpty()) return;

        Optional<String> optionalName = prompt("Add Movie", "Movie name:");
        if (optionalName.isEmpty()) return;

        Optional<String> optionalGenre = prompt("Add Movie", "Genre:");
        if (optionalGenre.isEmpty()) return;

        Optional<String> optionalDuration = prompt("Add Movie", "Duration (minutes):");
        if (optionalDuration.isEmpty()) return;

        Optional<String> optionalPrice = prompt("Add Movie", "Ticket price:");
        if (optionalPrice.isEmpty()) return;

        try {
            int duration = Integer.parseInt(optionalDuration.get());
            double price = Double.parseDouble(optionalPrice.get());
            boolean success = system.addMovie(optionalId.get(), optionalName.get(), optionalGenre.get(), duration, price);
            if (success) {
                showAlert("Add Movie", "Movie added successfully.", Alert.AlertType.INFORMATION);
                outputArea.setText(system.getMovieListText());
            } else {
                showAlert("Add Movie", "A movie with that name already exists.", Alert.AlertType.WARNING);
            }
        } catch (NumberFormatException e) {
            showAlert("Add Movie", "Duration and price must be numeric.", Alert.AlertType.ERROR);
        }
    }

    private void updateMovie() {
        Optional<String> optionalCurrentName = prompt("Update Movie", "Enter existing movie name:");
        if (optionalCurrentName.isEmpty()) return;

        Optional<String> optionalNewName = prompt("Update Movie", "New movie name (leave blank to keep current):");
        Optional<String> optionalNewGenre = prompt("Update Movie", "New genre (leave blank to keep current):");
        Optional<String> optionalNewDuration = prompt("Update Movie", "New duration (leave blank to keep current):");
        Optional<String> optionalNewPrice = prompt("Update Movie", "New price (leave blank to keep current):");

        Integer newDuration = null;
        Double newPrice = null;
        try {
            if (optionalNewDuration.isPresent() && !optionalNewDuration.get().isBlank()) {
                newDuration = Integer.parseInt(optionalNewDuration.get());
            }
            if (optionalNewPrice.isPresent() && !optionalNewPrice.get().isBlank()) {
                newPrice = Double.parseDouble(optionalNewPrice.get());
            }
        } catch (NumberFormatException e) {
            showAlert("Update Movie", "Duration and price must be numeric.", Alert.AlertType.ERROR);
            return;
        }

        boolean success = system.updateMovieData(optionalCurrentName.get(), optionalNewName.orElse(""), optionalNewGenre.orElse(""), newDuration, newPrice);
        if (success) {
            showAlert("Update Movie", "Movie updated successfully.", Alert.AlertType.INFORMATION);
            outputArea.setText(system.getMovieListText());
        } else {
            showAlert("Update Movie", "Movie not found.", Alert.AlertType.WARNING);
        }
    }

    private void deleteMovie() {
        Optional<String> maybeName = prompt("Delete Movie", "Enter movie name:");
        if (maybeName.isEmpty()) return;
        boolean deleted = system.deleteMovieByName(maybeName.get());
        if (deleted) {
            showAlert("Delete Movie", "Movie deleted successfully.", Alert.AlertType.INFORMATION);
            outputArea.setText(system.getMovieListText());
        } else {
            showAlert("Delete Movie", "Movie not found.", Alert.AlertType.WARNING);
        }
    }

    private void manageOffers() {
        Optional<String> actionChoice = prompt("Manage Offers", "Enter action: add, delete, or list:");
        if (actionChoice.isEmpty()) {
            return;
        }
        String action = actionChoice.get().trim().toLowerCase();
        switch (action) {
            case "add" -> addOffer();
            case "delete" -> deleteOffer();
            case "list" -> outputArea.setText(String.join("\n", system.getOffers()));
            default -> showAlert("Manage Offers", "Invalid action. Use add, delete, or list.", Alert.AlertType.WARNING);
        }
    }

    private void addOffer() {
        Optional<String> maybeOffer = prompt("Add Offer", "Enter offer description:");
        if (maybeOffer.isEmpty()) {
            return;
        }
        if (system.addOffer(maybeOffer.get())) {
            showAlert("Add Offer", "Offer added successfully.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Add Offer", "Offer already exists.", Alert.AlertType.WARNING);
        }
    }

    private void deleteOffer() {
        Optional<String> maybeOffer = prompt("Delete Offer", "Enter offer description to delete:");
        if (maybeOffer.isEmpty()) {
            return;
        }
        if (system.removeOffer(maybeOffer.get())) {
            showAlert("Delete Offer", "Offer deleted successfully.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Delete Offer", "Offer not found.", Alert.AlertType.WARNING);
        }
    }

    private void bookSeats(String username) {
        Optional<String> maybeMovie = prompt("Book Seats", "Enter movie name:");
        if (maybeMovie.isEmpty()) return;

        Optional<String> maybeSeats = prompt("Book Seats", "Enter seats to book (comma separated, e.g. A1,A2):");
        if (maybeSeats.isEmpty()) return;

        List<String> requestedSeats = Arrays.stream(maybeSeats.get().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<String> availableSeats = system.getAvailableSeatsForUI(maybeMovie.get(), requestedSeats);
        if (availableSeats == null) {
            showAlert("Book Seats", "Movie not found.", Alert.AlertType.WARNING);
            return;
        }
        if (availableSeats.isEmpty()) {
            showAlert("Book Seats", "No seats were available for booking.", Alert.AlertType.INFORMATION);
            return;
        }

        Double pricePerSeat = system.getMoviePrice(maybeMovie.get());
        if (pricePerSeat == null) {
            showAlert("Book Seats", "Movie not found.", Alert.AlertType.WARNING);
            return;
        }

        double totalAmount = pricePerSeat * availableSeats.size();
        Optional<String> paymentInput = prompt("Payment", "Total due: $" + String.format("%.2f", totalAmount) + ". Enter payment amount:");
        if (paymentInput.isEmpty()) return;

        try {
            double payment = Double.parseDouble(paymentInput.get());
            if (payment < totalAmount) {
                showAlert("Payment", "Payment was insufficient. Booking canceled.", Alert.AlertType.ERROR);
                return;
            }

            system.addBooking(username, maybeMovie.get(), availableSeats);
            String confirmation = "Successfully booked: " + String.join(", ", availableSeats);
            if (payment > totalAmount) {
                confirmation += "\nChange: $" + String.format("%.2f", payment - totalAmount);
            }
            showAlert("Book Seats", confirmation, Alert.AlertType.INFORMATION);
            outputArea.setText(system.getSeatMap(maybeMovie.get()));
        } catch (NumberFormatException e) {
            showAlert("Payment", "Invalid payment amount.", Alert.AlertType.ERROR);
        }
    }

    private void displaySeats() {
        Optional<String> maybeMovie = prompt("Display Seats", "Enter movie name:");
        if (maybeMovie.isEmpty()) return;
        outputArea.setText(system.getSeatMap(maybeMovie.get()));
    }

    private void cancelBooking(String username) {
        Optional<String> maybeMovie = prompt("Cancel Booking", "Enter movie name:");
        if (maybeMovie.isEmpty()) return;

        Optional<String> maybeSeats = prompt("Cancel Booking", "Enter seats to cancel (comma separated):");
        if (maybeSeats.isEmpty()) return;

        List<String> requestedSeats = Arrays.stream(maybeSeats.get().split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        boolean canceled = system.cancelSeatsForUI(username, maybeMovie.get(), requestedSeats);
        if (canceled) {
            showAlert("Cancel Booking", "Booking updated successfully.", Alert.AlertType.INFORMATION);
            outputArea.setText(String.join("\n", system.getUserBookingSummaries(username)));
        } else {
            showAlert("Cancel Booking", "No booking found for this movie.", Alert.AlertType.WARNING);
        }
    }

    private Optional<String> prompt(String title, String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait().map(String::trim).filter(s -> !s.isEmpty());
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
