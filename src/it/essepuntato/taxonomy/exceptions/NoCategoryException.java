package it.essepuntato.taxonomy.exceptions;

/**
 * @author Silvio Peroni
 */
public class NoCategoryException extends Exception{
    public NoCategoryException() {}
    public NoCategoryException(Throwable t) {
        super(t);
    }
    public NoCategoryException(String m) {
        super(m);
    }
    public NoCategoryException(String m, Throwable t) {
        super(m, t);
    }
}
