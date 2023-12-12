package Exceptions.ProfileException;

import Exceptions.PostException.PostException;

// Exceção lançada ao encontrar um post duplicado no banco de dados
public class DuplicateProfileException extends PostException {
    public DuplicateProfileException(String message) {
        super(message);
        
    }
    
}
