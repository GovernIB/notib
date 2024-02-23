
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RespostaBase {

    private boolean error;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date errorData;
    private String errorDescripcio;

}
