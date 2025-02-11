package es.caib.notib.api.interna.model.adviser;

import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.SincronizarEnvio;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Base64;

@Getter
@Setter
public class CieAdviser implements Serializable {

    private String organismoEmisor;
    @NotNull
    private String identificador;
    @NotNull
    private BigInteger tipoEntrega;
    @NotNull
    private BigInteger modoNotificacion;

    @NotNull
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

    public SincronizarEnvio asSincronizarEnvio() {

        es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Receptor r = null;
        if (receptor != null) {
            r = new es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Receptor();
            r.setNifReceptor(receptor.getNifReceptor());
            r.setNombreReceptor(receptor.getNombreReceptor());
            r.setVinculoReceptor(receptor.getVinculoReceptor());
            r.setNifRepresentante(receptor.getNifRepresentante());
            r.setNombreRepresentante(receptor.getNombreRepresentante());
            r.setCsvRepresetante(receptor.getCsvRepresetante());
        }

        es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Acuse aPdf = null;
        if (acusePDF != null) {
            aPdf = new es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Acuse();
            if (acusePDF.getContenido() != null) {
                aPdf.setContenido(Base64.getDecoder().decode(acusePDF.getContenido()));
            }
            aPdf.setHash(acusePDF.getHash());
            aPdf.setCsvResguardo(acusePDF.getCsvResguardo());
        }

        es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Acuse aXml = null;
        if (acuseXML != null) {
            aXml = new es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Acuse();
            if (acuseXML.getContenido() != null)
                aXml.setContenido(Base64.getDecoder().decode(acuseXML.getContenido()));
            aXml.setHash(acuseXML.getHash());
            aXml.setCsvResguardo(acuseXML.getCsvResguardo());
        }

        var sincronizarEnvio = new SincronizarEnvio();
        sincronizarEnvio.setOrganismoEmisor(organismoEmisor);
        sincronizarEnvio.setIdentificador(identificador);
        sincronizarEnvio.setTipoEntrega(tipoEntrega);
        sincronizarEnvio.setModoNotificacion(modoNotificacion);
        sincronizarEnvio.setEstado(estado);
        sincronizarEnvio.setFechaEstado(fechaEstado);
        sincronizarEnvio.setReceptor(r);
        sincronizarEnvio.setAcusePDF(aPdf);
        sincronizarEnvio.setAcuseXML(aXml);
        sincronizarEnvio.setOpcionesSincronizarEnvio(getOpciones(opcionesSincronizarEnvio));
        return sincronizarEnvio;
    }

    private es.caib.notib.logic.intf.ws.adviser.nexea.common.Opciones getOpciones(Opciones opciones) {

        var os = new es.caib.notib.logic.intf.ws.adviser.nexea.common.Opciones();
        es.caib.notib.logic.intf.ws.adviser.nexea.common.Opcion o;
        if (opciones != null) {
            for (var op : opciones.getOpcion()) {
                o = new es.caib.notib.logic.intf.ws.adviser.nexea.common.Opcion();
                o.setTipo(op.getTipo());
                o.setValue(op.getValue());
                os.getOpcion().add(o);
            }
        }
        return os;
    }

//    public void sethIdentificador(String hIdentificador) {
//        this.hIdentificador = hIdentificador;
//    }
//
//    public CieAdviser asDto() {
//
//        Receptor r = null;
//        if (receptor != null) {
//            r = new Receptor();
//            r.setNifReceptor(receptor.getNifReceptor());
//            r.setNombreReceptor(receptor.getNombreReceptor());
//            r.setVinculoReceptor(receptor.getVinculoReceptor());
//            r.setNifRepresentante(receptor.getNifRepresentante());
//            r.setNombreRepresentante(receptor.getNombreRepresentante());
//            r.setCsvRepresetante(receptor.getCsvRepresetante());
//        }
//
//        Acuse aPdf = null;
//        if (acusePDF != null) {
//            aPdf = new Acuse();
//            if (acusePDF.getContenido() != null)
//                aPdf.setContenido(Base64.getDecoder().decode(Arrays.toString(acusePDF.getContenido())));
//            aPdf.setHash(acusePDF.getHash());
//            aPdf.setCsvResguardo(acusePDF.getCsvResguardo());
//        }
//
//        Acuse aXml = null;
//        if (acuseXML != null) {
//            aXml = new Acuse();
//            if (acuseXML.getContenido() != null)
//                aXml.setContenido(Base64.getDecoder().decode(Arrays.toString(acuseXML.getContenido())));
//            aXml.setHash(acuseXML.getHash());
//            aXml.setCsvResguardo(acuseXML.getCsvResguardo());
//        }
//
//        var dto = new CieAdviser();
//        dto.setOrganismoEmisor(organismoEmisor);
//        dto.sethIdentificador(hIdentificador);
//        dto.setTipoEntrega(tipoEntrega);
//        dto.setModoNotificacion(modoNotificacion);
//        dto.setEstado(estado);
//        dto.setFechaEstado(fechaEstado);
//        dto.setReceptor(r);
//        dto.setAcusePDF(aPdf);
//        dto.setAcuseXML(aXml);
//        dto.setOpcionesSincronizarEnvio(getOpciones(opcionesSincronizarEnvio));
//        dto.setCodigoRespuesta(codigoRespuesta);
//        dto.setDescripcionRespuesta(descripcionRespuesta);
//        dto.setOpcionesResultadoSincronizarEnvio(getOpciones(opcionesResultadoSincronizarEnvio));
//        return dto;
//    }

}
