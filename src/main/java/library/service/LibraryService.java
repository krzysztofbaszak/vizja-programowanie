package library.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import library.db.ItemDao;
import library.db.LibrarianDao;
import library.db.UserDao;
import library.model.Item;
import library.model.Librarian;
import library.model.User;
import library.util.ScreenUtil;

/**
 * Serwis biblioteczny zarządzający logiką biznesową aplikacji bibliotecznej.
 * Odpowiada za interakcję użytkownika z bazą danych i realizację operacji na zasobach,
 * takich jak książki, użytkownicy oraz bibliotekarze.
 */
public class LibraryService {

    /**
     * Wyświetla tabelę bibliotekarzy z ich danymi.
     * 
     * @param librarians lista bibliotekarzy do wyświetlenia
     */
    public void printLibrarianTable(List<Librarian> librarians) {
        System.out.println("\n== Lista bibliotekarzy ==");
        System.out.printf("%-15s | %-25s | %-10s\n", "Login", "Imię i nazwisko", "Wynagrodzenie");
        System.out.println("--------------------------------------------------------------");
        for (Librarian l : librarians) {
            System.out.printf("%-15s | %-25s | %10d\n", l.getUsername(), l.getFullName(), l.getSalary());
        }
        System.out.println();
    }

    /**
     * Wyświetla tabelę użytkowników.
     * 
     * @param users lista użytkowników do wyświetlenia
     */
    public void printUserTable(List<User> users) {
        System.out.println("\n== Lista użytkowników ==");
        System.out.printf("%-15s | %-25s\n", "Login", "Imię i nazwisko");
        System.out.println("-------------------------------------------");
        for (User u : users) {
            System.out.printf("%-15s | %-25s\n", u.getUsername(), u.getFullName());
        }
        System.out.println();
    }

    /**
     * Wyświetla katalog pozycji bibliotecznych (książek, magazynów).
     */
    public void printItemsTable() {
        List<Item> items = ItemDao.getAll();
        System.out.println("\n== Katalog pozycji bibliotecznych ==");
        System.out.printf("%-36s | %-30s | %-6s | %-12s | %-20s | %-20s | %-20s\n",
                "UUID", "Tytuł", "Rok", "Status", "Wypożyczający", "Autor", "Gatunek/Nr Wydania");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (Item i : items) {
            String status = i.isAvailable() ? "DOSTĘPNA" : "WYPOŻYCZONA";
            String borrower = i.getLoanedTo() == null ? "-" : i.getLoanedTo();
            String author = "-";
            String genreOrIssue = "-";
            if (i instanceof library.model.Book) {
                library.model.Book b = (library.model.Book) i;
                author = b.getAuthor();
                genreOrIssue = b.getGenre();
            } else if (i instanceof library.model.Magazine) {
                library.model.Magazine m = (library.model.Magazine) i;
                genreOrIssue = "Nr: " + m.getIssueNumber();
            }
            System.out.printf("%-36s | %-30s | %-6d | %-12s | %-20s | %-20s | %-20s\n",
                    i.getUuid().toString(), i.getTitle(), i.getYear(), status, borrower, author, genreOrIssue);
        }
        System.out.println();
    }

    /**
     * Interaktywnie dodaje nową książkę do biblioteki poprzez konsolę.
     * Użytkownik może anulować operację wpisując 'q' w dowolnym momencie.
     */
    public void addBookInteractive() {
        printItemsTable();
        Scanner sc = new Scanner(System.in);
        System.out.println("== DODAWANIE NOWEJ KSIĄŻKI ==");
        System.out.println("Wpisz 'q' w dowolnym momencie, aby anulować operację.");

        System.out.print("Tytuł: ");
        String title = sc.nextLine();
        if (title.equalsIgnoreCase("q")) return;

        System.out.print("Autor: ");
        String author = sc.nextLine();
        if (author.equalsIgnoreCase("q")) return;

        System.out.print("Rok wydania: ");
        String yearStr = sc.nextLine();
        if (yearStr.equalsIgnoreCase("q")) return;
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            System.out.println("Nieprawidłowy rok. Anulowano dodawanie.");
            return;
        }

        System.out.print("Gatunek: ");
        String genre = sc.nextLine();
        if (genre.equalsIgnoreCase("q")) return;

        System.out.println("\n--- Podsumowanie nowej książki ---");
        System.out.printf("Tytuł:   %s\n", title);
        System.out.printf("Autor:   %s\n", author);
        System.out.printf("Rok:     %d\n", year);
        System.out.printf("Gatunek: %s\n", genre);

        System.out.print("Czy dodać tę książkę? (t/n): ");
        String confirm = sc.nextLine();
        if (!confirm.trim().equalsIgnoreCase("t")) {
            System.out.println("Anulowano dodawanie książki.");
            return;
        }

        ItemDao.addBook(title, author, year, genre);
        System.out.println("Dodano książkę!");
    }

    /**
     * Interaktywne wypożyczenie pozycji bibliotecznej.
     * Jeśli wywołujący jest bibliotekarzem, może wskazać użytkownika, dla którego wypożycza.
     * 
     * @param currentUsername aktualny login użytkownika wywołującego
     * @param isLibrarian flaga czy wywołujący jest bibliotekarzem
     */
    public void borrowItemInteractive(String currentUsername, boolean isLibrarian) {
        printItemsTable();
        Scanner sc = new Scanner(System.in);

        String targetUser = currentUsername;
        if (isLibrarian) {
            System.out.println("Wpisz 'q' aby anulować operację.");
            System.out.print("Czy chcesz wypożyczyć książkę jako Ty (" + currentUsername + ")? (t/n/q): ");
            String ans = sc.nextLine().trim();
            if (ans.equalsIgnoreCase("q")) return;

            if (ans.equalsIgnoreCase("n")) {
                List<User> users = UserDao.getAllUsers();
                if (users.isEmpty()) {
                    System.out.println("Brak użytkowników w systemie!");
                    return;
                }
                printUserTable(users);
                System.out.print("Podaj login użytkownika, na którego ma być wypożyczenie (q = anuluj): ");
                String userLogin = sc.nextLine().trim();
                if (userLogin.equalsIgnoreCase("q")) return;
                if (users.stream().noneMatch(u -> u.getUsername().equals(userLogin))) {
                    System.out.println("Nie znaleziono takiego użytkownika.");
                    return;
                }
                targetUser = userLogin;
            }
        }

        System.out.print("Podaj UUID książki do wypożyczenia (q = anuluj): ");
        String uuid = sc.nextLine();
        if (uuid.equalsIgnoreCase("q")) return;

        boolean success = ItemDao.borrowItem(uuid, targetUser);
        if (success) {
            System.out.println("Wypożyczono książkę użytkownikowi: " + targetUser + "!");
        } else {
            System.out.println("Nie udało się wypożyczyć – książka nie istnieje lub jest już wypożyczona.");
        }
    }

    /**
     * Interaktywnie przyjmuje zwrot książki.
     */
    public void returnItemInteractive() {
        ScreenUtil.clearScreen();
        printItemsTable();
        Scanner sc = new Scanner(System.in);
        System.out.println("Wpisz 'q' aby anulować operację.");
        System.out.print("Podaj UUID książki do zwrotu: ");
        String uuid = sc.nextLine();
        if (uuid.equalsIgnoreCase("q")) return;

        boolean success = ItemDao.returnItem(uuid);
        if (success) {
            System.out.println("Przyjęto zwrot!");
        } else {
            System.out.println("Nie udało się przyjąć zwrotu (może książka już jest dostępna?).");
        }
    }

    /**
     * Interaktywnie usuwa pozycję biblioteczną po podaniu UUID.
     */
    public void removeItemInteractive() {
        printItemsTable();
        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj UUID pozycji do usunięcia ('q' - anuluj): ");
        String uuidStr = sc.nextLine();
        if (uuidStr.equalsIgnoreCase("q")) return;
        try {
            UUID uuid = UUID.fromString(uuidStr);
            ItemDao.delete(uuid);
            System.out.println("Usunięto pozycję!");
        } catch (Exception e) {
            System.out.println("Nieprawidłowy UUID.");
        }
    }

    /**
     * Interaktywnie dodaje nowego użytkownika do systemu.
     */
    public void addUserInteractive() {
        List<User> users = UserDao.getAllUsers();
        if (!users.isEmpty()) {
            printUserTable(users);
        }
        Scanner sc = new Scanner(System.in);
        System.out.print("Login użytkownika: ");
        String username = sc.nextLine();
        System.out.print("Imię i nazwisko: ");
        String fullname = sc.nextLine();
        System.out.print("Hasło: ");
        String password = sc.nextLine();
        UserDao.addUser(username, fullname, password);
        System.out.println("Dodano użytkownika!");
    }

    /**
     * Interaktywnie edytuje istniejącego użytkownika.
     */
    public void editUserInteractive() {
        Scanner sc = new Scanner(System.in);
        List<User> users = UserDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Brak użytkowników do edycji!");
            return;
        }
        printUserTable(users);

        System.out.print("Login użytkownika do edycji (q aby anulować): ");
        String username = sc.nextLine().trim();
        if (username.equalsIgnoreCase("q")) return;

        User oldUser = users.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
        if (oldUser == null) {
            System.out.println("Niepoprawny login!");
            return;
        }
        String fullname = oldUser.getFullName();
        String password = oldUser.getPassword();

        System.out.print("Czy zmienić imię i nazwisko? (t/n/q): ");
        String ans = sc.nextLine().trim();
        if (ans.equalsIgnoreCase("q")) return;
        if (ans.equalsIgnoreCase("t")) {
            System.out.print("Nowe imię i nazwisko (q aby anulować): ");
            fullname = sc.nextLine().trim();
            if (fullname.equalsIgnoreCase("q")) return;
        }
        System.out.print("Czy zmienić hasło? (t/n/q): ");
        ans = sc.nextLine().trim();
        if (ans.equalsIgnoreCase("q")) return;
        if (ans.equalsIgnoreCase("t")) {
            System.out.print("Nowe hasło (q aby anulować): ");
            password = sc.nextLine().trim();
            if (password.equalsIgnoreCase("q")) return;
        }

        System.out.println("\n--- Podsumowanie ---");
        System.out.printf("Przed: %s | %s\n", oldUser.getUsername(), oldUser.getFullName());
        System.out.printf("Po:    %s | %s\n", oldUser.getUsername(), fullname);
        System.out.print("Czy zapisać zmiany? (t/n/q): ");
        ans = sc.nextLine().trim();
        if (ans.equalsIgnoreCase("q") || !ans.equalsIgnoreCase("t")) {
            System.out.println("Anulowano edycję.");
            return;
        }
        UserDao.editUser(username, fullname, password);
        System.out.println("Edytowano użytkownika!");
    }

    /**
     * Interaktywnie usuwa użytkownika z systemu.
     */
    public void removeUserInteractive() {
        Scanner sc = new Scanner(System.in);
        List<User> users = UserDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Brak użytkowników do usunięcia!");
            return;
        }
        printUserTable(users);

        System.out.print("Login użytkownika do usunięcia: ");
        String username = sc.nextLine().trim();
        if (username.isEmpty() || users.stream().noneMatch(u -> u.getUsername().equals(username))) {
            System.out.println("Niepoprawny login!");
            return;
        }
        UserDao.removeUser(username);
        System.out.println("Usunięto użytkownika!");
    }

    /**
     * Interaktywnie dodaje nowego bibliotekarza.
     */
    public void addLibrarianInteractive() {
        List<Librarian> librarians = LibrarianDao.getAllLibrarians();
        if (!librarians.isEmpty()) {
            printLibrarianTable(librarians);
        }
        Scanner sc = new Scanner(System.in);
        System.out.print("Login bibliotekarza: ");
        String username = sc.nextLine();
        if (username.equalsIgnoreCase("q")) return;
        System.out.print("Imię i nazwisko: ");
        String fullname = sc.nextLine();
        if (fullname.equalsIgnoreCase("q")) return;
        System.out.print("Hasło: ");
        String password = sc.nextLine();
        if (password.equalsIgnoreCase("q")) return;

        int salary = 0;
        while (true) {
            System.out.print("Wynagrodzenie: ");
            String salaryStr = sc.nextLine().trim();
            if (salaryStr.equalsIgnoreCase("q")) return;
            try {
                salary = Integer.parseInt(salaryStr);
                if (salary < 0) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                System.out.println("Wynagrodzenie musi być liczbą całkowitą >= 0!");
            }
        }

        System.out.println("\n--- Podsumowanie nowego bibliotekarza ---");
        System.out.printf("Login: %s\nImię i nazwisko: %s\nHasło: %s\nWynagrodzenie: %d\n",
            username, fullname, password, salary);
        System.out.print("Czy dodać tego bibliotekarza? (t/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("t")) {
            System.out.println("Anulowano dodawanie bibliotekarza.");
            return;
        }

        LibrarianDao.addLibrarian(username, fullname, password, salary);
        System.out.println("Dodano bibliotekarza!");
    }

    /**
     * Interaktywnie edytuje bibliotekarza.
     */
    public void editLibrarianInteractive() {
        List<Librarian> librarians = LibrarianDao.getAllLibrarians();
        if (librarians.isEmpty()) {
            System.out.println("Brak bibliotekarzy do edycji!");
            return;
        }
        printLibrarianTable(librarians);

        Scanner sc = new Scanner(System.in);
        System.out.print("Login bibliotekarza do edycji: ");
        String username = sc.nextLine().trim();
        if (username.equalsIgnoreCase("q")) return;
        Librarian oldLibrarian = librarians.stream()
                .filter(l -> l.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        if (oldLibrarian == null) {
            System.out.println("Niepoprawny login!");
            return;
        }

        String fullname = oldLibrarian.getFullName();
        String password = oldLibrarian.getPassword();
        int salary = oldLibrarian.getSalary();

        System.out.print("Czy zmienić imię i nazwisko? (t/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("t")) {
            System.out.print("Nowe imię i nazwisko: ");
            fullname = sc.nextLine().trim();
            if (fullname.equalsIgnoreCase("q")) return;
        }
        System.out.print("Czy zmienić hasło? (t/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("t")) {
            System.out.print("Nowe hasło: ");
            password = sc.nextLine().trim();
            if (password.equalsIgnoreCase("q")) return;
        }
        System.out.print("Czy zmienić wynagrodzenie? (t/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("t")) {
            while (true) {
                System.out.print("Nowe wynagrodzenie: ");
                String salaryStr = sc.nextLine().trim();
                if (salaryStr.equalsIgnoreCase("q")) return;
                try {
                    int newSalary = Integer.parseInt(salaryStr);
                    if (newSalary < 0) throw new NumberFormatException();
                    salary = newSalary;
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Wynagrodzenie musi być liczbą całkowitą >= 0!");
                }
            }
        }

        System.out.println("\n--- Podsumowanie zmian ---");
        System.out.printf("Przed: %s | %s | %s | %d\n",
                oldLibrarian.getUsername(), oldLibrarian.getFullName(), oldLibrarian.getPassword(), oldLibrarian.getSalary());
        System.out.printf("Po:    %s | %s | %s | %d\n",
                oldLibrarian.getUsername(), fullname, password, salary);

        System.out.print("Czy zapisać zmiany? (t/n): ");
        if (sc.nextLine().trim().equalsIgnoreCase("t")) {
            LibrarianDao.editLibrarian(username, fullname, password, salary);
            System.out.println("Edytowano bibliotekarza!");
        } else {
            System.out.println("Anulowano edycję.");
        }
    }

    /**
     * Interaktywnie usuwa bibliotekarza.
     */
    public void removeLibrarianInteractive() {
        List<Librarian> librarians = LibrarianDao.getAllLibrarians();
        if (librarians.isEmpty()) {
            System.out.println("Brak bibliotekarzy do usunięcia!");
            return;
        }
        printLibrarianTable(librarians);

        Scanner sc = new Scanner(System.in);
        System.out.print("Login bibliotekarza do usunięcia: ");
        String username = sc.nextLine().trim();
        if (username.equalsIgnoreCase("q")) return;
        if (username.isEmpty() || librarians.stream().noneMatch(l -> l.getUsername().equals(username))) {
            System.out.println("Niepoprawny login!");
            return;
        }

        System.out.printf("Czy na pewno usunąć bibliotekarza %s? (t/n): ", username);
        if (!sc.nextLine().trim().equalsIgnoreCase("t")) {
            System.out.println("Anulowano usunięcie.");
            return;
        }

        LibrarianDao.removeLibrarian(username);
        System.out.println("Usunięto bibliotekarza!");
    }

    /**
     * Menu interaktywnego importu i eksportu książek w formatach binarnym i tekstowym.
     */
    public void importExportBooksInteractive() {
        Scanner sc = new Scanner(System.in);
        System.out.println("1. Import książek");
        System.out.println("2. Eksport książek");
        System.out.print("Wybierz opcję: ");
        String op = sc.nextLine().trim();

        switch (op) {
            case "1": 
                System.out.println("Format:\n1. BINARNY\n2. TEKSTOWY (CSV)");
                System.out.print("Wybierz format: ");
                String importFormat = sc.nextLine().trim();
                switch (importFormat) {
                    case "1":
                        System.out.print("Podaj ścieżkę pliku: ");
                        String binImportPath = sc.nextLine();
                        library.util.SerializationUtil.importFromBin(binImportPath);
                        break;
                    case "2":
                        System.out.print("Podaj ścieżkę pliku: ");
                        String txtImportPath = sc.nextLine();
                        library.util.SerializationUtil.importFromTxt(txtImportPath);
                        break;
                    default:
                        System.out.println("Nieznany format!");
                }
                break;
            case "2": 
                System.out.println("Format:\n1. BINARNY\n2. TEKSTOWY (CSV)");
                System.out.print("Wybierz format: ");
                String exportFormat = sc.nextLine().trim();
                switch (exportFormat) {
                    case "1":
                        System.out.print("Podaj ścieżkę pliku: ");
                        String binExportPath = sc.nextLine();
                        library.util.SerializationUtil.exportToBin(binExportPath);
                        break;
                    case "2":
                        System.out.print("Podaj ścieżkę pliku: ");
                        String txtExportPath = sc.nextLine();
                        library.util.SerializationUtil.exportToTxt(txtExportPath);
                        break;
                    default:
                        System.out.println("Nieznany format!");
                }
                break;
            default:
                System.out.println("Nieznana opcja!");
        }
    }

    /**
     * Import użytkowników z pliku (binarnie lub tekstowo).
     */
    @SuppressWarnings("unchecked")
    public void importUsersInteractive() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Import użytkowników: 1. BINARNIE  2. TEKSTOWO");
        System.out.print("Wybierz format (1/2): ");
        String format = sc.nextLine();
        System.out.print("Podaj ścieżkę pliku: ");
        String path = sc.nextLine();

        switch (format) {
            case "1":
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
                    List<User> users = (List<User>) ois.readObject();
                    int added = 0, skipped = 0;
                    for (User u : users) {
                        if (UserDao.getByUsername(u.getUsername()).isEmpty()) {
                            UserDao.addUser(u.getUsername(), u.getFullName(), u.getPassword());
                            added++;
                        } else {
                            System.out.println("Użytkownik " + u.getUsername() + " już istnieje, pomijam.");
                            skipped++;
                        }
                    }
                    System.out.printf("Zaimportowano %d nowych użytkowników (pominięto %d istniejących) z pliku binarnego: %s\n",
                            added, skipped, path);
                } catch (Exception e) {
                    System.out.println("Błąd importu: " + e.getMessage());
                }
                break;

            case "2":
                try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                    String line = br.readLine(); 
                    int added = 0, skipped = 0;
                    while ((line = br.readLine()) != null) {
                        String[] data = line.split(";");
                        if (data.length >= 2) {
                            String username = data[0];
                            String fullname = data[1];
                            String password = data.length >= 3 ? data[2] : ""; 

                            if (UserDao.getByUsername(username).isEmpty()) {
                                UserDao.addUser(username, fullname, password);
                                added++;
                            } else {
                                System.out.println("Użytkownik " + username + " już istnieje, pomijam.");
                                skipped++;
                            }
                        }
                    }
                    System.out.printf("Zaimportowano %d nowych użytkowników (pominięto %d istniejących) z pliku tekstowego: %s\n",
                            added, skipped, path);
                } catch (Exception e) {
                    System.out.println("Błąd importu: " + e.getMessage());
                }
                break;

            default:
                System.out.println("Nieznany format!");
        }
    }

    /**
     * Eksport użytkowników do pliku (binarnie lub tekstowo).
     */
    public void exportUsersInteractive() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Eksport użytkowników: 1. BINARNIE  2. TEKSTOWO");
        System.out.print("Wybierz format (1/2): ");
        String format = sc.nextLine();
        System.out.print("Podaj ścieżkę pliku: ");
        String path = sc.nextLine();

        List<User> users = UserDao.getAllUsers();
        switch (format) {
            case "1":
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                    oos.writeObject(users);
                    System.out.println("Wyeksportowano użytkowników binarnie.");
                } catch (Exception e) {
                    System.out.println("Błąd eksportu: " + e.getMessage());
                }
                break;
            case "2":
                try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
                    pw.println("Login;Imię i nazwisko");
                    for (User u : users) {
                        pw.printf("%s;%s\n", u.getUsername(), u.getFullName());
                    }
                    System.out.println("Wyeksportowano użytkowników tekstowo.");
                } catch (Exception e) {
                    System.out.println("Błąd eksportu: " + e.getMessage());
                }
                break;
            default:
                System.out.println("Nieznany format!");
        }
    }

    /**
     * Eksport wypożyczeń do pliku CSV.
     */
    public void exportLoansInteractive() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Eksport wypożyczeń: 1. TEKSTOWO (CSV)");
        System.out.print("Wybierz format (1): ");
        String format = sc.nextLine();
        System.out.print("Podaj ścieżkę do pliku: ");
        String path = sc.nextLine();

        if (!format.equals("1")) {
            System.out.println("Nieznany format!");
            return;
        }
        List<Item> items = ItemDao.getAll();
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.println("UUID;Tytuł;Użytkownik");
            for (Item i : items) {
                if (!i.isAvailable() && i.getLoanedTo() != null) {
                    pw.printf("%s;%s;%s\n", i.getUuid().toString(), i.getTitle(), i.getLoanedTo());
                }
            }
            System.out.println("Wyeksportowano wypożyczenia.");
        } catch (Exception e) {
            System.out.println("Błąd eksportu wypożyczeń: " + e.getMessage());
        }
    }

    /**
     * Wyświetla listę wypożyczeń danego użytkownika.
     * 
     * @param username login użytkownika
     */
    public void listUserLoans(String username) {
        List<Item> items = ItemDao.getUserLoans(username);
        System.out.println("\n== Twoje wypożyczenia ==");
        for (Item i : items) {
            System.out.println(i);
        }
    }

    /**
     * Interaktywny zwrot wypożyczonej książki przez użytkownika.
     * 
     * @param username login użytkownika
     */
    public void returnUserLoanInteractive(String username) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj UUID książki do zwrotu: ");
        String uuid = sc.nextLine();
        boolean success = ItemDao.returnItemByUser(uuid, username);
        if (success) {
            System.out.println("Zwrócono książkę!");
        } else {
            System.out.println("Nie udało się zwrócić – ta pozycja nie należy do Ciebie lub nie istnieje.");
        }
    }

    /**
     * Próba logowania użytkownika z podaną rolą.
     * 
     * @param username login
     * @param password hasło
     * @param role rola użytkownika (np. "user", "librarian")
     * @return true jeśli login i hasło są poprawne, false w przeciwnym wypadku
     */
    public boolean login(String username, String password, String role) {
        return UserDao.findUser(username, role)
                .map(u -> u.getPassword().equals(password))
                .orElse(false);
    }
}
