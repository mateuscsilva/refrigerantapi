package one.digitalinnovation.refrigerantapi.builder;

import lombok.Builder;
import one.digitalinnovation.refrigerantapi.dto.SodaDTO;
import one.digitalinnovation.refrigerantapi.enums.SodaType;

@Builder
public class SodaDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Pepsi";

    @Builder.Default
    private String brand = "PepsiCO";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private SodaType type = SodaType.COLA;

    public SodaDTO toSodaDTO() {
        return new SodaDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
