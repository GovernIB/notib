
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RespostaConsultaDadesRegistreV2 extends RespostaBase {

    private int numRegistre;
    private String numRegistreFormatat;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date dataRegistre;
    private byte[] justificant;
    private String oficina;
    private String llibre;
    private boolean enviamentSir;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date dataRecepcioSir;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date dataRegistreDestiSir;

}