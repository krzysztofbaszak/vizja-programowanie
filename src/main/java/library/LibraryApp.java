package library;

import java.util.Scanner;
import library.cli.LibrarianCommand;
import library.cli.UserCommand;
import library.db.DatabaseManager;
import library.db.LibrarianDao;
import library.db.UserDao;
import library.model.Librarian;
import library.model.User;
import library.util.ScreenUtil;

/**
 * Główna klasa aplikacji systemu bibliotecznego Vizja Library.
 * <p>
 * Zapewnia ekran logowania oraz uruchamia odpowiedni panel
 * dla bibliotekarza lub użytkownika po pomyślnym zalogowaniu.
 * </p>
 * <p>
 * Wyświetla powitanie z informacją o wersji i autorze.
 * </p>
 * 
 * @author Krzysztof Baszak
 * @version 1.0
 */
public class LibraryApp {

    /**
     * Metoda startowa aplikacji.
     * Łączy się z bazą danych, następnie wyświetla ekran logowania.
     * Po pomyślnym zalogowaniu uruchamia odpowiedni panel użytkownika.
     *
     * @param args argumenty linii poleceń (nieużywane)
     */
    public static void main(String[] args) {
        DatabaseManager.connect();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            ScreenUtil.clearScreen();
            System.out.println("\n" +
                "##############################################\n" +
                "#                                            #\n" +
                "#         V I Z J A   L I B R A R Y          #\n" +
                "#                                            #\n" +
                "#               wersja 1.0                   #\n" +
                "#         autor: Krzysztof Baszak            #\n" +
                "#                                            #\n" +
                "##############################################\n"
            );
            String role = null;
            String username = null;

            // Pętla logowania: dopóki dane nie są poprawne, prosi o ponowne wprowadzenie
            while (true) {
                System.out.print("Podaj nazwę użytkownika: ");
                String u = scanner.nextLine().trim();
                System.out.print("Podaj hasło: ");
                String p = scanner.nextLine().trim();

                Librarian librarian = LibrarianDao.getAllLibrarians().stream()
                        .filter(l -> l.getUsername().equals(u) && l.getPassword().equals(p))
                        .findFirst()
                        .orElse(null);
                if (librarian != null) {
                    role = "librarian";
                    username = u;
                    ScreenUtil.clearScreen();
                    System.out.println("Zalogowano jako " + username + " (librarian)");
                    break;
                }

                User user = UserDao.getAllUsers().stream()
                        .filter(us -> us.getUsername().equals(u) && us.getPassword().equals(p))
                        .findFirst()
                        .orElse(null);
                if (user != null) {
                    role = "user";
                    username = u;
                    ScreenUtil.clearScreen();
                    System.out.println("Zalogowano jako " + username + " (user)");
                    break;
                }

                System.out.println("Błędny login lub hasło. Spróbuj ponownie!\n");
            }

            // Po zalogowaniu uruchamia odpowiedni panel
            if ("librarian".equals(role)) {
                new LibrarianCommand(username).run();
            } else if ("user".equals(role)) {
                new UserCommand(username).run();
            } else {
                System.out.println("Nieznana rola! Kończę pracę.");
                break;
            }

            System.out.println("\nWylogowano. Powrót do ekranu logowania.");
        }
    }
}
