package library.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Abstrakcyjna klasa bazowa reprezentująca pozycję biblioteczną.
 * Zawiera pola identyfikujące pozycję oraz jej podstawowe właściwości,
 * takie jak tytuł, rok wydania, dostępność oraz informacje o wypożyczeniu.
 * Implementuje interfejs {@link Serializable} oraz wymaga implementacji metody wyświetlania szczegółów.
 */
public abstract class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Unikalny identyfikator pozycji */
    protected final UUID uuid;

    /** Tytuł pozycji */
    protected String title;

    /** Rok wydania */
    protected int year;

    /** Informacja o dostępności pozycji */
    protected boolean available;

    /** Nazwa użytkownika, który wypożyczył pozycję (null jeśli dostępna) */
    protected String loanedTo;

    /**
     * Konstruktor tworzący nową pozycję z generowanym UUID.
     * Pozycja domyślnie jest dostępna i nie jest wypożyczona.
     *
     * @param title tytuł pozycji
     * @param year rok wydania
     */
    public Item(String title, int year) {
        this.uuid = UUID.randomUUID();
        this.title = title;
        this.year = year;
        this.available = true;
        this.loanedTo = null;
    }

    /**
     * Konstruktor pełny z parametrem UUID.
     *
     * @param uuid unikalny identyfikator
     * @param title tytuł pozycji
     * @param year rok wydania
     * @param available status dostępności
     * @param loanedTo nazwa użytkownika wypożyczającego (null jeśli dostępna)
     */
    public Item(UUID uuid, String title, int year, boolean available, String loanedTo) {
        this.uuid = uuid;
        this.title = title;
        this.year = year;
        this.available = available;
        this.loanedTo = loanedTo;
    }

    /** @return unikalny identyfikator pozycji */
    public UUID getUuid() { return uuid; }

    /** @return tytuł pozycji */
    public String getTitle() { return title; }

    /** @return rok wydania */
    public int getYear() { return year; }

    /** Ustawia tytuł pozycji
     * @param title nowy tytuł
     */
    public void setTitle(String title) { this.title = title; }

    /** Ustawia rok wydania
     * @param year nowy rok wydania
     */
    public void setYear(int year) { this.year = year; }

    /** @return czy pozycja jest dostępna */
    public boolean isAvailable() { return available; }

    /** Ustawia status dostępności pozycji
     * @param available dostępność (true = dostępna)
     */
    public void setAvailable(boolean available) { this.available = available; }

    /** @return nazwa użytkownika, który wypożyczył pozycję (lub null) */
    public String getLoanedTo() { return loanedTo; }

    /** Ustawia użytkownika, który wypożyczył pozycję
     * @param loanedTo nazwa użytkownika
     */
    public void setLoanedTo(String loanedTo) { this.loanedTo = loanedTo; }

    /**
     * Abstrakcyjna metoda do wyświetlania szczegółów pozycji.
     * Każda podklasa musi ją zaimplementować.
     */
    public abstract void displayDetails();

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s (%d) [%s] %s", title, year, uuid,
                available ? "DOSTĘPNA" : "WYPOŻYCZONA przez: " + (loanedTo != null ? loanedTo : "—"));
    }
}
