package one.digitalinnovation.refrigerantapi.utils;

import one.digitalinnovation.refrigerantapi.builder.SodaDTOBuilder;
import one.digitalinnovation.refrigerantapi.dto.SodaDTO;
import one.digitalinnovation.refrigerantapi.entity.Soda;
import one.digitalinnovation.refrigerantapi.exception.SodaAlreadyRegisteredException;
import one.digitalinnovation.refrigerantapi.mapper.SodaMapper;
import one.digitalinnovation.refrigerantapi.repository.SodaRepository;
import one.digitalinnovation.refrigerantapi.service.SodaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
