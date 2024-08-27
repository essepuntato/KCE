package it.essepuntato.taxonomy.exceptions;

/**
 * @author Silvio Peroni
 */
public class NoPropertyException extends Exception {
    public NoPropertyException() {}
    public NoPropertyException(Throwable t) {
        super(t);
    }
    public NoPropertyException(String m) {
        super(m);
    }
    public NoPropertyException(String m, Throwable t) {
        super(m, t);
    }
}
