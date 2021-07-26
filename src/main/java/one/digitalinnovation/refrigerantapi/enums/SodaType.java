package one.digitalinnovation.refrigerantapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SodaType {

    COKE("Coke"),
    FRUIT("Fruit"),
    GUARANA("Guarana");

    private final String description;
}
