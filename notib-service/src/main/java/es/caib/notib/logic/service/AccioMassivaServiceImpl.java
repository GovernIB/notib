package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaFiltre;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.logic.intf.service.AccioMassivaService;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.JustificantService;
import es.caib.notib.persist.entity.AccioMassivaEntity;
import es.caib.notib.persist.repository.AccioMassivaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class AccioMassivaServiceImpl implements AccioMassivaService {

    @Autowired
    private AccioMassivaRepository accioMassivaRepository;
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    private EnviamentService enviamentService;
    @Autowired
    private JustificantService justificantService;


    @Override
    public PaginaDto<AccioMassivaDto> findAmbFiltre(AccioMassivaFiltre filtre, PaginacioParamsDto paginacioParams) {

        try {
            var pageable = getMappeigPropietats(paginacioParams);
            var accions = accioMassivaRepository.findAll(pageable);
            return paginacioHelper.toPaginaDto(accions, AccioMassivaDto.class);
        } catch (Exception ex) {
            var msg = "Error carregant les dades de la taula d'accions massives";
            log.error(msg, ex);
            throw ex;
        }
    }

    private Pageable getMappeigPropietats(PaginacioParamsDto paginacioParams) {

        Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
//        mapeigPropietatsOrdenacio.put("usuariCodi", new String[] {"usuariCodi"});
//        mapeigPropietatsOrdenacio.put("endpoint", new String[] {"usuariCodi"});
//        mapeigPropietatsOrdenacio.put("data", new String[] {"data"});

//        return paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
        return paginacioHelper.toSpringDataPageable(paginacioParams);
    }

    // Execucio d'accions massives

    @Override
    public Long altaAccioMassiva(AccioMassivaTipus accioMassivaTipus, Long entitatId) {

        try {
            var entity = AccioMassivaEntity.builder().tipus(accioMassivaTipus).entitatId(entitatId).build();
            entity = accioMassivaRepository.saveAndFlush(entity);
            return entity.getId();
        } catch (Exception ex) {
            log.error("Error creant l'accio massiva de tipus " + accioMassivaTipus + " per l'entitat " + entitatId, ex);
            throw ex;
        }
    }

    @Transactional
    @Override
    public FitxerDto exportar(AccioMassivaExecucio accio) throws Exception {

        var accioEntity = accioMassivaRepository.findById(accio.getAccioId()).orElseThrow();
        accioEntity.setDataInici(new Date());
        FitxerDto fitxer = null;
        try {
            fitxer = enviamentService.exportacio(accio.getEntitatId(), accio.getSeleccio(), accio.getFormat());
            accioEntity.setError(false);
        } catch (Exception ex) {
            log.error("Error en l'accio massiva exporatar", ex);
            accioEntity.setError(true);
            accioEntity.setErrorDescripcio(ex.getMessage());
            accioEntity.setExcepcioStacktrace(Arrays.toString(ex.getStackTrace()));
        }
        accioEntity.setDataFi(new Date());
        accioMassivaRepository.save(accioEntity);
        return fitxer;
    }

    @Override
    public List<FitxerDto> descarregarJustificant(AccioMassivaExecucio accio) {

        List<FitxerDto> justificants = new ArrayList<>();
        try {
            var accioEntity = accioMassivaRepository.findById(accio.getAccioId()).orElseThrow();
            accioEntity.setDataInici(new Date());
            var seqNum = 0;
            FitxerDto justificant;
            for (var notificacioId : accio.getSeleccio()) {
                var sequence = "sequence" + UUID.randomUUID();
                seqNum++;
                try {
                    justificant = justificantService.generarJustificantEnviament(notificacioId, accio.getEntitatId(), sequence);
                    if (justificant == null) {
                        log.error("[MASSIVA DESCARREGAR JUSTIFICANT] Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document. Notificacio id " + notificacioId);
                        continue;
                    }
                } catch (Exception ex) {
                    log.error("Error descarregant el justificant per la notificacio " + notificacioId + " " + ex.getMessage());
                    continue;
                }
                justificants.add(justificant);
            }
        } catch (Exception ex) {
            log.error("Error descarregant els justificants", ex);
        }
        return justificants;
    }
}


