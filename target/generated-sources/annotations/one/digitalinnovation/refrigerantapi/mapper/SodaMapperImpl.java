package one.digitalinnovation.refrigerantapi.mapper;

import javax.annotation.processing.Generated;
import one.digitalinnovation.refrigerantapi.dto.SodaDTO;
import one.digitalinnovation.refrigerantapi.entity.Soda;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-07-27T16:47:04-0300",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 16 (Oracle Corporation)"
)
public class SodaMapperImpl implements SodaMapper {

    @Override
    public Soda toModel(SodaDTO sodaDTO) {
        if ( sodaDTO == null ) {
            return null;
        }

        Soda soda = new Soda();

        soda.setId( sodaDTO.getId() );
        soda.setName( sodaDTO.getName() );
        soda.setBrand( sodaDTO.getBrand() );
        soda.setMax( sodaDTO.getMax() );
        soda.setQuantity( sodaDTO.getQuantity() );
        soda.setSodaType( sodaDTO.getSodaType() );

        return soda;
    }

    @Override
    public SodaDTO toDTO(Soda soda) {
        if ( soda == null ) {
            return null;
        }

        SodaDTO sodaDTO = new SodaDTO();

        sodaDTO.setId( soda.getId() );
        sodaDTO.setName( soda.getName() );
        sodaDTO.setBrand( soda.getBrand() );
        sodaDTO.setMax( soda.getMax() );
        sodaDTO.setQuantity( soda.getQuantity() );
        sodaDTO.setSodaType( soda.getSodaType() );

        return sodaDTO;
    }
}
