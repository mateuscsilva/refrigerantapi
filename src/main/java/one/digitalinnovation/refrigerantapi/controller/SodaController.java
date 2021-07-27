package one.digitalinnovation.refrigerantapi.controller;

import lombok.AllArgsConstructor;
import one.digitalinnovation.refrigerantapi.dto.QuantityDTO;
import one.digitalinnovation.refrigerantapi.dto.SodaDTO;
import one.digitalinnovation.refrigerantapi.exception.SodaAlreadyRegisteredException;
import one.digitalinnovation.refrigerantapi.exception.SodaNotFoundException;
import one.digitalinnovation.refrigerantapi.exception.SodaStockExceededBottomLimitException;
import one.digitalinnovation.refrigerantapi.exception.SodaStockExceededException;
import one.digitalinnovation.refrigerantapi.service.SodaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/sodas")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SodaController {

    private final SodaService sodaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SodaDTO createSoda(@RequestBody @Valid SodaDTO sodaDTO) throws SodaAlreadyRegisteredException {
        return sodaService.createSoda(sodaDTO);
    }

    @GetMapping("/{name}")
    public SodaDTO findByName(@PathVariable String name) throws SodaNotFoundException {
        return sodaService.findByName(name);
    }

    @GetMapping
    public List<SodaDTO> listSodas(){ return sodaService.listAlL(); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws SodaNotFoundException {
        sodaService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public SodaDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws SodaNotFoundException,
            SodaStockExceededException {
        return sodaService.increment(id, quantityDTO.getQuantity());
    }

    @PatchMapping("/{id}/decrement")
    public SodaDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws
            SodaNotFoundException, SodaStockExceededBottomLimitException {
        return sodaService.decrement(id, quantityDTO.getQuantity());
    }
}
