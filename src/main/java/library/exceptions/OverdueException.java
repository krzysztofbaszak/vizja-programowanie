package library.exceptions;

/**
 * Wyjątek sygnalizujący, że dana pozycja biblioteczna jest przeterminowana.
 * Może być używany do obsługi sytuacji, gdy książka lub magazyn jest zaległy
 * i np. nie można wykonać pewnych operacji do czasu uregulowania stanu.
 */
public class OverdueException extends Exception {

    /**
     * Konstruktor wyjątku z komunikatem błędu.
     *
     * @param message opis błędu dotyczącego przeterminowania
     */
    public OverdueException(String message) {
        super(message);
    }
}
