
package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
public class RespostaConsultaDadesRegistreV2 extends RespostaBase {

    private int numRegistre;
    private String numRegistreFormatat;
    private Date dataRegistre;
    private byte[] justificant;
    private String oficina;
    private String llibre;
    private boolean enviamentSir;
    private Date dataRecepcioSir;
    private Date dataRegistreDestiSir;

}