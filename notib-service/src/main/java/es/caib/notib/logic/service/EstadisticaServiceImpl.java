package es.caib.notib.logic.service;

import es.caib.comanda.ms.estadistica.model.Dimensio;
import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.Fet;
import es.caib.comanda.ms.estadistica.model.Format;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistreEstadistic;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.comanda.ms.estadistica.model.Temps;
import es.caib.notib.client.domini.explotacio.*;
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
import es.caib.notib.persist.entity.explotacio.ExplotTempsEntity;
import es.caib.notib.persist.repository.explotacio.ExplotDimensioListRepository;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstadisticaServiceImpl implements EstadisticaService {

    private final ExplotTempsRepository explotTempsRepository;
    private final ExplotDimensioRepository explotDimensioRepository;
    private final ExplotFetsRepository explotFetsRepository;
    private final IntegracioHelper integracioHelper;
    private final ExplotDimensioListRepository explotDimensioListRepository;

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

        LocalDate data = ahir();
        ExplotTempsEntity temps = explotTempsRepository.findFirstByData(data);
        if (temps == null) {
            Date ahir = Date.from(ahir().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            return RegistresEstadistics.builder()
                    .temps(Temps.builder().data(ahir).build())
//                    .dimensionsDescripcio(getDimensions().stream()
//                            .map(d -> GenericDimensio.builder().nom(d.getNom()).valor(d.getDescripcio()).build())
//                            .collect(Collectors.toList()))
                    .fets(List.of())
                    .build();
        }
        List<ExplotFetsEntity> fets = explotFetsRepository.findByTemps(temps);

        return toRegistresEstadistics(fets, data);
    }

    @Override
    public List<DimensioDesc> getDimensions() {
        List<ExplotDimensio> dim = explotDimensioRepository.getDimensionsPerEstadistiques();
//        List<ExplotDimensio> dim = getDimensionsPerEstadistiques();
        return List.of(
                DimensioDesc.builder().nom("Entitat").descripcio("Codi de l'entitat a la que pertany la comunicació/notificació").valors(dim.stream().map(d -> d.getEntitatId().toString()).distinct().sorted().collect(Collectors.toList())).build(),
                DimensioDesc.builder().nom("Organ Gestor").descripcio("Organ gestor al que pertany la comunicació/notificació").valors(dim.stream().map(d -> d.getOrganCodi()).distinct().sorted().collect(Collectors.toList())).build(),
                DimensioDesc.builder().nom("Procediment").descripcio("Procediment al que pertany la comunicació/notificació").valors(dim.stream().map(d -> d.getProcedimentId() != null ? d.getProcedimentId().toString() : "").distinct().sorted().collect(Collectors.toList())).build(),
                DimensioDesc.builder().nom("Usuari").descripcio("Codi de l'usuari que ha creat la comunicació/notificació").valors(dim.stream().map(d -> d.getUsuariCodi()).distinct().sorted().collect(Collectors.toList())).build(),
                DimensioDesc.builder().nom("Tipus").descripcio("Tipus de comunicació oficial: notificació, comunicació o comunicació SIR").valors(dim.stream().map(d -> d.getTipus().name()).distinct().sorted().collect(Collectors.toList())).build(),
                DimensioDesc.builder().nom("Origen").descripcio("Des d'on s'ha creat la comunicació/notificació: des de la interfície web, des de la API Rest o com a enviament massiu").valors(dim.stream().map(d -> d.getOrigen().name()).distinct().sorted().collect(Collectors.toList())).build());

    }

    @Override
    public List<IndicadorDesc> getIndicadors() {
        return List.of(
                IndicadorDesc.builder().nom("Pendent").descripcio("La comunicació/notificació està pendent de ser registrada").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Error enviant a registre").descripcio("S'ha produït un error al intentar registrar la comunicació/notificació").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Registrada").descripcio("La comunicació/notificació ha estat registrada i està pendent de ser enviada al destinatari").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Registre SIR acceptat").descripcio("La comunicació SIR ha estat acceptada per l'administració destinatària").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Registre SIR rebutjat").descripcio("La comunicació SIR ha estat rebutjada per l'administració destinatària").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Error enviant a Notific@").descripcio("S'ha produït un error al intentar enviar la comunicació/notificació al destinatari mitjançant Notific@").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Enviada a Notific@").descripcio("La comunicació/notificació s'ha enviat a Notific@ i està pendent de compareixença del destinatari a DEHú").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Acceptada a Notific@").descripcio("La comunicació/notificació ha estat acceptada pel destinatari a DEHú").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Rebutjada a Notific@").descripcio("La comunicació/notificació ha estat rebutjada pel destinatari a DEHú").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Expirada a Notific@").descripcio("Ha passat el termini establert per a la compareixença del destinatari a DEHú").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Error enviant al CIE").descripcio("S'ha produït un error al intentar enviar la comunicació/notificació al destinatari mitjançant CIE + Operador postal").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Enviada a CIE").descripcio("La comunicació/notificació s'ha enviat al CIE i està pendent de ser entregada per l'operador postal").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Acceptada al CIE").descripcio("La comunicació/notificació ha estat entregada per l'operador postal al destinatari").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Rebutjada al CIE").descripcio("La comunicació/notificació s'ha intentat entregar per l'operador postal, però ha estat rebutjada pel destinatari").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Error en l'entrega per CIE").descripcio("S'ha produït algun problema que ha impedit realitzar la entrega postal").format(Format.LONG).build(),
                IndicadorDesc.builder().nom("Processada").descripcio("La comunicació/notificació ha estat processada").format(Format.LONG).build()
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
//        List<ExplotDimensio> dimensionsPerEstadistiques = getDimensionsPerEstadistiques();
        List<ExplotDimensioEntity> dimensionsEnDb = explotDimensioRepository.findAllOrdered();

        return actualitzarDimensions(dimensionsEnDb, dimensionsPerEstadistiques);
    }

//    private List<ExplotDimensio> getDimensionsPerEstadistiques() {
//        List<ExplotDimensioList> dimensions = explotDimensioListRepository.findAll();
//
//        return dimensions.stream().map(dim -> ExplotDimensio.builder()
//                .entitatId(dim.getEntitatId())
//                .procedimentId(dim.getProcedimentId())
//                .organCodi(dim.getOrganCodi())
//                .usuariCodi(dim.getUsuariCodi())
//                .tipus(dim.getTipus())
//                .origen(dim.getOrigen())
//                .build())
//                .collect(Collectors.toList());
//    }

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
                Date.from(dema().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));

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

    private int compareEstadistiquesAndDimensions(ExplotFets estadistiques, ExplotDimensioEntity dimension) {
        int comparison = estadistiques.getEntitatId().compareTo(dimension.getEntitatId());
        if (comparison != 0) return comparison;

        comparison = estadistiques.getProcedimentId().compareTo(dimension.getProcedimentId());
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
        ExplotFetsEntity fetsEntity = new ExplotFetsEntity();
        fetsEntity.setDimensio(dimension);
        fetsEntity.setTemps(ete);

        fetsEntity.setPendent(estadistiques.getPendent());
        fetsEntity.setRegEnviamentError(estadistiques.getRegEnviamentError());
        fetsEntity.setRegistrada(estadistiques.getRegistrada());
        fetsEntity.setRegAcceptada(estadistiques.getRegAcceptada());
        fetsEntity.setRegRebutjada(estadistiques.getRegRebutjada());
        fetsEntity.setNotEnviamentError(estadistiques.getNotEnviamentError());
        fetsEntity.setNotEnviada(estadistiques.getNotEnviada());
        fetsEntity.setNotNotificada(estadistiques.getNotNotificada());
        fetsEntity.setNotRebutjada(estadistiques.getNotRebutjada());
        fetsEntity.setNotExpirada(estadistiques.getNotExpirada());
        fetsEntity.setCieEnviamentError(estadistiques.getCieEnviamentError());
        fetsEntity.setCieEnviada(estadistiques.getCieEnviada());
        fetsEntity.setCieNotificada(estadistiques.getCieNotificada());
        fetsEntity.setCieRebutjada(estadistiques.getCieRebutjada());
        fetsEntity.setCieError(estadistiques.getCieError());
        fetsEntity.setProcessada(estadistiques.getProcessada());

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
                DimensioEntitat.builder().entitatId(dimensio.getEntitatId()).build(),
                DimensioProcediment.builder().procedimentId(dimensio.getProcedimentId()).build(),
                DimensioOrgan.builder().organCodi(dimensio.getOrganCodi()).build(),
                DimensioUsuari.builder().usuariCodi(dimensio.getUsuariCodi()).build(),
                DimensioTipus.builder().tipus(dimensio.getTipus()).build(),
                DimensioOrigen.builder().origen(dimensio.getOrigen()).build()
        );
    }

    private List<Fet> toFets(ExplotFetsEntity fet) {
        return List.of(
                FetPendent.builder().pendent(Double.valueOf(fet.getPendent())).build(),
                FetRegEnviamentError.builder().regEnviamentError(Double.valueOf(fet.getRegEnviamentError())).build(),
                FetRegistrada.builder().registrada(Double.valueOf(fet.getRegistrada())).build(),
                FetRegAcceptada.builder().regAcceptada(Double.valueOf(fet.getRegAcceptada())).build(),
                FetRegRebutjada.builder().regRebutjada(Double.valueOf(fet.getRegRebutjada())).build(),
                FetNotEnviamentError.builder().notEnviamentError(Double.valueOf(fet.getNotEnviamentError())).build(),
                FetNotEnviada.builder().notEnviada(Double.valueOf(fet.getNotEnviada())).build(),
                FetNotNotificada.builder().notNotificada(Double.valueOf(fet.getNotNotificada())).build(),
                FetNotRebutjada.builder().notRebutjada(Double.valueOf(fet.getNotRebutjada())).build(),
                FetNotExpirada.builder().notExpirada(Double.valueOf(fet.getNotExpirada())).build(),
                FetCieEnviamentError.builder().cieEnviamentError(Double.valueOf(fet.getCieEnviamentError())).build(),
                FetCieEnviada.builder().cieEnviada(Double.valueOf(fet.getCieEnviada())).build(),
                FetCieNotificada.builder().cieNotificada(Double.valueOf(fet.getCieNotificada())).build(),
                FetCieRebutjada.builder().cieRebutjada(Double.valueOf(fet.getCieRebutjada())).build(),
                FetCieError.builder().cieError(Double.valueOf(fet.getCieError())).build(),
                FetProcessada.builder().processada(Double.valueOf(fet.getProcessada())).build()
        );
    }

}
