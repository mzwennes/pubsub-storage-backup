package nl.debijenkorf.backup.values;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class Field implements Serializable {

    private final String name;
    private final DataType type;

}
