package one.digitalinnovation.refrigerantapi.service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.refrigerantapi.dto.SodaDTO;
import one.digitalinnovation.refrigerantapi.entity.Soda;
import one.digitalinnovation.refrigerantapi.exception.SodaAlreadyRegisteredException;
import one.digitalinnovation.refrigerantapi.exception.SodaNotFoundException;
import one.digitalinnovation.refrigerantapi.exception.SodaStockExceededBottomLimitException;
import one.digitalinnovation.refrigerantapi.exception.SodaStockExceededException;
import one.digitalinnovation.refrigerantapi.mapper.SodaMapper;
import one.digitalinnovation.refrigerantapi.repository.SodaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SodaService {

    private final SodaRepository sodaRepository;
    private final SodaMapper sodaMapper = SodaMapper.INSTANCE;


    public SodaDTO createSoda(SodaDTO sodaDTO) throws SodaAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(sodaDTO.getName());
        Soda toSaveSoda = sodaMapper.toModel(sodaDTO);
        Soda savedSoda = sodaRepository.save(toSaveSoda);
        return sodaMapper.toDTO(savedSoda);
    }

    public SodaDTO findByName(String name) throws SodaNotFoundException {
        Soda savedSoda = sodaRepository.findByName(name)
                .orElseThrow(() -> new SodaNotFoundException(name));
        return sodaMapper.toDTO(savedSoda);
    }

    public List<SodaDTO> listAlL(){
        return sodaRepository.findAll()
                .stream()
                .map(sodaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws SodaNotFoundException {
        verifyIfExists(id);
        sodaRepository.deleteById(id);
    }

    public SodaDTO increment(Long id, int quantityToIncrement) throws SodaNotFoundException,
            SodaStockExceededException {
        Soda savedSoda = verifyIfExists(id);
        int quantityAfterIncrement =  quantityToIncrement + savedSoda.getQuantity();
        if(quantityAfterIncrement <=  savedSoda.getMax()){
            savedSoda.setQuantity(quantityAfterIncrement);
            Soda sodaIncrementedStock = sodaRepository.save(savedSoda);
            return sodaMapper.toDTO(sodaIncrementedStock);
        }
        throw new SodaStockExceededException(id, quantityToIncrement);

    }

    public SodaDTO decrement(Long id, Integer quantityToDecrement) throws SodaNotFoundException, SodaStockExceededBottomLimitException {
        Soda savedSoda = verifyIfExists(id);
        int quantityAfterDecrement = savedSoda.getQuantity() - quantityToDecrement;
        if(quantityAfterDecrement >= 0 && quantityToDecrement >= 0){
            savedSoda.setQuantity(quantityAfterDecrement);
            Soda sodaDecrementedStock = sodaRepository.save(savedSoda);
            return sodaMapper.toDTO(sodaDecrementedStock);
        }
        throw new SodaStockExceededBottomLimitException(id, quantityToDecrement);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws SodaAlreadyRegisteredException {
        Optional<Soda> optSavedSoda = sodaRepository.findByName(name);
        if(optSavedSoda.isPresent()){
            throw new SodaAlreadyRegisteredException(name);
        }
    }

    private Soda verifyIfExists(Long id) throws SodaNotFoundException {
        return sodaRepository.findById(id)
                .orElseThrow(() -> new SodaNotFoundException(id));
    }

}
