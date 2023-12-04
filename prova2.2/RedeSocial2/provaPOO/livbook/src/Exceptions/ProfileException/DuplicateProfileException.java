package Exceptions.ProfileException;

import Exceptions.PostException.PostException;

public class DuplicateProfileException extends PostException {
    public DuplicateProfileException(String message) {
        super(message);
        
    }
    
}
