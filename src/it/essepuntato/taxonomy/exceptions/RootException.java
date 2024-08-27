package it.essepuntato.taxonomy.exceptions;

/**
 * @author Silvio Peroni
 */
public class RootException extends Exception {
    public RootException() {}
    public RootException(Throwable t) {
        super(t);
    }
    public RootException(String m) {
        super(m);
    }
    public RootException(String m, Throwable t) {
        super(m, t);
    }
}
