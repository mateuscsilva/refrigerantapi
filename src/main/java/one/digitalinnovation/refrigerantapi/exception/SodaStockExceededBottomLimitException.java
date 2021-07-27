package one.digitalinnovation.refrigerantapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SodaStockExceededBottomLimitException extends Exception{

    public SodaStockExceededBottomLimitException(Long id, int quantityToDecrement){
        super(String.format("Soda with %s ID to decrement informed exceeds the min stock capacity: %s",
                id, quantityToDecrement));
    }
}
