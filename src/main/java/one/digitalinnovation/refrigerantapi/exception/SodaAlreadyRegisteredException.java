package one.digitalinnovation.refrigerantapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SodaAlreadyRegisteredException extends Exception{

    public SodaAlreadyRegisteredException(String name){
        super(String.format("Soda with name %s already registered in the system", name));
    }
}
