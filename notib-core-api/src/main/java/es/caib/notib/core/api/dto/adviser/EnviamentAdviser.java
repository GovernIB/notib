package es.caib.notib.core.api.dto.adviser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnviamentAdviser implements Serializable {

    private String organismoEmisor;
    private String hIdentificador;
    private BigInteger tipoEntrega;
    private BigInteger modoNotificacion;
    private String estado;
    private XMLGregorianCalendar fechaEstado;
    private Receptor receptor;
    private Acuse acusePDF;
    private Acuse acuseXML;
    private Opciones opcionesSincronizarEnvio;
    private String codigoRespuesta;
    private String descripcionRespuesta;
    private Opciones opcionesResultadoSincronizarEnvio;

}
