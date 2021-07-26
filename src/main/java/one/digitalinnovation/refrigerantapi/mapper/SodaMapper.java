package one.digitalinnovation.refrigerantapi.mapper;

import one.digitalinnovation.refrigerantapi.dto.SodaDTO;
import one.digitalinnovation.refrigerantapi.entity.Soda;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SodaMapper {

    SodaMapper INSTANCE = Mappers.getMapper(SodaMapper.class);

    Soda toModel(SodaDTO sodaDTO);

    SodaDTO toDTO(Soda soda);
}
