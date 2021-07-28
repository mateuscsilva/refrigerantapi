package one.digitalinnovation.refrigerantapi.controller;

import one.digitalinnovation.refrigerantapi.builder.SodaDTOBuilder;
import one.digitalinnovation.refrigerantapi.dto.QuantityDTO;
import one.digitalinnovation.refrigerantapi.dto.SodaDTO;
import one.digitalinnovation.refrigerantapi.exception.SodaNotFoundException;
import one.digitalinnovation.refrigerantapi.exception.SodaStockExceededBottomLimitException;
import one.digitalinnovation.refrigerantapi.exception.SodaStockExceededException;
import one.digitalinnovation.refrigerantapi.service.SodaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.JsonPath;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static one.digitalinnovation.refrigerantapi.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SodaControllerTest {

    private static final String SODA_API_URL_PATH = "/api/v1/sodas";
    private static final String INVALID_SODA_NAME = "@";
    private static final long VALID_SODA_ID = 1L;
    private static final long INVALID_SODA_ID = 2L;
    private static final String SODA_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String SODA_API_SUBPATH_DECREMENT_URL = "/decrement";
    private static final String SODA_API_SUBPATH_EMPTY_URL = "/empty";

    private MockMvc mockMvc;

    @Mock
    private SodaService sodaService;

    @InjectMocks
    private SodaController sodaController;

    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(sodaController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenASodaIsCreated() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        when(sodaService.createSoda(sodaDTO)).thenReturn(sodaDTO);


        mockMvc.perform(post(SODA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sodaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$.sodaType", is(sodaDTO.getSodaType().toString())));


    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setBrand(null);

        mockMvc.perform(post(SODA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sodaDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        when(sodaService.findByName(sodaDTO.getName())).thenReturn(sodaDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH + "/" + sodaDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$.sodaType", is(sodaDTO.getSodaType().toString())));
    }


    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        when(sodaController.findByName(sodaDTO.getName())).thenThrow(SodaNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH + "/" + sodaDTO.getName())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithBeersIsCalledThenOkStatusIsReturned() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        when(sodaController.listAll()).thenReturn(Collections.singletonList(sodaDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$[0].sodaType", is(sodaDTO.getSodaType().toString())));
    }

    @Test
    void whenGETListWithoutSodasIsCalledThenOkStatusIsReturned() throws Exception {
        when(sodaController.listAll()).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        doNothing().when(sodaService).deleteById(sodaDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete(SODA_API_URL_PATH + "/" + sodaDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        doThrow(SodaNotFoundException.class).when(sodaService).deleteById(INVALID_SODA_ID);

        mockMvc.perform(MockMvcRequestBuilders.delete(SODA_API_URL_PATH + "/" + INVALID_SODA_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setQuantity(sodaDTO.getQuantity() + quantityDTO.getQuantity());

        when(sodaService.increment(VALID_SODA_ID, quantityDTO.getQuantity())).thenReturn(sodaDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(SODA_API_URL_PATH + "/" + VALID_SODA_ID + SODA_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$.sodaType", is(sodaDTO.getSodaType().toString())))
                .andExpect(jsonPath("$.quantity", is(sodaDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setQuantity(sodaDTO.getQuantity() + quantityDTO.getQuantity());

        when(sodaService.increment(VALID_SODA_ID, quantityDTO.getQuantity())).
                thenThrow(SodaStockExceededException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(SODA_API_URL_PATH + "/" + VALID_SODA_ID + SODA_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void whenPATCHIsCalledWithInvalidBeerIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        when(sodaService.increment(INVALID_SODA_ID, quantityDTO.getQuantity())).thenThrow(SodaNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(SODA_API_URL_PATH + "/" + INVALID_SODA_ID + SODA_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setQuantity(sodaDTO.getQuantity() + quantityDTO.getQuantity());

        when(sodaService.decrement(VALID_SODA_ID, quantityDTO.getQuantity())).thenReturn(sodaDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(SODA_API_URL_PATH + "/" + VALID_SODA_ID
                + SODA_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$.sodaType", is(sodaDTO.getSodaType().toString())))
                .andExpect(jsonPath("$.quantity", is(sodaDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToDecrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(60)
                .build();

        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setQuantity(sodaDTO.getQuantity() - quantityDTO.getQuantity());

        when(sodaService.decrement(VALID_SODA_ID, quantityDTO.getQuantity())).thenThrow(SodaStockExceededBottomLimitException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(SODA_API_URL_PATH + "/"
                + VALID_SODA_ID + SODA_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidSodaIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        when(sodaService.decrement(INVALID_SODA_ID, quantityDTO.getQuantity())).thenThrow(SodaNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(SODA_API_URL_PATH + "/"
                + INVALID_SODA_ID + SODA_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledWithInvalidSodaNameToEmptyThenNotFoundStatusIsReturned() throws Exception {
        when(sodaService.emptyStockByName(INVALID_SODA_NAME)).thenThrow(SodaNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(SODA_API_URL_PATH + "/"
                + INVALID_SODA_NAME + SODA_API_SUBPATH_EMPTY_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledWithValidSodaNameToEmptyThenOkStatusIsReturned() throws Exception {
        SodaDTO expectedSodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        expectedSodaDTO.setQuantity(0);

        when(sodaService.emptyStockByName(expectedSodaDTO.getName())).thenReturn(expectedSodaDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(SODA_API_URL_PATH + "/"
                + expectedSodaDTO.getName() + SODA_API_SUBPATH_EMPTY_URL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(expectedSodaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(expectedSodaDTO.getBrand())))
                .andExpect(jsonPath("$.sodaType", is(expectedSodaDTO.getSodaType().toString())))
                .andExpect(jsonPath("$.quantity", is(expectedSodaDTO.getQuantity())));
    }
}