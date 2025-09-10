package mk.ukim.finki.wp.liga.model.Exceptions;

public class InvalidFootballMatchException extends RuntimeException {
    
    public InvalidFootballMatchException() {
        super();
    }
    
    public InvalidFootballMatchException(String message) {
        super(message);
    }
}
