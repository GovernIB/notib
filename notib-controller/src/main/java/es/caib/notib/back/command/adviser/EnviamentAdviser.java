package es.caib.notib.back.command.adviser;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.math.BigInteger;

@Getter
@Setter
public class EnviamentAdviser implements Serializable {

    private String organismoEmisor;
    private String hIdentificador;
    private BigInteger tipoEntrega;
    private BigInteger modoNotificacion;
    private String estado;
    private XMLGregorianCalendar fechaEstado;
    @NotNull
    @Valid
    private Receptor receptor;
    @Valid
    private Acuse acusePDF;
    @Valid
    private Acuse acuseXML;
    @Valid
    private Opciones opcionesSincronizarEnvio;
    private String codigoRespuesta;
    private String descripcionRespuesta;
    @Valid
    private Opciones opcionesResultadoSincronizarEnvio;

    public es.caib.notib.logic.intf.dto.adviser.EnviamentAdviser asDto() {

        es.caib.notib.logic.intf.dto.adviser.Receptor r = null;
        if (receptor != null) {
            r = new es.caib.notib.logic.intf.dto.adviser.Receptor();
            r.setNifReceptor(receptor.getNifReceptor());
            r.setNombreReceptor(receptor.getNombreReceptor());
            r.setVinculoReceptor(receptor.getVinculoReceptor());
            r.setNifRepresentante(receptor.getNifRepresentante());
            r.setNombreRepresentante(receptor.getNombreRepresentante());
            r.setCsvRepresetante(receptor.getCsvRepresetante());
        }

        es.caib.notib.logic.intf.dto.adviser.Acuse aPdf = null;
        if (acusePDF != null) {
            aPdf = new es.caib.notib.logic.intf.dto.adviser.Acuse();
            if (acusePDF.getContenido() != null)
                aPdf.setContenido(Base64.decodeBase64(acusePDF.getContenido()));
            aPdf.setHash(acusePDF.getHash());
            aPdf.setCsvResguardo(acusePDF.getCsvResguardo());
        }

        es.caib.notib.logic.intf.dto.adviser.Acuse aXml = null;
        if (acuseXML != null) {
            aXml = new es.caib.notib.logic.intf.dto.adviser.Acuse();
            if (acuseXML.getContenido() != null)
                aXml.setContenido(Base64.decodeBase64(acuseXML.getContenido()));
            aXml.setHash(acuseXML.getHash());
            aXml.setCsvResguardo(acuseXML.getCsvResguardo());
        }

        es.caib.notib.logic.intf.dto.adviser.EnviamentAdviser dto = new es.caib.notib.logic.intf.dto.adviser.EnviamentAdviser();
        dto.setOrganismoEmisor(organismoEmisor);
        dto.setHIdentificador(hIdentificador);
        dto.setTipoEntrega(tipoEntrega);
        dto.setModoNotificacion(modoNotificacion);
        dto.setEstado(estado);
        dto.setFechaEstado(fechaEstado);
        dto.setReceptor(r);
        dto.setAcusePDF(aPdf);
        dto.setAcuseXML(aXml);
        dto.setOpcionesSincronizarEnvio(getOpciones(opcionesSincronizarEnvio));
        dto.setCodigoRespuesta(codigoRespuesta);
        dto.setDescripcionRespuesta(descripcionRespuesta);
        dto.setOpcionesResultadoSincronizarEnvio(getOpciones(opcionesResultadoSincronizarEnvio));
        return dto;
    }

    private es.caib.notib.logic.intf.dto.adviser.Opciones getOpciones(Opciones opciones) {

        es.caib.notib.logic.intf.dto.adviser.Opciones  os = new es.caib.notib.logic.intf.dto.adviser.Opciones();
        es.caib.notib.logic.intf.dto.adviser.Opcion o;
        if (opciones != null) {
            for (es.caib.notib.back.command.adviser.Opcion op : opciones.getOpcion()) {
                o = new es.caib.notib.logic.intf.dto.adviser.Opcion();
                o.setTipo(op.getTipo());
                o.setValue(o.getValue());
                os.getOpcion().add(o);
            }
        }
        return os;
    }
}
