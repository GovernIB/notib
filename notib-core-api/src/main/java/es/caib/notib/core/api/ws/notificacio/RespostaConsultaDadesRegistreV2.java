package es.caib.notib.core.api.ws.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
