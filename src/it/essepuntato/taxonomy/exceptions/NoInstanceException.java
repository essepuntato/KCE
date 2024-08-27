package it.essepuntato.taxonomy.exceptions;

/**
 *
 * @author Silvio Peroni
 */
public class NoInstanceException extends Exception {
    public NoInstanceException() {}
    public NoInstanceException(Throwable t) {
        super(t);
    }
    public NoInstanceException(String m) {
        super(m);
    }
    public NoInstanceException(String m, Throwable t) {
        super(m, t);
    }
}
