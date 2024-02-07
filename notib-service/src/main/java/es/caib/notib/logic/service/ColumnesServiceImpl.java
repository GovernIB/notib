package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.intf.dto.Taula;
import es.caib.notib.logic.intf.dto.notenviament.ColumnesDto;
import es.caib.notib.logic.intf.service.ColumnesService;
import es.caib.notib.persist.entity.ColumnesEntity;
import es.caib.notib.persist.repository.ColumnesRemesesRepository;
import es.caib.notib.persist.repository.ColumnesRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ColumnesServiceImpl implements ColumnesService {

    @Autowired
    private MetricsHelper metricsHelper;
    @Autowired
    private EntityComprovarHelper entityComprovarHelper;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private ColumnesRepository columnesRepository;
    @Autowired
    private ColumnesRemesesRepository columnesRemesesRepository;
    @Autowired
    private UsuariRepository usuariRepository;

    @Override
    public void columnesCreate(String codiUsuari, Long entitatId, ColumnesDto columnes) {

        var timer = metricsHelper.iniciMetrica();
        try {
            var entitatEntity = entityComprovarHelper.comprovarEntitat(entitatId);
            var usuariEntity = usuariRepository.findByCodi(codiUsuari);
            if (columnes == null) {
                columnes = new ColumnesDto();
                columnes.setDataEnviament(true);
                columnes.setDir3Codi(true);
                columnes.setProCodi(true);
                columnes.setConcepte(true);
                columnes.setTitularNomLlinatge(true);
                columnes.setEstat(true);
            }
            // Dades generals de la notificació
            ColumnesEntity columnesEntity = ColumnesEntity.builder()
                    .dataEnviament(columnes.isDataEnviament())
                    .dataProgramada(columnes.isDataProgramada())
                    .notIdentificador(columnes.isNotIdentificador())
                    .proCodi(columnes.isProCodi())
                    .grupCodi(columnes.isGrupCodi())
                    .dir3Codi(columnes.isDir3Codi())
                    .usuari(columnes.isUsuari())
                    .enviamentTipus(columnes.isEnviamentTipus())
                    .concepte(columnes.isConcepte())
                    .descripcio(columnes.isDescripcio())
                    .titularNomLlinatge(columnes.isTitularNomLlinatge())
                    .titularEmail(columnes.isTitularEmail())
                    .destinataris(columnes.isDestinataris())
                    .llibreRegistre(columnes.isLlibreRegistre())
                    .numeroRegistre(columnes.isNumeroRegistre())
                    .dataRegistre(columnes.isDataRegistre())
                    .dataCaducitat(columnes.isDataCaducitat())
                    .codiNotibEnviament(columnes.isCodiNotibEnviament())
                    .numCertificacio(columnes.isNumCertificacio())
                    .csvUuid(columnes.isCsvUuid())
                    .estat(columnes.isEstat())
                    .entitat(entitatEntity)
                    .user(usuariEntity).build();

            columnesRepository.saveAndFlush(columnesEntity);
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }


    @Transactional
    @Override
    public void columnesUpdate(Long entitatId, ColumnesDto columnes) {

        var timer = metricsHelper.iniciMetrica();
        try {
            var columnesEntity = columnesRepository.findById(columnes.getId()).orElseThrow();
            columnesEntity.update(columnes);
            columnesRepository.saveAndFlush(columnesEntity);
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ColumnesDto getColumnesUsuari(Long entitatId, String codiUsuari, Taula taula) {

        var timer = metricsHelper.iniciMetrica();
        try {
            var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
            var usuari = usuariRepository.findByCodi(codiUsuari);
            var columnes = columnesRepository.findByEntitatAndUser(entitat, usuari);
            return conversioTipusHelper.convertir(columnes, ColumnesDto.class);
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }
}
