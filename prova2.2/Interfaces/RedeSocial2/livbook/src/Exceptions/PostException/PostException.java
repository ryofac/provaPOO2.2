package Exceptions.PostException;

import Exceptions.AppException;

// Classe que encapsula as exceções relacionadas a posts
public class PostException extends AppException {
    public PostException(String msg) {
        super(msg);
    }

}
