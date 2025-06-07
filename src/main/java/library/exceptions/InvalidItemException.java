package library.exceptions;

/**
 * Wyjątek sygnalizujący nieprawidłową pozycję biblioteczną (Item).
 * Używany do zgłaszania błędów związanych z walidacją lub stanem obiektu Item.
 */
public class InvalidItemException extends Exception {

    /**
     * Konstruktor wyjątku z komunikatem błędu.
     *
     * @param message opis błędu
     */
    public InvalidItemException(String message) {
        super(message);
    }
}
