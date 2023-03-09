package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.AdviserResponseDto;
import es.caib.notib.core.api.dto.adviser.EnviamentAdviser;
import es.caib.notib.core.api.service.AdviserService;
import es.caib.notib.core.wsdl.adviser.Acuse;
import es.caib.notib.core.wsdl.adviser.AdviserWsV2PortType;
import es.caib.notib.core.wsdl.adviser.Opcion;
import es.caib.notib.core.wsdl.adviser.Opciones;
import es.caib.notib.core.wsdl.adviser.Receptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Holder;

@Service
public class
AdviserServiceImpl implements AdviserService {

    @Autowired
    private AdviserWsV2PortType adviser;

    @Override
    public AdviserResponseDto sincronitzarEnviament(EnviamentAdviser env) {

        Receptor receptor = new Receptor();
        receptor.setNifReceptor(env.getReceptor().getNifReceptor());
        receptor.setNombreReceptor(env.getReceptor().getNombreReceptor());
        receptor.setVinculoReceptor(env.getReceptor().getVinculoReceptor());
        receptor.setNifRepresentante(env.getReceptor().getNifRepresentante());
        receptor.setNombreRepresentante(env.getReceptor().getNombreRepresentante());
        receptor.setCsvRepresetante(env.getReceptor().getCsvRepresetante());

        Acuse acusePdf = new Acuse();
        acusePdf.setContenido(env.getAcusePDF().getContenido());
        acusePdf.setHash(env.getAcusePDF().getHash());
        acusePdf.setCsvResguardo(env.getAcusePDF().getCsvResguardo());

        Acuse acuseXml = new Acuse();
        acuseXml.setContenido(env.getAcusePDF().getContenido());
        acuseXml.setHash(env.getAcusePDF().getHash());
        acuseXml.setCsvResguardo(env.getAcusePDF().getCsvResguardo());

        Holder<String> identificador = new Holder<>(env.getHIdentificador());
        Holder<String> codigoRespuesta = new Holder<>(env.getCodigoRespuesta());
        Holder<String> descripcionRespuesta = new Holder<>(env.getDescripcionRespuesta());
        Holder<Opciones> opciones = new Holder<>(getOpciones(env.getOpcionesResultadoSincronizarEnvio()));

        adviser.sincronizarEnvio(
                env.getOrganismoEmisor(),
                identificador,
                env.getTipoEntrega(),
                env.getModoNotificacion(),
                env.getEstado(),
                env.getFechaEstado(),
                receptor,
                acusePdf,
                acuseXml,
                getOpciones(env.getOpcionesSincronizarEnvio()),
                codigoRespuesta,
                descripcionRespuesta,
                opciones);

        return AdviserResponseDto.builder()
                .identificador(env.getHIdentificador())
                .codigoRespuesta(codigoRespuesta.value)
                .descripcionRespuesta(descripcionRespuesta.value)
                .opcionesResultadoSincronizarEnvio(env.getOpcionesResultadoSincronizarEnvio())
                .build();
    }

    private Opciones getOpciones(es.caib.notib.core.api.dto.adviser.Opciones opciones) {

        Opciones os = new Opciones();
        Opcion o;
        for ( es.caib.notib.core.api.dto.adviser.Opcion op : opciones.getOpcion()) {
            o = new Opcion();
            o.setTipo(op.getTipo());
            o.setValue(o.getValue());
            os.getOpcion().add(o);
        }
        return os;
    }
}
