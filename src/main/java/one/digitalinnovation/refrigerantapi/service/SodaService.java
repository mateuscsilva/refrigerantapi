package one.digitalinnovation.refrigerantapi.service;

import lombok.AllArgsConstructor;
import one.digitalinnovation.refrigerantapi.dto.SodaDTO;
import one.digitalinnovation.refrigerantapi.entity.Soda;
import one.digitalinnovation.refrigerantapi.mapper.SodaMapper;
import one.digitalinnovation.refrigerantapi.repository.SodaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SodaService {

    private final SodaRepository sodaRepository;
    private final SodaMapper sodaMapper = SodaMapper.INSTANCE;


    public SodaDTO createSoda(SodaDTO sodaDTO){
        return new SodaDTO();
    }
}
