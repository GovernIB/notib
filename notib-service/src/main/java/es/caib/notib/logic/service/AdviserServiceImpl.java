package es.caib.notib.logic.service;


import es.caib.notib.logic.intf.dto.adviser.EnviamentAdviser;
import es.caib.notib.logic.intf.service.AdviserService;
import es.caib.notib.logic.intf.ws.adviser.Acuse;
import es.caib.notib.logic.intf.ws.adviser.AdviserWsV2PortType;
import es.caib.notib.logic.intf.ws.adviser.Opciones;
import es.caib.notib.logic.intf.ws.adviser.Opcion;
import es.caib.notib.logic.intf.ws.adviser.Receptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Holder;

//@Service
public class AdviserServiceImpl implements AdviserService {

//    @Autowired
    private AdviserWsV2PortType adviser;

    @Override
    public void sincronitzarEnviament(EnviamentAdviser env) {

        Receptor r = new Receptor();
        r.setNifReceptor(env.getReceptor().getNifReceptor());
        r.setNombreReceptor(env.getReceptor().getNombreReceptor());
        r.setVinculoReceptor(env.getReceptor().getVinculoReceptor());
        r.setNifRepresentante(env.getReceptor().getNifRepresentante());
        r.setNombreRepresentante(env.getReceptor().getNombreRepresentante());
        r.setCsvRepresetante(env.getReceptor().getCsvRepresetante());

        Acuse aPdf = new Acuse();
        aPdf.setContenido(env.getAcusePDF().getContenido());
        aPdf.setHash(env.getAcusePDF().getHash());
        aPdf.setCsvResguardo(env.getAcusePDF().getCsvResguardo());

        Acuse aXml = new Acuse();
        aXml.setContenido(env.getAcusePDF().getContenido());
        aXml.setHash(env.getAcusePDF().getHash());
        aXml.setCsvResguardo(env.getAcusePDF().getCsvResguardo());

        adviser.sincronizarEnvio(env.getOrganismoEmisor(), new Holder<>(env.getHIdentificador()), env.getTipoEntrega(), env.getModoNotificacion(), env.getEstado(),
                env.getFechaEstado(), r, aPdf, aXml, getOpciones(env.getOpcionesSincronizarEnvio()), new Holder<>(env.getCodigoRespuesta()),
                new Holder<>(env.getDescripcionRespuesta()), new Holder<>(getOpciones(env.getOpcionesResultadoSincronizarEnvio())));
    }
    private Opciones getOpciones(es.caib.notib.logic.intf.dto.adviser.Opciones opciones) {

        var os = new Opciones();
        Opcion o;
        for (es.caib.notib.logic.intf.dto.adviser.Opcion op : opciones.getOpcion()) {
            o = new Opcion();
            o.setTipo(op.getTipo());
            o.setValue(o.getValue());
            os.getOpcion().add(o);
        }
        return os;
    }
}