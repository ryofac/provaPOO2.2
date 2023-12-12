package Exceptions;

// Exceção que encapsula as exceções relacionadas ao aplicativo em si
public class AppException extends Exception {
    public AppException(String msg){
        super(msg);
    }
    
}
