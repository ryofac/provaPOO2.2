package Exceptions.DBException;

// Esse erro é lançado em caso de erro com o banco de dados
public class DBException extends Exception {
    public DBException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
