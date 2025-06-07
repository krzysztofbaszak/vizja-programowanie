package library.model;

/**
 * Interfejs definiujący zachowanie obiektów, które mogą być wypożyczane.
 */
public interface Loanable {

    /**
     * Sprawdza, czy obiekt jest dostępny do wypożyczenia.
     * 
     * @return true, jeśli obiekt jest dostępny, false jeśli jest wypożyczony
     */
    boolean isAvailable();

    /**
     * Wypożycza obiekt na rzecz użytkownika o podanej nazwie.
     * 
     * @param username nazwa użytkownika wypożyczającego obiekt
     */
    void borrow(String username);

    /**
     * Zwraca obiekt, czyniąc go ponownie dostępnym.
     */
    void returnItem();

    /**
     * Pobiera nazwę użytkownika, który aktualnie wypożyczył obiekt.
     * 
     * @return nazwa użytkownika lub null, jeśli obiekt nie jest wypożyczony
     */
    String getLoanedTo();
}
