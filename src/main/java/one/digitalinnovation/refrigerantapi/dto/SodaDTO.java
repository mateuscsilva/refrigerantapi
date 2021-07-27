package one.digitalinnovation.refrigerantapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import one.digitalinnovation.refrigerantapi.enums.SodaType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SodaDTO {

    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    @Size(min = 1, max = 100)
    private String brand;

    @NotNull
    @Max(value = 500)
    @Min(0)
    private int max;

    @NotNull
    @Max(value = 100)
    @Min(0)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @NotNull
    private SodaType sodaType;
}
