package one.digitalinnovation.refrigerantapi.controller;

import one.digitalinnovation.refrigerantapi.builder.SodaDTOBuilder;
import one.digitalinnovation.refrigerantapi.dto.SodaDTO;
import one.digitalinnovation.refrigerantapi.entity.Soda;
import one.digitalinnovation.refrigerantapi.exception.SodaAlreadyRegisteredException;
import one.digitalinnovation.refrigerantapi.exception.SodaNotFoundException;
import one.digitalinnovation.refrigerantapi.service.SodaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static one.digitalinnovation.refrigerantapi.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SodaControllerTest {

    private static final String SODA_API_URL_PATH = "/api/v1/sodas";
    private static final long VALID_SODA_ID = 1L;
    private static final long INVALID_SODA_ID = 2l;
    private static final String SODA_API_SUBPATH_INCREMENT_URL = "/increment";
    //private static final String SODA_API_SUBPATH_DECREMENT_URL = "/decrement";

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
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();
        sodaDTO.setBrand(null);

        // then
        mockMvc.perform(post(SODA_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(sodaDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        SodaDTO sodaDTO = SodaDTOBuilder.builder().build().toSodaDTO();

        //when
        when(sodaService.findByName(sodaDTO.getName())).thenReturn(sodaDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(SODA_API_URL_PATH + "/" + sodaDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(sodaDTO.getName())))
                .andExpect(jsonPath("$.brand", is(sodaDTO.getBrand())))
                .andExpect(jsonPath("$.sodaType", is(sodaDTO.getSodaType().toString())));
    }

}