package library.util;

/**
 * Klasa narzędziowa do operacji na ekranie konsoli.
 */
public class ScreenUtil {

    /**
     * Czyści ekran konsoli (działa na terminalach obsługujących ANSI).
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
