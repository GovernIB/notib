package es.caib.notib.core.api.dto;

import es.caib.notib.core.api.dto.adviser.Acuse;
import es.caib.notib.core.api.dto.adviser.Opciones;
import es.caib.notib.core.api.dto.adviser.Receptor;
import lombok.Getter;
import lombok.Setter;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
public class EnviamentAdviser implements Serializable {

    private String organismoEmisor;
    private Holder<String> hIdentificador;
    private BigInteger tipoEntrega;
    private BigInteger modoNotificacion;
    private String estado;
    private XMLGregorianCalendar fechaEstado;
    private Receptor receptor;
    private Acuse acusePDF;
    private Acuse acuseXML;
    private Opciones opcionesSincronizarEnvio;
    private Holder<String> codigoRespuesta;
    private Holder<String> descripcionRespuesta;
    private Holder<Opciones> opcionesResultadoSincronizarEnvio;

}
