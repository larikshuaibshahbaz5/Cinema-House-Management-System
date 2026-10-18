package cinema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class CinemaHouseManagementSystem {
    private final Scanner scanner = new Scanner(System.in);
    private final List<Movie> movies = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final List<UserAccount> users = new ArrayList<>();
    private final List<String> offers = new java.util.ArrayList<>(Arrays.asList("Weekend 2-for-1", "Student 20% off", "Family pack deal"));
    private final List<String> shopItems = Arrays.asList("Popcorn", "Soda", "Combo Pack", "Nachos");

    public CinemaHouseManagementSystem() {
        seedUsers();
        seedMovies();
    }

    public boolean login(String username, String password) {
        for (UserAccount user : users) {
            if (user.username.equalsIgnoreCase(username) && user.password.equals(password)) {
                return true;
            }
        }
        return false;
    }

    public String getRole(String username) {
        for (UserAccount user : users) {
            if (user.username.equalsIgnoreCase(username)) {
                return user.role;
            }
        }
        return "customer";
    }

    public void showAdminMenu(String username) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add movie");
            System.out.println("2. Update movie");
            System.out.println("3. Delete movie");
            System.out.println("4. View all movies");
            System.out.println("5. Search movie by name");
            System.out.println("6. Logout");
            System.out.print("Choose an option: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> addMovie();
                case 2 -> updateMovie();
                case 3 -> deleteMovie();
                case 4 -> viewAllMovies();
                case 5 -> searchMovieByName();
                case 6 -> {
                    running = false;
                    System.out.println("Logged out successfully.");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public void showCustomerMenu(String username) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Search movies by name");
            System.out.println("2. View all movies");
            System.out.println("3. View offers");
            System.out.println("4. View shop items");
            System.out.println("5. Book seats");
            System.out.println("6. Display available seats");
            System.out.println("7. Cancel booking");
            System.out.println("8. View my bookings");
            System.out.println("9. Logout");
            System.out.print("Choose an option: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> searchMovieByName();
                case 2 -> viewAllMovies();
                case 3 -> viewOffers();
                case 4 -> viewShop();
                case 5 -> bookSeats(username);
                case 6 -> displayAvailableSeats();
                case 7 -> cancelBooking(username);
                case 8 -> viewMyBookings(username);
                case 9 -> {
                    running = false;
                    System.out.println("Logged out successfully.");
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void seedUsers() {
        users.add(new UserAccount("admin", "admin", "admin"));
        users.add(new UserAccount("customer", "customer", "customer"));
    }

    private void seedMovies() {
        movies.add(new Movie("M001", "Inception", "Sci-Fi", 148, 12.50));
        movies.add(new Movie("M002", "The Matrix", "Action", 136, 10.00));
        movies.add(new Movie("M003", "Titanic", "Romance", 194, 9.50));
    }

    private void addMovie() {
        System.out.print("Movie ID: ");
        String id = scanner.nextLine();
        System.out.print("Movie name: ");
        String name = scanner.nextLine();
        System.out.print("Genre: ");
        String genre = scanner.nextLine();
        System.out.print("Duration (minutes): ");
        int duration = readInt();
        System.out.print("Ticket price: ");
        double price = readDouble();

        movies.add(new Movie(id, name, genre, duration, price));
        System.out.println("Movie added successfully.");
    }

    private void updateMovie() {
        System.out.print("Enter movie name to update: ");
        String name = scanner.nextLine();
        Movie movie = findMovieByName(name);
        if (movie == null) {
            System.out.println("Movie not found.");
            return;
        }

        System.out.print("New movie name (leave blank to keep current): ");
        String newName = scanner.nextLine();
        if (!newName.isBlank()) {
            movie.name = newName;
        }

        System.out.print("New genre (leave blank to keep current): ");
        String newGenre = scanner.nextLine();
        if (!newGenre.isBlank()) {
            movie.genre = newGenre;
        }

        System.out.print("New duration (leave blank to keep current): ");
        String durationInput = scanner.nextLine();
        if (!durationInput.isBlank()) {
            movie.duration = Integer.parseInt(durationInput);
        }

        System.out.print("New price (leave blank to keep current): ");
        String priceInput = scanner.nextLine();
        if (!priceInput.isBlank()) {
            movie.price = Double.parseDouble(priceInput);
        }

        System.out.println("Movie updated successfully.");
    }

    private void deleteMovie() {
        System.out.print("Enter movie name to delete: ");
        String name = scanner.nextLine();
        Movie movie = findMovieByName(name);
        if (movie == null) {
            System.out.println("Movie not found.");
            return;
        }
        movies.remove(movie);
        System.out.println("Movie deleted successfully.");
    }

    private void viewAllMovies() {
        if (movies.isEmpty()) {
            System.out.println("No movies available.");
            return;
        }
        System.out.println("\nAvailable movies:");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }

    private void searchMovieByName() {
        System.out.print("Enter movie name: ");
        String name = scanner.nextLine();
        Movie movie = findMovieByName(name);
        if (movie == null) {
            System.out.println("Movie not found.");
            return;
        }
        System.out.println(movie);
    }

    private void viewOffers() {
        System.out.println("\nCurrent offers:");
        for (String offer : offers) {
            System.out.println("- " + offer);
        }
    }

    private void viewShop() {
        System.out.println("\nShop items:");
        for (String item : shopItems) {
            System.out.println("- " + item);
        }
    }

    private void bookSeats(String username) {
        System.out.print("Enter movie name: ");
        String movieName = scanner.nextLine();
        Movie movie = findMovieByName(movieName);
        if (movie == null) {
            System.out.println("Movie not found.");
            return;
        }

        displaySeatMap(movieName);
        System.out.print("Enter seats to book (comma separated, e.g. A1,A2): ");
        String input = scanner.nextLine();
        List<String> requestedSeats = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (requestedSeats.isEmpty()) {
            System.out.println("No seats selected.");
            return;
        }

        Set<String> bookedSeats = bookings.stream()
                .filter(b -> b.movieName.equalsIgnoreCase(movieName))
                .flatMap(b -> b.seats.stream())
                .collect(Collectors.toSet());

        List<String> validSeats = new ArrayList<>();
        for (String seat : requestedSeats) {
            if (bookedSeats.contains(seat.toUpperCase())) {
                System.out.println("Seat " + seat + " is already booked.");
            } else {
                validSeats.add(seat.toUpperCase());
            }
        }

        if (validSeats.isEmpty()) {
            System.out.println("No seats were booked.");
            return;
        }

        bookings.add(new Booking(username, movieName, validSeats));
        System.out.println("Seats booked successfully for " + String.join(", ", validSeats));
    }

    private void displayAvailableSeats() {
        System.out.print("Enter movie name: ");
        String movieName = scanner.nextLine();
        Movie movie = findMovieByName(movieName);
        if (movie == null) {
            System.out.println("Movie not found.");
            return;
        }
        displaySeatMap(movieName);
    }

    private void displaySeatMap(String movieName) {
        List<String> seatLabels = Arrays.asList("A1", "A2", "A3", "A4", "A5", "B1", "B2", "B3", "B4", "B5");
        Set<String> bookedSeats = bookings.stream()
                .filter(b -> b.movieName.equalsIgnoreCase(movieName))
                .flatMap(b -> b.seats.stream())
                .collect(Collectors.toSet());

        System.out.println("\nSeats for " + movieName + ":");
        for (String seat : seatLabels) {
            String status = bookedSeats.contains(seat) ? "[Booked]" : "[Available]";
            System.out.print(seat + " " + status + "   ");
        }
        System.out.println();
    }

    private void cancelBooking(String username) {
        System.out.print("Enter movie name: ");
        String movieName = scanner.nextLine();
        List<Booking> userBookings = bookings.stream()
                .filter(b -> b.customerName.equalsIgnoreCase(username) && b.movieName.equalsIgnoreCase(movieName))
                .collect(Collectors.toList());

        if (userBookings.isEmpty()) {
            System.out.println("No booking found for this movie.");
            return;
        }

        System.out.println("Your booked seats: " + userBookings.get(0).seats);
        System.out.print("Enter seats to cancel (comma separated): ");
        String input = scanner.nextLine();
        List<String> requestedSeats = Arrays.stream(input.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Booking booking = userBookings.get(0);
        List<String> remainingSeats = booking.seats.stream()
                .filter(seat -> !requestedSeats.contains(seat))
                .collect(Collectors.toList());

        if (remainingSeats.isEmpty()) {
            bookings.remove(booking);
        } else {
            booking.seats = remainingSeats;
        }
        System.out.println("Booking update completed.");
    }

    private void viewMyBookings(String username) {
        List<Booking> userBookings = bookings.stream()
                .filter(b -> b.customerName.equalsIgnoreCase(username))
                .collect(Collectors.toList());
        if (userBookings.isEmpty()) {
            System.out.println("You have no bookings yet.");
            return;
        }
        System.out.println("\nYour bookings:");
        for (Booking booking : userBookings) {
            System.out.println("- " + booking.movieName + " -> " + booking.seats);
        }
    }

    public String getMovieListText() {
        if (movies.isEmpty()) {
            return "No movies available.";
        }
        return movies.stream()
                .map(Movie::toString)
                .collect(Collectors.joining("\n"));
    }

    public String getMovieDetails(String name) {
        Movie movie = findMovieByName(name);
        return movie == null ? null : movie.toString();
    }

    public Double getMoviePrice(String movieName) {
        Movie movie = findMovieByName(movieName);
        return movie == null ? null : movie.price;
    }

    public List<String> getOffers() {
        return offers;
    }

    public boolean addOffer(String offer) {
        if (offers.stream().anyMatch(existing -> existing.equalsIgnoreCase(offer))) {
            return false;
        }
        offers.add(offer);
        return true;
    }

    public boolean removeOffer(String offer) {
        return offers.removeIf(existing -> existing.equalsIgnoreCase(offer));
    }

    public List<String> getShopItems() {
        return shopItems;
    }

    public List<String> getAvailableSeatsForUI(String movieName, List<String> seatRequests) {
        Movie movie = findMovieByName(movieName);
        if (movie == null) {
            return null;
        }

        Set<String> bookedSeats = bookings.stream()
                .filter(b -> b.movieName.equalsIgnoreCase(movieName))
                .flatMap(b -> b.seats.stream())
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        return seatRequests.stream()
                .map(String::toUpperCase)
                .filter(s -> !s.isEmpty())
                .distinct()
                .filter(s -> !bookedSeats.contains(s))
                .collect(Collectors.toList());
    }

    public void addBooking(String username, String movieName, List<String> seats) {
        if (!seats.isEmpty()) {
            bookings.add(new Booking(username, movieName, seats));
        }
    }

    public List<String> getUserBookingSummaries(String username) {
        return bookings.stream()
                .filter(b -> b.customerName.equalsIgnoreCase(username))
                .map(b -> b.movieName + " -> " + b.seats)
                .collect(Collectors.toList());
    }

    public boolean addMovie(String id, String name, String genre, int duration, double price) {
        if (findMovieByName(name) != null) {
            return false;
        }
        movies.add(new Movie(id, name, genre, duration, price));
        return true;
    }

    public boolean updateMovieData(String currentName, String newName, String newGenre, Integer newDuration, Double newPrice) {
        Movie movie = findMovieByName(currentName);
        if (movie == null) {
            return false;
        }
        if (!newName.isBlank()) {
            movie.name = newName;
        }
        if (!newGenre.isBlank()) {
            movie.genre = newGenre;
        }
        if (newDuration != null) {
            movie.duration = newDuration;
        }
        if (newPrice != null) {
            movie.price = newPrice;
        }
        return true;
    }

    public boolean deleteMovieByName(String name) {
        Movie movie = findMovieByName(name);
        if (movie == null) {
            return false;
        }
        movies.remove(movie);
        return true;
    }

    public List<String> bookSeatsForUI(String username, String movieName, List<String> seatRequests) {
        Movie movie = findMovieByName(movieName);
        if (movie == null) {
            return null;
        }

        Set<String> bookedSeats = bookings.stream()
                .filter(b -> b.movieName.equalsIgnoreCase(movieName))
                .flatMap(b -> b.seats.stream())
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        List<String> validSeats = seatRequests.stream()
                .map(String::toUpperCase)
                .filter(s -> !s.isEmpty())
                .distinct()
                .filter(s -> !bookedSeats.contains(s))
                .collect(Collectors.toList());

        if (!validSeats.isEmpty()) {
            bookings.add(new Booking(username, movieName, validSeats));
        }
        return validSeats;
    }

    public boolean cancelSeatsForUI(String username, String movieName, List<String> seatRequests) {
        List<Booking> userBookings = bookings.stream()
                .filter(b -> b.customerName.equalsIgnoreCase(username) && b.movieName.equalsIgnoreCase(movieName))
                .collect(Collectors.toList());

        if (userBookings.isEmpty()) {
            return false;
        }

        Booking booking = userBookings.get(0);
        List<String> remainingSeats = booking.seats.stream()
                .filter(seat -> !seatRequests.contains(seat.toUpperCase()))
                .collect(Collectors.toList());

        if (remainingSeats.isEmpty()) {
            bookings.remove(booking);
        } else {
            booking.seats = remainingSeats;
        }
        return true;
    }

    public String getSeatMap(String movieName) {
        Movie movie = findMovieByName(movieName);
        if (movie == null) {
            return "Movie not found.";
        }
        List<String> seatLabels = Arrays.asList("A1", "A2", "A3", "A4", "A5", "B1", "B2", "B3", "B4", "B5");
        Set<String> bookedSeats = bookings.stream()
                .filter(b -> b.movieName.equalsIgnoreCase(movieName))
                .flatMap(b -> b.seats.stream())
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        return seatLabels.stream()
                .map(seat -> seat + " " + (bookedSeats.contains(seat) ? "[Booked]" : "[Available]"))
                .collect(Collectors.joining("   "));
    }

    private Movie findMovieByName(String name) {
        for (Movie movie : movies) {
            if (movie.name.equalsIgnoreCase(name)) {
                return movie;
            }
        }
        return null;
    }

    private int readInt() {
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double readDouble() {
        String input = scanner.nextLine();
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static class UserAccount {
        private final String username;
        private final String password;
        private final String role;

        private UserAccount(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }

    private static class Movie {
        private final String id;
        private String name;
        private String genre;
        private int duration;
        private double price;

        private Movie(String id, String name, String genre, int duration, double price) {
            this.id = id;
            this.name = name;
            this.genre = genre;
            this.duration = duration;
            this.price = price;
        }

        @Override
        public String toString() {
            return id + " | " + name + " | " + genre + " | " + duration + " mins | $" + price;
        }
    }

    private static class Booking {
        private final String customerName;
        private final String movieName;
        private List<String> seats;

        private Booking(String customerName, String movieName, List<String> seats) {
            this.customerName = customerName;
            this.movieName = movieName;
            this.seats = seats;
        }
    }
}
