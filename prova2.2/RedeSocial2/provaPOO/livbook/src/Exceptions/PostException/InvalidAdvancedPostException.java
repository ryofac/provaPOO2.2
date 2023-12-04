package Exceptions.PostException;

/** 
 * É geralmente lançada quando se tenta interpretar um Post como um Post Avançado incorretamente 
 */
public class InvalidAdvancedPostException extends PostException {
    public InvalidAdvancedPostException(String msg){
        super(msg);
    }
    
}
