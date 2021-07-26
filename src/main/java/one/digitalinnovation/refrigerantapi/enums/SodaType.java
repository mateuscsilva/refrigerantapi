package one.digitalinnovation.refrigerantapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SodaType {

    CLUB("Club"),
    COLA("Cola"),
    GINGER_ALE("Ginger ale"),
    GINGER_BEER("Ginger beer"),
    LEMON_LINE("Lemon line"),
    ROOT_BEER("Root beer"),
    TONIC("Tonic"),
    WATER("Water");

    private final String description;
}
