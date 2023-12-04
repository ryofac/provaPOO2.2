package Exceptions.PostException;

import Exceptions.AppException;

// Essa exceção é lançada quando algum elemento em sua pesquisa não existe no repository
// A sua mensagem é passada no construtor ao longo do código
public class PostNotFoundException extends AppException {
    public PostNotFoundException(String message) {
        super(message);
    }

}
