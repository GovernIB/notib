package es.caib.notib.war.command;

import com.google.common.base.Strings;
import es.caib.notib.core.api.dto.config.ConfigDto;
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
    private String entitatKey;

    public boolean isBooleanValue() {
        return value!=null && value.equals("true");
    }
    public void setBooleanValue(boolean booleanValue) {
        this.value = booleanValue ? "true" : "false";
    }

    public boolean isEntitatBooleanValue() {
        return entitatKey!=null && entitatKey.equals("true");
    }

    public void setEntitatBooleanValue(boolean booleanValue) {
        this.entitatKey = booleanValue ? "true" : "false";
    }


    public ConfigDto asDto() {
        return ConfigDto.builder().key(this.key).value(this.value).build();
    }
}
