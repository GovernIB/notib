package es.caib.notib.logic.intf.dto;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AsientoRegistralBeanDto {
	
    protected List<AnexoWsDto> anexos;
    protected String aplicacion;
    protected String aplicacionTelematica;
    protected String codigoAsunto;
    protected String codigoAsuntoDenominacion;
    protected String codigoError;
    protected Long codigoSia;
    protected String codigoUsuario;
    protected String descripcionError;
    protected String entidadCodigo;
    protected String entidadDenominacion;
    protected String entidadRegistralDestinoCodigo;
    protected String entidadRegistralDestinoDenominacion;
    protected String entidadRegistralInicioCodigo;
    protected String entidadRegistralInicioDenominacion;
    protected String entidadRegistralOrigenCodigo;
    protected String entidadRegistralOrigenDenominacion;
    protected Long estado;
    protected String expone;
    protected XMLGregorianCalendar fechaRecepcion;
    protected XMLGregorianCalendar fechaRegistro;
    protected XMLGregorianCalendar fechaRegistroDestino;
    protected Long id;
    protected String identificadorIntercambio;
    protected Long idioma;
    protected List<InteresadoWsDto> interesados;
    protected String libroCodigo;
    protected String motivo;
    protected String numeroExpediente;
    protected int numeroRegistro;
    protected String numeroRegistroDestino;
    protected String numeroRegistroFormateado;
    protected String numeroTransporte;
    protected String observaciones;
    protected boolean presencial;
    protected String referenciaExterna;
    protected String resumen;
    protected String solicita;
    protected String tipoAsunto;
    protected String tipoAsuntoDenominacion;
    protected Long tipoDocumentacionFisicaCodigo;
    protected String tipoEnvioDocumentacion;
    protected Long tipoRegistro;
    protected String tipoTransporte;
    protected String unidadTramitacionDestinoCodigo;
    protected String unidadTramitacionDestinoDenominacion;
    protected String unidadTramitacionOrigenCodigo;
    protected String unidadTramitacionOrigenDenominacion;
    protected String version;
    
}
