package es.caib.notib.logic.service;

import es.caib.comanda.ms.estadistica.model.Dimensio;
import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.Fet;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistreEstadistic;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.comanda.ms.estadistica.model.Temps;
import es.caib.notib.client.domini.explotacio.DimEnum;
import es.caib.notib.client.domini.explotacio.DimensioNotib;
import es.caib.notib.client.domini.explotacio.FetNotib;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.service.EstadisticaService;
import es.caib.notib.persist.entity.explotacio.ExplotDimensio;
import es.caib.notib.persist.entity.explotacio.ExplotDimensioEntity;
import es.caib.notib.persist.entity.explotacio.ExplotFets;
import es.caib.notib.persist.entity.explotacio.ExplotFetsEntity;
import es.caib.notib.persist.entity.explotacio.ExplotFetsKey;
import es.caib.notib.persist.entity.explotacio.ExplotTempsEntity;
import es.caib.notib.persist.repository.explotacio.ExplotDimensioRepository;
import es.caib.notib.persist.repository.explotacio.ExplotFetsRepository;
import es.caib.notib.persist.repository.explotacio.ExplotTempsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static es.caib.comanda.ms.estadistica.model.Format.DECIMAL;
import static es.caib.comanda.ms.estadistica.model.Format.LONG;
import static es.caib.notib.client.domini.explotacio.FetEnum.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstadisticaServiceImpl implements EstadisticaService {

    private final ExplotTempsRepository explotTempsRepository;
    private final ExplotDimensioRepository explotDimensioRepository;
    private final ExplotFetsRepository explotFetsRepository;
    private final IntegracioHelper integracioHelper;

    @Override
    @Transactional(timeout = 3600)
    public void generarDadesExplotacio() {
        generarDadesExplotacio(ahir());
    }

    @Override
    @Transactional(timeout = 3600)
    public void generarDadesExplotacio(LocalDate data) {
        // Generar dades d'explotació
        String accioDesc = "GenerarDadesExplotacio - Recupera dades per taules d'explotació.";
        HashMap<String, String> accioParams = new HashMap<>();
        long t0 = System.currentTimeMillis();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        var info = new IntegracioInfo(
                IntegracioCodi.EXPLOTACIO,
                accioDesc,
                IntegracioAccioTipusEnumDto.PROCESSAR,
                new AccioParam("Data de recollida de dades", formatter.format(data)));

        try {

            log.debug("Iniciant procés de syncExplotacioDadesConvocatories. Data: " + data);
            if (data == null) data = ahir();


            ExplotTempsEntity ete = explotTempsRepository.findFirstByData(data);

            if (ete == null) {
                ete = new ExplotTempsEntity(data);
                ete = explotTempsRepository.save(ete);
            }

            List<ExplotDimensioEntity> dimensions = obtenirDimensions();
            actualitzarDadesEstadistiques(ete, dimensions);

            log.debug("Finalitzant procés de syncExplotacioDadesConvocatories, guardant a monitor de integracions.");

            integracioHelper.addAccioOk(info);

            log.debug("Finalitzat procés de generarDadesExplotacio.");
        } catch (Exception ex) {
            log.error("Error generant informació estadística", ex);
            integracioHelper.addAccioError(info, "Error generant informació estadística", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RegistresEstadistics consultaUltimesEstadistiques() {

        return consultaEstadistiques(ahir());
    }

    @Override
    @Transactional(readOnly = true)
    public RegistresEstadistics consultaEstadistiques(LocalDate data) {
        ExplotTempsEntity temps = explotTempsRepository.findFirstByData(data);
        if (temps == null) {
            Date dia = Date.from(ahir().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            return RegistresEstadistics.builder()
                    .temps(Temps.builder().data(dia).build())
                    .fets(List.of())
                    .build();
        }
        List<ExplotFetsEntity> fets = explotFetsRepository.findByTemps(temps);
        return toRegistresEstadistics(fets, data);
    }

    @Override
    public List<DimensioDesc> getDimensions() {
        List<ExplotDimensio> dim = explotDimensioRepository.getDimensionsPerEstadistiques();
        return List.of(
                DimensioDesc.builder().codi(DimEnum.ENT.name()).nom(DimEnum.ENT.getNom()).descripcio(DimEnum.ENT.getDescripcio()).valors(dim.stream().map(d -> Optional.ofNullable(d.getEntitatId()).map(Object::toString).orElse("")).filter(s -> !s.isEmpty()).distinct().sorted().collect(Collectors.toList())).build(),
                DimensioDesc.builder().codi(DimEnum.ORG.name()).nom(DimEnum.ORG.getNom()).descripcio(DimEnum.ORG.getDescripcio()).valors(dim.stream().map(d -> Optional.ofNullable(d.getOrganCodi()).orElse("")).filter(s -> !s.isEmpty()).distinct().sorted().collect(Collectors.toList())).build(),
                DimensioDesc.builder().codi(DimEnum.PRC.name()).nom(DimEnum.PRC.getNom()).descripcio(DimEnum.PRC.getDescripcio()).valors(dim.stream().map(d -> Optional.ofNullable(d.getProcedimentId()).map(Object::toString).orElse("")).distinct().sorted().collect(Collectors.toList())).build(),
                DimensioDesc.builder().codi(DimEnum.USU.name()).nom(DimEnum.USU.getNom()).descripcio(DimEnum.USU.getDescripcio()).valors(dim.stream().map(d -> Optional.ofNullable(d.getUsuariCodi()).orElse("DESCONEGUT")).distinct().sorted().collect(Collectors.toList())).build(),
                DimensioDesc.builder().codi(DimEnum.TIP.name()).nom(DimEnum.TIP.getNom()).descripcio(DimEnum.TIP.getDescripcio()).valors(dim.stream().map(d -> d.getTipus().name()).distinct().sorted().collect(Collectors.toList())).build(),
                DimensioDesc.builder().codi(DimEnum.ORI.name()).nom(DimEnum.ORI.getNom()).descripcio(DimEnum.ORI.getDescripcio()).valors(dim.stream().map(d -> d.getOrigen().name()).distinct().sorted().collect(Collectors.toList())).build()
        );
    }

    @Override
    public List<IndicadorDesc> getIndicadors() {
        return List.of(
                IndicadorDesc.builder().codi(PND.name()).nom(PND.getNom()).descripcio(PND.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(REG_ERR.name()).nom(REG_ERR.getNom()).descripcio(REG_ERR.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(REG.name()).nom(REG.getNom()).descripcio(REG.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(SIR_ACC.name()).nom(SIR_ACC.getNom()).descripcio(SIR_ACC.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(SIR_REB.name()).nom(SIR_REB.getNom()).descripcio(SIR_REB.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(NOT_ERR.name()).nom(NOT_ERR.getNom()).descripcio(NOT_ERR.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(NOT_ENV.name()).nom(NOT_ENV.getNom()).descripcio(NOT_ENV.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(NOT_ACC.name()).nom(NOT_ACC.getNom()).descripcio(NOT_ACC.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(NOT_REB.name()).nom(NOT_REB.getNom()).descripcio(NOT_REB.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(NOT_EXP.name()).nom(NOT_EXP.getNom()).descripcio(NOT_EXP.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(CIE_ERR.name()).nom(CIE_ERR.getNom()).descripcio(CIE_ERR.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(CIE_ENV.name()).nom(CIE_ENV.getNom()).descripcio(CIE_ENV.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(CIE_ACC.name()).nom(CIE_ACC.getNom()).descripcio(CIE_ACC.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(CIE_REB.name()).nom(CIE_REB.getNom()).descripcio(CIE_REB.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(CIE_FAL.name()).nom(CIE_FAL.getNom()).descripcio(CIE_FAL.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(PRC.name()).nom(PRC.getNom()).descripcio(PRC.getDescripcio()).format(LONG).build(),
                // Transicions
                IndicadorDesc.builder().codi(TR_CRE.name()).nom(TR_CRE.getNom()).descripcio(TR_CRE.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_REG_ERR.name()).nom(TR_REG_ERR.getNom()).descripcio(TR_REG_ERR.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_REG.name()).nom(TR_REG.getNom()).descripcio(TR_REG.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_SIR_ACC.name()).nom(TR_SIR_ACC.getNom()).descripcio(TR_SIR_ACC.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_SIR_REB.name()).nom(TR_SIR_REB.getNom()).descripcio(TR_SIR_REB.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_NOT_ERR.name()).nom(TR_NOT_ERR.getNom()).descripcio(TR_NOT_ERR.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_NOT_ENV.name()).nom(TR_NOT_ENV.getNom()).descripcio(TR_NOT_ENV.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_NOT_ACC.name()).nom(TR_NOT_ACC.getNom()).descripcio(TR_NOT_ACC.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_NOT_REB.name()).nom(TR_NOT_REB.getNom()).descripcio(TR_NOT_REB.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_NOT_EXP.name()).nom(TR_NOT_EXP.getNom()).descripcio(TR_NOT_EXP.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_NOT_FAL.name()).nom(TR_NOT_FAL.getNom()).descripcio(TR_NOT_FAL.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_CIE_ERR.name()).nom(TR_CIE_ERR.getNom()).descripcio(TR_CIE_ERR.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_CIE_ENV.name()).nom(TR_CIE_ENV.getNom()).descripcio(TR_CIE_ENV.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_CIE_ACC.name()).nom(TR_CIE_ACC.getNom()).descripcio(TR_CIE_ACC.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_CIE_REB.name()).nom(TR_CIE_REB.getNom()).descripcio(TR_CIE_REB.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_CIE_CAN.name()).nom(TR_CIE_CAN.getNom()).descripcio(TR_CIE_CAN.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_CIE_FAL.name()).nom(TR_CIE_FAL.getNom()).descripcio(TR_CIE_FAL.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_EML_ERR.name()).nom(TR_EML_ERR.getNom()).descripcio(TR_EML_ERR.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TR_EML_ENV.name()).nom(TR_EML_ENV.getNom()).descripcio(TR_EML_ENV.getDescripcio()).format(LONG).build(),

                // Temps mig en estat
                IndicadorDesc.builder().codi(TMP_PND.name()).nom(TMP_PND.getNom()).descripcio(TMP_PND.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_REG.name()).nom(TMP_REG.getNom()).descripcio(TMP_REG.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_NOT.name()).nom(TMP_NOT.getNom()).descripcio(TMP_NOT.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_CIE.name()).nom(TMP_CIE.getNom()).descripcio(TMP_CIE.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_TOT.name()).nom(TMP_TOT.getNom()).descripcio(TMP_TOT.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_REG_SAC.name()).nom(TMP_REG_SAC.getNom()).descripcio(TMP_REG_SAC.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_REG_SRB.name()).nom(TMP_REG_SRB.getNom()).descripcio(TMP_REG_SRB.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_REG_NOT.name()).nom(TMP_REG_NOT.getNom()).descripcio(TMP_REG_NOT.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_REG_EML.name()).nom(TMP_REG_EML.getNom()).descripcio(TMP_REG_EML.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_NOT_NOT.name()).nom(TMP_NOT_NOT.getNom()).descripcio(TMP_NOT_NOT.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_NOT_REB.name()).nom(TMP_NOT_REB.getNom()).descripcio(TMP_NOT_REB.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_NOT_EXP.name()).nom(TMP_NOT_EXP.getNom()).descripcio(TMP_NOT_EXP.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_NOT_FAL.name()).nom(TMP_NOT_FAL.getNom()).descripcio(TMP_NOT_FAL.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_CIE_NOT.name()).nom(TMP_CIE_NOT.getNom()).descripcio(TMP_CIE_NOT.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_CIE_REB.name()).nom(TMP_CIE_REB.getNom()).descripcio(TMP_CIE_REB.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_CIE_CAN.name()).nom(TMP_CIE_CAN.getNom()).descripcio(TMP_CIE_CAN.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_CIE_FAL.name()).nom(TMP_CIE_FAL.getNom()).descripcio(TMP_CIE_FAL.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_TOT_NAC.name()).nom(TMP_TOT_NAC.getNom()).descripcio(TMP_TOT_NAC.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_TOT_NRB.name()).nom(TMP_TOT_NRB.getNom()).descripcio(TMP_TOT_NRB.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_TOT_NEX.name()).nom(TMP_TOT_NEX.getNom()).descripcio(TMP_TOT_NEX.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_TOT_NFL.name()).nom(TMP_TOT_NFL.getNom()).descripcio(TMP_TOT_NFL.getDescripcio()).format(LONG).build(),
                IndicadorDesc.builder().codi(TMP_TOT_CAC.name()).nom(TMP_TOT_CAC.getNom()).descripcio(TMP_TOT_CAC.getDescripcio()).format(LONG).build(),

                // Intents
                IndicadorDesc.builder().codi(INT_REG.name()).nom(INT_REG.getNom()).descripcio(INT_REG.getDescripcio()).format(DECIMAL).build(),
                IndicadorDesc.builder().codi(INT_SIR.name()).nom(INT_SIR.getNom()).descripcio(INT_SIR.getDescripcio()).format(DECIMAL).build(),
                IndicadorDesc.builder().codi(INT_NOT.name()).nom(INT_NOT.getNom()).descripcio(INT_NOT.getDescripcio()).format(DECIMAL).build(),
                IndicadorDesc.builder().codi(INT_CIE.name()).nom(INT_CIE.getNom()).descripcio(INT_CIE.getDescripcio()).format(DECIMAL).build(),
                IndicadorDesc.builder().codi(INT_EML.name()).nom(INT_EML.getNom()).descripcio(INT_EML.getDescripcio()).format(DECIMAL).build()
        );
    }


    private LocalDate ahir() {
        return LocalDate.now().minusDays(1);
    }

    private LocalDate dema() {
        return LocalDate.now().plusDays(1);
    }

    private List<ExplotDimensioEntity> obtenirDimensions() {

        // Obtenim totes les dimensions per procedimentServei/usuari. Les que no existeixin les crearem
        List<ExplotDimensio> dimensionsPerEstadistiques = explotDimensioRepository.getDimensionsPerEstadistiques();
        List<ExplotDimensioEntity> dimensionsEnDb = explotDimensioRepository.findAllOrdered();

        return actualitzarDimensions(dimensionsEnDb, dimensionsPerEstadistiques);
    }

    private List<ExplotDimensioEntity> actualitzarDimensions(List<ExplotDimensioEntity> dimensionsEnDb, List<ExplotDimensio> dimensionsPerEstadistiques) {
        List<ExplotDimensioEntity> dimensions = new ArrayList<>();

        for (ExplotDimensio dimensioConsulta : dimensionsPerEstadistiques) {
            // Converteix la instància de ExplotConsultaDimensio a ExplotConsultaDimensioEntity
            ExplotDimensioEntity dimensioEntity = toConsultaDimensioEntity(dimensioConsulta);

            // Comprova si existeix a la base de dades, si no, la guarda
            int dimensioEntityIndex = dimensionsEnDb.indexOf(dimensioEntity);
            if (dimensioEntityIndex == -1) {
                dimensioEntity = explotDimensioRepository.save(dimensioEntity);
                dimensionsEnDb.add(dimensioEntity);
                dimensions.add(dimensioEntity);
            } else {
                dimensions.add(dimensionsEnDb.get(dimensioEntityIndex));
            }
        }

        return dimensions;
    }

    private ExplotDimensioEntity toConsultaDimensioEntity(ExplotDimensio dimensio) {
        return ExplotDimensioEntity.builder()
                .entitatId(dimensio.getEntitatId())
                .procedimentId(dimensio.getProcedimentId())
                .organCodi(dimensio.getOrganCodi())
                .usuariCodi(dimensio.getUsuariCodi())
                .tipus(dimensio.getTipus())
                .origen(dimensio.getOrigen())
                .build();
    }

    private void actualitzarDadesEstadistiques(ExplotTempsEntity ete, List<ExplotDimensioEntity> dimensions) {
        // Eliminam les dades d'explotació de la data
        explotFetsRepository.deleteAllByTemps(ete);

        List<ExplotFets> estadistiques = explotFetsRepository.getFetsPerEstadistiques(
                false,
                Date.from(ete.getData().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

        addAdditionalStats(estadistiques, ete.getData());

        int dadaIndex = 0;
        int dimensionIndex = 0;

        // Les dues llistes estan ordenades
        while (dadaIndex < estadistiques.size() && dimensionIndex < dimensions.size()) {
            ExplotFets estadistica = estadistiques.get(dadaIndex);
            ExplotDimensioEntity dimension = dimensions.get(dimensionIndex);

            int comparison = compareEstadistiquesAndDimensions(estadistica, dimension);
            if (comparison == 0) {
                saveToFetsEntity(estadistica, dimension, ete);
                dadaIndex++;
            } else if (comparison < 0) {
                dadaIndex++;
            } else {
                dimensionIndex++;
            }
        }
    }

    private void addAdditionalStats(List<ExplotFets> estadistiques, LocalDate localData) {
        Map<ExplotFetsKey, ExplotFets> statsMap = new HashMap<>();
        Date iniciDelDia = Date.from(localData.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date finalDelDia = Date.from(localData.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());


        estadistiques.forEach(stat -> statsMap.put(
                new ExplotFetsKey(stat.getEntitatId(), stat.getProcedimentId(), stat.getOrganCodi(), stat.getUsuariCodi(), stat.getTipus(), stat.getOrigen()),
                stat));

        // Notificacions creades
        explotFetsRepository.getStatsCreacioPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrCreades(stat.getTotalEnviaments());
        });

        // Notificacions amb error al intentar registrar
        explotFetsRepository.getStatsRegEnviamentErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrRegEnviadesError(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setIntentsRegistre(stat.getIntentsMig().longValue());
        });

        // Notificacions registrades
        explotFetsRepository.getStatsRegistradaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrRegistrades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigPendent(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setIntentsRegistre(stat.getIntentsMig().longValue());
        });

        // Comunicacions acceptades via SIR
        explotFetsRepository.getStatsRegAcceptadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrSirAcceptades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigRegistrada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigRegistradaPerSirAcceptada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setIntentsRegistre(stat.getIntentsMig().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Comunicacions rebutjades via SIR
        explotFetsRepository.getStatsRegRebutjadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrSirRebutjades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigRegistrada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigRegistradaPerSirRebutjada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setIntentsRegistre(stat.getIntentsMig().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions amb error al intentar enviar a Notifica
        explotFetsRepository.getStatsNotEnviamentErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrNotEnviadesError(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setIntentsRegistre(stat.getIntentsMig().longValue());
        });

        // Notificacions enviades a Notifica
        explotFetsRepository.getStatsNotEnviadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrNotEnviades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigRegistrada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigRegistradaPerNotificada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setIntentsNotEnviament(stat.getIntentsMig().longValue());
        });

        // Notificacions notificades via Notifica
        explotFetsRepository.getStatsNotNotificadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrNotNotificades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigNotEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigNotEnviadaPerNotificada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotalPerNotAcceptada(stat.getTempsTotal().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions rebutjades via Notifica
        explotFetsRepository.getStatsNotRebutjadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrNotRebujtades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigNotEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigNotEnviadaPerRebubjada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotalPerNotRebutjada(stat.getTempsTotal().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions expirades via Notifica
        explotFetsRepository.getStatsNotExpiradaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrNotExpirades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigNotEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigNotEnviadaPerExpirada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotalPerNotExpirada(stat.getTempsTotal().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions fallades via Notifica
        explotFetsRepository.getStatsNotErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrNotFallades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigNotEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigNotEnviadaPerFallada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotalPerNotFallada(stat.getTempsTotal().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions amb error al intentar enviar al CIE
        explotFetsRepository.getStatsCieEnviamentErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrCieEnviadesError(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setIntentsCieEnviament(stat.getIntentsMig().longValue());
        });

        // Notificacions enviades al CIE
        explotFetsRepository.getStatsCieEnviadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrCieEnviades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setIntentsCieEnviament(stat.getIntentsMig().longValue());
        });

        // Notificacions notificades via CIE
        explotFetsRepository.getStatsCieNotificadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrCieNotificades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigCieEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigCieEnviadaPerNotificada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotalPerCieAcceptada(stat.getTempsTotal().longValue());
            statsMap.get(stat.getKey()).setTemsMigTotal(stat.getTempsTotal().longValue());
        });

        // Notificacions rebutjades via CIE
        explotFetsRepository.getStatsCieRebutjadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrCieRebutjades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigCieEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigCieEnviadaPerRebubjada(stat.getTempsMigEstat().longValue());
        });

        // Notificacions cancel·lades via CIE
        explotFetsRepository.getStatsCieCanceladaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrCieCancelades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigCieEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigCieEnviadaPerCancelada(stat.getTempsMigEstat().longValue());
        });

        // Notificacions fallades via CIE
        explotFetsRepository.getStatsCieErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrCieFallades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigCieEnviada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigCieEnviadaPerFallada(stat.getTempsMigEstat().longValue());
        });

        // Notificacions amb error al intentar enviar via email
        explotFetsRepository.getStatsEmailEnviamentErrorPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrEmailEnviadesError(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setIntentsEmailEnviament(stat.getIntentsMig().longValue());
        });

        // Notificacions enviades via email
        explotFetsRepository.getStatsEmailEnviadaPerDay(iniciDelDia, finalDelDia).forEach(stat -> {
            statsMap.get(stat.getKey()).setTrEmailEnviades(stat.getTotalEnviaments());
            statsMap.get(stat.getKey()).setTemsMigRegistrada(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setTemsMigRegistradaPerEmail(stat.getTempsMigEstat().longValue());
            statsMap.get(stat.getKey()).setIntentsEmailEnviament(stat.getIntentsMig().longValue());
        });

    }

    private int compareEstadistiquesAndDimensions(ExplotFets estadistiques, ExplotDimensioEntity dimension) {
        int comparison = estadistiques.getEntitatId().compareTo(dimension.getEntitatId());
        if (comparison != 0) return comparison;

        if (estadistiques.getProcedimentId() == null && dimension.getProcedimentId() == null) {
            comparison = 0;
        } else if (estadistiques.getProcedimentId() == null) {
            comparison = -1;
        } else if (dimension.getProcedimentId() == null) {
            comparison = 1;
        } else {
            comparison = estadistiques.getProcedimentId().compareTo(dimension.getProcedimentId());
        }
        if (comparison != 0) return comparison;

        comparison = estadistiques.getOrganCodi().compareTo(dimension.getOrganCodi());
        if (comparison != 0) return comparison;

        comparison = estadistiques.getTipus().compareTo(dimension.getTipus());
        if (comparison != 0) return comparison;

        comparison = estadistiques.getOrigen().compareTo(dimension.getOrigen());
        if (comparison != 0) return comparison;

        return estadistiques.getUsuariCodi().compareTo(dimension.getUsuariCodi());
    }

    private void saveToFetsEntity(ExplotFets estadistiques, ExplotDimensioEntity dimension, ExplotTempsEntity ete) {
        ExplotFetsEntity fetsEntity = new ExplotFetsEntity(dimension, ete, estadistiques);
        explotFetsRepository.save(fetsEntity);
    }

    private RegistresEstadistics toRegistresEstadistics(List<ExplotFetsEntity> fets, LocalDate data) {
        Date dia = Date.from(data.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return RegistresEstadistics.builder()
                .temps(Temps.builder().data(dia).build())
                .fets(fets.stream().map(this::toRegistreEstadistic).collect(Collectors.toList()))
                .build();
    }

    private RegistreEstadistic toRegistreEstadistic(ExplotFetsEntity fet) {
        return RegistreEstadistic.builder()
                .dimensions(toDimensions(fet.getDimensio()))
                .fets(toFets(fet))
                .build();
    }

    private List<Dimensio> toDimensions(ExplotDimensioEntity dimensio) {
        return List.of(
                new DimensioNotib(DimEnum.ENT, dimensio.getEntitatId()),
                new DimensioNotib(DimEnum.PRC, dimensio.getProcedimentId()),
                new DimensioNotib(DimEnum.ORG, dimensio.getOrganCodi()),
                new DimensioNotib(DimEnum.USU, dimensio.getUsuariCodi()),
                new DimensioNotib(DimEnum.TIP, dimensio.getTipus()),
                new DimensioNotib(DimEnum.ORI, dimensio.getOrigen())
        );
    }

    private List<Fet> toFets(ExplotFetsEntity fet) {
        return List.of(
                new FetNotib(PND, fet.getPendent()),
                new FetNotib(REG_ERR, fet.getRegEnviamentError()),
                new FetNotib(REG, fet.getRegistrada()),
                new FetNotib(SIR_ACC, fet.getRegAcceptada()),
                new FetNotib(SIR_REB, fet.getRegRebutjada()),
                new FetNotib(NOT_ERR, fet.getNotEnviamentError()),
                new FetNotib(NOT_ENV, fet.getNotEnviada()),
                new FetNotib(NOT_ACC, fet.getNotNotificada()),
                new FetNotib(NOT_REB, fet.getNotRebutjada()),
                new FetNotib(NOT_EXP, fet.getNotExpirada()),
                new FetNotib(CIE_ERR, fet.getCieEnviamentError()),
                new FetNotib(CIE_ENV, fet.getCieEnviada()),
                new FetNotib(CIE_ACC, fet.getCieNotificada()),
                new FetNotib(CIE_REB, fet.getCieRebutjada()),
                new FetNotib(CIE_FAL, fet.getCieError()),
                new FetNotib(PRC, fet.getProcessada()),

                // Transicions
                new FetNotib(TR_CRE, fet.getTrCreades()),
                new FetNotib(TR_REG_ERR, fet.getTrRegEnviadesError()),
                new FetNotib(TR_REG, fet.getTrRegistrades()),
                new FetNotib(TR_SIR_ACC, fet.getTrSirAcceptades()),
                new FetNotib(TR_SIR_REB, fet.getTrSirRebutjades()),
                new FetNotib(TR_NOT_ERR, fet.getTrNotEnviadesError()),
                new FetNotib(TR_NOT_ENV, fet.getTrNotEnviades()),
                new FetNotib(TR_NOT_ACC, fet.getTrNotNotificades()),
                new FetNotib(TR_NOT_REB, fet.getTrNotRebujtades()),
                new FetNotib(TR_NOT_EXP, fet.getTrNotExpirades()),
                new FetNotib(TR_NOT_FAL, fet.getTrNotFallades()),
                new FetNotib(TR_CIE_ERR, fet.getTrCieEnviadesError()),
                new FetNotib(TR_CIE_ENV, fet.getTrCieEnviades()),
                new FetNotib(TR_CIE_ACC, fet.getTrCieNotificades()),
                new FetNotib(TR_CIE_REB, fet.getTrCieRebutjades()),
                new FetNotib(TR_CIE_CAN, fet.getTrCieCancelades()),
                new FetNotib(TR_CIE_FAL, fet.getTrCieFallades()),
                new FetNotib(TR_EML_ERR, fet.getTrEmailEnviadesError()),
                new FetNotib(TR_EML_ENV, fet.getTrEmailEnviades()),

                // Temps mig en estat
                new FetNotib(TMP_PND, fet.getTemsMigPendent()),
                new FetNotib(TMP_REG, fet.getTemsMigRegistrada()),
                new FetNotib(TMP_NOT, fet.getTemsMigNotEnviada()),
                new FetNotib(TMP_CIE, fet.getTemsMigCieEnviada()),
                new FetNotib(TMP_TOT, fet.getTemsMigTotal()),
                new FetNotib(TMP_REG_SAC, fet.getTemsMigRegistradaPerSirAcceptada()),
                new FetNotib(TMP_REG_SRB, fet.getTemsMigRegistradaPerSirRebutjada()),
                new FetNotib(TMP_REG_NOT, fet.getTemsMigRegistradaPerNotificada()),
                new FetNotib(TMP_REG_EML, fet.getTemsMigRegistradaPerEmail()),
                new FetNotib(TMP_NOT_NOT, fet.getTemsMigNotEnviadaPerNotificada()),
                new FetNotib(TMP_NOT_REB, fet.getTemsMigNotEnviadaPerRebubjada()),
                new FetNotib(TMP_NOT_EXP, fet.getTemsMigNotEnviadaPerExpirada()),
                new FetNotib(TMP_NOT_FAL, fet.getTemsMigNotEnviadaPerFallada()),
                new FetNotib(TMP_CIE_NOT, fet.getTemsMigCieEnviadaPerNotificada()),
                new FetNotib(TMP_CIE_REB, fet.getTemsMigCieEnviadaPerRebubjada()),
                new FetNotib(TMP_CIE_CAN, fet.getTemsMigCieEnviadaPerCancelada()),
                new FetNotib(TMP_CIE_FAL, fet.getTemsMigCieEnviadaPerFallada()),
                new FetNotib(TMP_TOT_NAC, fet.getTemsMigTotalPerNotAcceptada()),
                new FetNotib(TMP_TOT_NRB, fet.getTemsMigTotalPerNotRebutjada()),
                new FetNotib(TMP_TOT_NEX, fet.getTemsMigTotalPerNotExpirada()),
                new FetNotib(TMP_TOT_NFL, fet.getTemsMigTotalPerNotFallada()),
                new FetNotib(TMP_TOT_CAC, fet.getTemsMigTotalPerCieAcceptada()),

                // Intents
                new FetNotib(INT_REG, fet.getIntentsRegistre()),
                new FetNotib(INT_SIR, fet.getIntentsSir()),
                new FetNotib(INT_NOT, fet.getIntentsNotEnviament()),
                new FetNotib(INT_CIE, fet.getIntentsCieEnviament()),
                new FetNotib(INT_EML, fet.getIntentsEmailEnviament())
        );
    }

    private Double toDouble(Long value) {
        return value == null ? null : Double.valueOf(value);
    }

}
