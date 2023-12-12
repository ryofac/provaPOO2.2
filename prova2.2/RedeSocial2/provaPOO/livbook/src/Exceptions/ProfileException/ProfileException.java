package Exceptions.ProfileException;

import Exceptions.AppException;

// Exceção que encapsula as exceções relacionadas a perfis
public class ProfileException extends AppException {
    public ProfileException(String msg) {
        super(msg);
    }

}
