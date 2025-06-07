package library.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Klasa reprezentująca książkę w systemie bibliotecznym.
 * Dziedziczy po klasie {@link Item} i implementuje interfejs {@link Loanable} oraz {@link Serializable}.
 */
public class Book extends Item implements Loanable, Serializable {
    private static final long serialVersionUID = 1L;

    /** Autor książki */
    private String author;

    /** Gatunek książki */
    private String genre;

    /**
     * Konstruktor tworzący nową książkę bez UUID, domyślnie dostępna.
     *
     * @param title  tytuł książki
     * @param year   rok wydania
     * @param author autor książki
     * @param genre  gatunek książki
     */
    public Book(String title, int year, String author, String genre) {
        super(title, year);
        this.author = author;
        this.genre = genre;
    }

    /**
     * Konstruktor pełny z UUID, statusem dostępności oraz informacją o wypożyczeniu.
     *
     * @param uuid      unikalny identyfikator książki
     * @param title     tytuł książki
     * @param year      rok wydania
     * @param author    autor książki
     * @param genre     gatunek książki
     * @param available informacja, czy książka jest dostępna
     * @param loanedTo  nazwa użytkownika, który wypożyczył książkę (lub null)
     */
    public Book(UUID uuid, String title, int year, String author, String genre, boolean available, String loanedTo) {
        super(uuid, title, year, available, loanedTo); 
        this.author = author;
        this.genre = genre;
    }

    /** @return autor książki */
    public String getAuthor() { return author; }

    /** @return gatunek książki */
    public String getGenre() { return genre; }

    /** Ustawia autora książki
     *  @param author autor książki
     */
    public void setAuthor(String author) { this.author = author; }

    /** Ustawia gatunek książki
     *  @param genre gatunek książki
     */
    public void setGenre(String genre) { this.genre = genre; }

    /**
     * Wyświetla szczegóły książki na konsoli.
     * Informuje o dostępności lub wypożyczeniu.
     */
    @Override
    public void displayDetails() {
        System.out.printf("BOOK | %s | %d | %s | %s | %s\n",
                title, year, author, genre,
                isAvailable() ? "Dostępna" : ("Wypożyczona przez: " + (getLoanedTo() != null ? getLoanedTo() : "-"))
        );
    }

    /** @return true jeśli książka jest dostępna do wypożyczenia */
    @Override
    public boolean isAvailable() { return available; }

    /**
     * Ustawia książkę jako wypożyczoną przez użytkownika.
     * @param username nazwa użytkownika wypożyczającego
     */
    @Override
    public void borrow(String username) {
        this.available = false;
        this.loanedTo = username;
    }

    /**
     * Zwraca książkę (ustawia jako dostępną).
     */
    @Override
    public void returnItem() {
        this.available = true;
        this.loanedTo = null;
    }

    /** @return nazwa użytkownika, który wypożyczył książkę */
    @Override
    public String getLoanedTo() { return loanedTo; }
}
