package one.digitalinnovation.refrigerantapi.service;

import one.digitalinnovation.refrigerantapi.builder.SodaDTOBuilder;
import one.digitalinnovation.refrigerantapi.dto.SodaDTO;
import one.digitalinnovation.refrigerantapi.entity.Soda;
import one.digitalinnovation.refrigerantapi.exception.SodaAlreadyRegisteredException;
import one.digitalinnovation.refrigerantapi.exception.SodaNotFoundException;
import one.digitalinnovation.refrigerantapi.exception.SodaStockExceededBottomLimitException;
import one.digitalinnovation.refrigerantapi.exception.SodaStockExceededException;
import one.digitalinnovation.refrigerantapi.mapper.SodaMapper;
import one.digitalinnovation.refrigerantapi.repository.SodaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SodaServiceTest {

    private static final long INVALID_SODA_ID = 1L;

    @Mock
    private SodaRepository sodaRepository;

    private SodaMapper sodaMapper = SodaMapper.INSTANCE;

    @InjectMocks
    private SodaService sodaService;

    @Test
    void whenSodaInformedThenIShouldCreated() throws SodaAlreadyRegisteredException {
        SodaDTO expectedSaveSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSaveSoda = sodaMapper.toModel(expectedSaveSodaDTO);

        when(sodaRepository.findByName(expectedSaveSodaDTO.getName())).thenReturn(Optional.empty());
        when(sodaRepository.save(expectedSaveSoda)).thenReturn(expectedSaveSoda);

        SodaDTO createdSodaDTO = sodaService.createSoda(expectedSaveSodaDTO);

        assertThat(createdSodaDTO.getId(), is(equalTo(expectedSaveSodaDTO.getId())));
        assertThat(createdSodaDTO.getName(), is(equalTo(expectedSaveSodaDTO.getName())));
        assertThat(createdSodaDTO.getQuantity(), is(equalTo(expectedSaveSodaDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredSodaInformedThenAnExceptionShouldBeThrown(){
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda duplicatedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(sodaRepository.findByName(expectedSodaDTO.getName())).thenReturn(Optional.of(duplicatedSoda));

        assertThrows(SodaAlreadyRegisteredException.class, () -> sodaService.createSoda(expectedSodaDTO));
    }

    @Test
    void whenValidSodaNameIsGivenThenReturnASoda() throws SodaNotFoundException {
        SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedFoundSoda = sodaMapper.toModel(expectedFoundSodaDTO);

        when(sodaRepository.findByName(expectedFoundSoda.getName())).thenReturn(Optional.of(expectedFoundSoda));

        SodaDTO foundSodaDTO = sodaService.findByName(expectedFoundSodaDTO.getName());
        assertThat(foundSodaDTO, is(equalTo(expectedFoundSodaDTO)));
    }

    @Test
    void whenNotRegisteredSodaNameIsGivenThenThrowAnException() {
        SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        when(sodaRepository.findByName(expectedFoundSodaDTO.getName())).thenReturn(Optional.empty());

        assertThrows(SodaNotFoundException.class, () -> sodaService.findByName(expectedFoundSodaDTO.getName()));
    }

    @Test
    void whenListSodaIsCalledThenReturnAListOfBeers(){
        SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedFoundSoda = sodaMapper.toModel(expectedFoundSodaDTO);

        when(sodaRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundSoda));

        List<SodaDTO> foundListSodaDTO = sodaService.listAlL();

        assertThat(foundListSodaDTO, is(not(empty())));
        assertThat(foundListSodaDTO.get(0), is(equalTo(expectedFoundSodaDTO)));
    }

    @Test
    void whenListBeerIsCalledThenReturnAnEmptyListOfBeers(){
        when(sodaService.listAlL()).thenReturn(Collections.EMPTY_LIST);

        List<SodaDTO> foundListSodaDTO =  sodaService.listAlL();

        assertThat(foundListSodaDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenASodaShouldBeDeleted() throws SodaNotFoundException{
        SodaDTO expectedDeletedBeerDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedDeletedBeer = sodaMapper.toModel(expectedDeletedBeerDTO);

        when(sodaRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
        doNothing().when(sodaRepository).deleteById(expectedDeletedBeerDTO.getId());

        sodaService.deleteById(expectedDeletedBeerDTO.getId());

        verify(sodaRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
        verify(sodaRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
    }

    @Test
    void whenIncrementIsCalledThenIncrementSodaStock() throws SodaNotFoundException, SodaStockExceededException {
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedSoda));
        when(sodaRepository.save(expectedSoda)).thenReturn(expectedSoda);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedSodaDTO.getQuantity() + quantityToIncrement;

        SodaDTO incrementedSodarDTO = sodaService.increment(expectedSodaDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedSodarDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedSodaDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedSoda));

        int quantityToIncrement = 80;
        assertThrows(SodaStockExceededException.class,
                () -> sodaService.increment(expectedSodaDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedSoda));

        int quantityToIncrement = 45;
        assertThrows(SodaStockExceededException.class,
                () -> sodaService.increment(expectedSodaDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(sodaRepository.findById(INVALID_SODA_ID)).thenReturn(Optional.empty());

        assertThrows(SodaNotFoundException.class, () -> sodaService.increment(INVALID_SODA_ID, quantityToIncrement));
    }


    @Test
    void whenDecrementIsCalledThenDecrementSodaStock() throws SodaNotFoundException,
            SodaStockExceededBottomLimitException {

        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);


        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedSoda));
        when(sodaRepository.save(expectedSoda)).thenReturn(expectedSoda);

        int quantityToDecrement = 10;
        int expectedQuantityAfterDecrement = expectedSodaDTO.getQuantity() - quantityToDecrement;


        SodaDTO decrementedSodaDTO = sodaService.decrement(expectedSodaDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(decrementedSodaDTO.getQuantity()));
        assertThat(expectedQuantityAfterDecrement, lessThan(expectedSodaDTO.getMax()));
    }

    @Test
    void whenDecrementIsLessThanMinThenThrowException() {
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedSoda));

        int quantityToDecrement = -1;
        assertThrows(SodaStockExceededBottomLimitException.class,
                () -> sodaService.decrement(expectedSodaDTO.getId(), quantityToDecrement));
    }


    @Test
    void whenDecrementAfterSubIsLessThanMinThenThrowException() {
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectedSoda = sodaMapper.toModel(expectedSodaDTO);

        when(sodaRepository.findById(expectedSodaDTO.getId())).thenReturn(Optional.of(expectedSoda));

        int quantityToIncrement = 45;
        assertThrows(SodaStockExceededException.class,
                () -> sodaService.increment(expectedSodaDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToDecrement = 10;

        when(sodaRepository.findById(INVALID_SODA_ID)).thenReturn(Optional.empty());

        assertThrows(SodaNotFoundException.class, () -> sodaService.decrement(INVALID_SODA_ID, quantityToDecrement));
    }


    @Test
    void whenEmptyIsCalledWithInvalidName() throws SodaNotFoundException {
        SodaDTO expectedFoundSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        when(sodaRepository.findByName(expectedFoundSodaDTO.getName())).thenReturn(Optional.empty());

        assertThrows(SodaNotFoundException.class, () -> sodaService.emptyStockByName(expectedFoundSodaDTO.getName()));
    }

    @Test
    void whenEmptyIsCalledWithValidName() throws SodaNotFoundException {
        SodaDTO expectEmptySodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        Soda expectEmptySoda = sodaMapper.toModel(expectEmptySodaDTO);

        when(sodaRepository.findByName(expectEmptySodaDTO.getName())).thenReturn(Optional.of(expectEmptySoda));
        when(sodaRepository.save(expectEmptySoda)).thenReturn(expectEmptySoda);

        int expectedQuantityAfterEmptyStock = 0;
        SodaDTO emptySodaDTO = sodaService.emptyStockByName(expectEmptySoda.getName());

        assertThat(expectedQuantityAfterEmptyStock, equalTo(emptySodaDTO.getQuantity()));
    }
}
