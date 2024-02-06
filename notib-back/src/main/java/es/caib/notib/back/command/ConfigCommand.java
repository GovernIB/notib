package es.caib.notib.back.command;

import es.caib.notib.logic.intf.dto.config.ConfigDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigCommand {

    private String key;
    private String value;

    private String entitatCodi;
    private String entitatValue;

    public boolean isBooleanValue() {
        return value!=null && value.equals("true");
    }
    public void setBooleanValue(boolean booleanValue) {
        this.value = booleanValue ? "true" : "false";
    }

    public boolean isEntitatBooleanValue() {
        return entitatValue!=null && entitatValue.equals("true");
    }

    public void setEntitatBooleanValue(boolean booleanValue) {
        this.entitatValue = booleanValue ? "true" : "false";
    }


    public ConfigDto asDto() {
        return ConfigDto.builder().key(this.key).value(this.value).entitatCodi(entitatCodi).entitatValue(entitatValue).build();
    }
}
