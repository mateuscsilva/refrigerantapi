package one.digitalinnovation.refrigerantapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Soda {

    private Long id;
    private String name;
    private String brand;
    private int max;
    private int quantity;

}
