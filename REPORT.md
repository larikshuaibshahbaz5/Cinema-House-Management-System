Introduction
- Cinema House is a compact desktop application built with Java and JavaFX that helps small cinemas manage their movie inventory, bookings, offers, and shop items.
- It provides a role-based interface: administrators can manage movies and offers, while customers or guests can view movies and make bookings.
- Designed as a demo/learning project, it uses simple in-memory storage and dialog-driven UI flows.

Cinema House — Presentation Report

Slide 1 — Title
- Cinema House Management System
- Desktop Java + JavaFX application
- Role-based UI: Admin, Customer/Guest

Slide 2 — Purpose & Audience
- Purpose: Manage movie inventory, bookings, offers, and shop items.
- Audience: Small cinema operators, demo/study projects, instructors or trainees learning Java/JavaFX.

Slide 3 — High-level Flow (user view)
1. Launch app -> Login screen with logo.
2. Login as `admin` (full control) or `customer`/continue as Guest.
3. Dashboard shows role-appropriate actions:
   - Admin: Add / Update / Delete movies, Manage offers.
   - Customer/Guest: View/Search movies, Book seats, Cancel bookings, View shop items.
4. Use dialog prompts for inputs (movie name, seats, payments). Logout returns to login.

Slide 4 — Key Components
- `Main.java` — JavaFX UI: login scene, dashboard scene, dialogs, logo handling.
- `CinemaHouseManagementSystem.java` — Core logic and in-memory data: users, movies, bookings, offers, shop items.
- `compile-javafx.bat` and `run-javafx.bat` — Build/run helper scripts for Windows.
- `Cinema Logo.jpg` — App logo shown on login.

Slide 5 — Libraries, Keywords, Arrays and How They Work
- Libraries used (what each does and how it's used):
   - Java core (`java.util`):
      - `List` / `ArrayList`: store ordered collections such as `movies`, `bookings`, `offers`, and `shopItems`.
      - `Arrays` / `Arrays.asList(...)`: create small fixed lists (used to seed `offers` and `seatLabels`).
      - `Optional`: wrap results from dialog inputs to avoid null checks when users cancel or submit empty values.
      - `Set`: used to collect booked seats for quick membership checks (no duplicates).
      - Stream API (`stream()`, `filter()`, `map()`, `collect()`): concise processing of collections for searching, filtering, and mapping data.
   - Java I/O (`java.io.File`): represent the `Cinema Logo.jpg` path and load the image into the UI at runtime.
   - JavaFX core and UI (`javafx.*`):
      - `javafx.application.Application`: base class for JavaFX apps; `Main` extends it to launch the UI.
      - `Stage` and `Scene`: `Stage` is the application window; `Scene` holds the UI content shown in the window.
      - Controls (`Button`, `Label`, `TextField`, `PasswordField`, `TextArea`): provide interactive widgets for login, input, and display.
      - Dialogs (`TextInputDialog`, `Alert`): collect single-line input and show information/warning/error messages to the user.
      - Layouts (`BorderPane`, `VBox`, `GridPane`): arrange controls on screen (side menu, center content, stacked vertical elements).
      - Images (`Image`, `ImageView`): load and display the logo on the login screen.
   - Build/run helpers:
      - The batch scripts (`compile-javafx.bat`, `run-javafx.bat`) rely on JavaFX SDK jars placed in the `lib/` folder; these jars are added to the `--module-path` when compiling and running so JavaFX classes are available.

- Important keywords and strings (how they are used):
   - `"admin"` and `"customer"` — default usernames/roles seeded in `seedUsers()`; used to toggle admin controls and permissions.
   - `"add"`, `"delete"`, `"list"` — action keywords used in the `manageOffers()` input and switch logic.
   - `"Cinema Logo.jpg"` — filename used by `Main.createLogoView()` to load the app logo at runtime.
   - Dialog titles and messages (e.g., `"Login"`, `"Add Movie"`, `"Book Seats"`) — textual keys used consistently in prompts and alerts for user feedback.

- String arrays / lists and roles in the code:
   - `offers` (List<String>) — initialized with `Arrays.asList(...)` in `CinemaHouseManagementSystem` and exposed via `getOffers()`. Methods `addOffer()` and `removeOffer()` manipulate this list while avoiding duplicates.
   - `shopItems` (List<String>) — static list of shop item names (Popcorn, Soda, etc.) shown by the UI when requested.
   - `seatLabels` (List<String>) — a fixed list like `A1..A5, B1..B5` used by `displaySeatMap()` and `getSeatMap()` to render seats and determine availability.
   - User-related strings — usernames and passwords are stored in `UserAccount` objects for authentication via `login()`.

- How these pieces work together (brief):
   - The UI (`Main`) delegates data and operations to `CinemaHouseManagementSystem` rather than manipulating data directly. For example, when booking, `Main.bookSeats()` collects user input and calls `system.getAvailableSeatsForUI(...)` and `system.addBooking(...)`.
   - Seat availability logic: booking records (`bookings`) are scanned to create a `Set` of booked seats for a movie; requests are normalized, filtered against booked seats, and the remaining seats are returned as available.
   - Offer management uses a simple case-insensitive check to avoid duplicates when adding (`offers.stream().anyMatch(...)`) and uses `removeIf(...)` when deleting.
   - The app uses `TextInputDialog` and `Alert` to interact with users; these dialog-based flows centralize input validation and error messaging in `Main` while the system class performs the business rules.

Slide 6 — Definitions (short & simple)
- Java `List` / `ArrayList`: a resizable collection that stores items in order. Used to keep movies, bookings, offers, and shop items.
- `Arrays.asList(...)`: a quick way to make a `List` from fixed items (used for initial offers and seat labels).
- `Optional<String>`: a wrapper that may or may not hold a value; used for user dialog results to avoid nulls.
- `Set`: an unordered collection that doesn't allow duplicates; used to collect booked seats for quick lookup.
- `Stream API`: a way to process collections (filter, map, collect) concisely and efficiently.
- `File`: represents a filesystem path; here used to load `Cinema Logo.jpg` at runtime.
- JavaFX `Scene` / `Stage`: `Stage` is the app window; `Scene` is the content inside the window.
- JavaFX controls (`Button`, `Label`, `TextField`, `PasswordField`, `TextArea`): UI widgets users interact with.
- `TextInputDialog`: a popup dialog that asks the user for a single line of text.
- `Alert`: a popup that shows information, warnings, or errors to the user.
- `BorderPane`, `VBox`, `GridPane`: layout containers that arrange UI controls in the window.
- Keyword `"admin"` / role string: indicates an administrator account and enables admin-only controls.
- Keyword `"customer"` / role string: indicates a regular user and shows customer features.
- `offers` (List<String>): stores promo offer descriptions; `addOffer()` prevents duplicates.
- `shopItems` (List<String>): static list of items sold at the cinema's shop.
- `seatLabels` (List<String>): fixed seat identifiers (e.g., A1, A2) used to display seat maps.
- `bookings` (List<Booking>): stores booking records; each booking contains username, movie, and seats.

Slide 6 — Data Model (simplified)
- Movie: id, name, genre, duration, price.
- Booking: customerName, movieName, List<String> seats.
- UserAccount: username, password, role.
- Offers & shop items: simple String lists.

Slide 6 — How booking works (step-by-step)
1. Customer selects movie and enters seats (e.g., "A1,A2").
2. System checks booked seats for that movie and returns only available seats.
3. Total due calculated from movie price * seats.
4. Payment amount entered via prompt. On success, booking recorded in memory.
5. Seat map and bookings update immediately in the UI.

Slide 6 — How to add an image in JavaFX
- Place the image file in the project folder or in a dedicated `images` folder.
- Use JavaFX classes `Image` and `ImageView` to load and display the image.
- Example:
  - `Image image = new Image(new FileInputStream("images/Cinema Logo.jpg"));`
  - `ImageView imageView = new ImageView(image);`
- Add the image to the scene or layout container using `pane.getChildren().add(imageView);` or `box.getChildren().add(imageView);`.
- If the image does not appear, check the file name, path, and whether the image file is available at runtime.
- This method is useful for adding logos, background pictures, or promotional images to the application.

Slide 7 — Implementation details
- UI uses JavaFX controls, dialogs, and layouts (BorderPane, VBox).
- Backend stores data in ArrayLists (in-memory). No database.
- Initial data seeded via `seedMovies()` and `seedUsers()`.
- Seat map is a fixed small set (A1..A5, B1..B5) — easy to extend.

Slide 8 — Limitations & Assumptions
- Plaintext passwords in memory (not secure). Use hashing for production.
- No persistence: restarting the app resets data.
- Single-user desktop app; no concurrent access handling.
- Static seat layout; not configurable at runtime.

Slide 9 — How to build & run (Windows) — detailed

Prerequisites
- Java 11 or newer installed and on `PATH`.
- JavaFX SDK jars downloaded and placed into the project `lib/` folder (the batch scripts expect the jars there).

What the scripts do
- `compile-javafx.bat`: compiles the `src` Java files to `out` using the Java compiler and the JavaFX module path. It creates the `out` classes used at runtime.
- `run-javafx.bat`: runs the compiled `Main` class with the Java launcher and sets `--module-path` to `lib` and `--add-modules` to `javafx.controls,javafx.fxml` so JavaFX is available at runtime.

Step-by-step (commands)
1. Open a PowerShell terminal in the project root (where `compile-javafx.bat` is located).
2. Compile sources:
```powershell
.\compile-javafx.bat
```
3. Run the app:
```powershell
.\run-javafx.bat
```

Common compile/run issues & fixes
- "JavaFX lib folder not found": ensure `lib/` exists and contains JavaFX `.jar` files. Example contents: `javafx-base.jar`, `javafx-controls.jar`, `javafx-graphics.jar`, `javafx-fxml.jar` (names vary by SDK version).
- "module not found" or runtime errors: confirm Java version is compatible with the JavaFX SDK you downloaded (JavaFX built for Java 11+). If using a JDK that bundles JavaFX differently, adjust `--module-path` accordingly.
- If you prefer running from an IDE (IntelliJ/VS Code): add JavaFX libraries to the project classpath/module path and run `Main` as a JavaFX application.


Slide 10 — Demo checklist
- Launch app, show login screen and logo.
- Login as `admin` and add a movie.
- Login as `customer` or Continue as Guest and book seats.
- Show seat map and cancel booking flow.

Slide 11 — Contact / Next steps
- For enhancements, start with persistence and security.
- I can implement the first improvement (persist movies + bookings) if you want.

Conclusion
- Cinema House demonstrates a complete desktop application concept that combines JavaFX UI design, role-based access, movie management, seat booking, offers, and shop-item handling in one simple system.
- The project highlights core programming concepts such as classes, collections, lists, sets, user interaction through dialogs, and basic data flow between the interface and logic layer.
- It serves as a strong learning example for beginners in Java, JavaFX, and object-oriented application development, while also showing a clear path for future improvements such as database integration and stronger security.

