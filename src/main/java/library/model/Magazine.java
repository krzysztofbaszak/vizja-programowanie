package library.model;

import java.util.UUID;
import java.io.Serializable;

/**
 * Klasa reprezentująca magazyn (czasopismo) jako pozycję biblioteczną.
 * Implementuje interfejs Loanable, co oznacza, że może być wypożyczana.
 */
public class Magazine extends Item implements Loanable, Serializable {
    private static final long serialVersionUID = 1L;

    /** Numer wydania magazynu */
    private int issueNumber;

    /**
     * Konstruktor tworzący nowy egzemplarz magazynu z nowym UUID.
     * 
     * @param title       tytuł magazynu
     * @param year        rok wydania
     * @param issueNumber numer wydania magazynu
     */
    public Magazine(String title, int year, int issueNumber) {
        super(title, year);
        this.issueNumber = issueNumber;
    }

    /**
     * Konstruktor tworzący magazyn z określonym UUID i stanem wypożyczenia.
     * 
     * @param uuid        unikalny identyfikator magazynu
     * @param title       tytuł magazynu
     * @param year        rok wydania
     * @param issueNumber numer wydania magazynu
     * @param available   dostępność magazynu (true jeśli dostępny)
     * @param loanedTo    nazwa użytkownika, który wypożyczył magazyn (lub null)
     */
    public Magazine(UUID uuid, String title, int year, int issueNumber, boolean available, String loanedTo) {
        super(uuid, title, year, available, loanedTo);
        this.issueNumber = issueNumber;
    }

    /** 
     * Pobiera numer wydania magazynu.
     * 
     * @return numer wydania
     */
    public int getIssueNumber() { return issueNumber; }

    /**
     * Ustawia numer wydania magazynu.
     * 
     * @param issueNumber numer wydania do ustawienia
     */
    public void setIssueNumber(int issueNumber) { this.issueNumber = issueNumber; }

    /**
     * Wyświetla szczegóły magazynu na konsoli.
     */
    @Override
    public void displayDetails() {
        System.out.printf("MAGAZINE | %s | %d | Nr: %d | %s\n",
                title, year, issueNumber,
                isAvailable() ? "Dostępny" : ("Wypożyczony przez: " + (getLoanedTo() != null ? getLoanedTo() : "-"))
        );
    }

    /**
     * Sprawdza, czy magazyn jest dostępny do wypożyczenia.
     * 
     * @return true jeśli dostępny, false jeśli wypożyczony
     */
    @Override
    public boolean isAvailable() { return available; }

    /**
     * Wypożycza magazyn na rzecz użytkownika.
     * 
     * @param username nazwa użytkownika wypożyczającego magazyn
     */
    @Override
    public void borrow(String username) {
        this.available = false;
        this.loanedTo = username;
    }

    /**
     * Zwraca magazyn, czyniąc go dostępnym.
     */
    @Override
    public void returnItem() {
        this.available = true;
        this.loanedTo = null;
    }

    /**
     * Pobiera nazwę użytkownika, który wypożyczył magazyn.
     * 
     * @return nazwa użytkownika lub null, jeśli magazyn nie jest wypożyczony
     */
    @Override
    public String getLoanedTo() { return loanedTo; }
}
