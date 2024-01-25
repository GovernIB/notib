package es.caib.notib.logic.statemachine.mappers;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.HibernateHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.RegistreSmHelper;
import es.caib.notib.logic.intf.dto.AnexoWsDto;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.InteresadoWsDto;
import es.caib.notib.logic.intf.dto.notificacio.EnviamentSirTipusDocumentEnviarEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.statemachine.dto.CodiNomDto;
import es.caib.notib.logic.intf.statemachine.dto.DocumentRegistreDto;
import es.caib.notib.logic.intf.statemachine.dto.DocumentRegistreDto.DocumentRegistreDtoBuilder;
import es.caib.notib.logic.intf.statemachine.dto.EnviamentRegistreDto;
import es.caib.notib.logic.intf.statemachine.dto.InteressatRegistreDto;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.registre.TipusRegistreRegweb3Enum;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static es.caib.notib.logic.intf.dto.notificacio.EnviamentSirTipusDocumentEnviarEnumDto.BINARI;
import static es.caib.notib.logic.intf.dto.notificacio.EnviamentSirTipusDocumentEnviarEnumDto.TOT;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class EnviamentRegistreMapper {

    @Autowired
    protected PluginHelper pluginHelper;
    @Autowired
    protected ConfigHelper configHelper;
    @Autowired
    protected PersonaMapper personaMapper;
    @Autowired
    protected DocumentMapper documentMapper;

    @Mapping(source = "notificaReferencia", target = "uuid")
    @Mapping(source = "notificacio.entitat.codi", target = "entitatCodi")
    @Mapping(source = "notificacio.entitat.nom", target = "entitatNom")
    @Mapping(source = "notificacio.entitat.dir3Codi", target = "entitatDir3Codi")
    @Mapping(source = "notificacio.id", target = "notificacioId")
    @Mapping(source = "notificacio.concepte", target = "concepte")
    @Mapping(source = "notificacio.enviamentTipus", target = "tipusEnviament")
    @Mapping(source = "notificacio.estat", target = "notificacioEstat")
    @Mapping(source = "notificacio.descripcio", target = "descripcio")
    @Mapping(source = "notificacio.idioma", target = "idioma")
    @Mapping(source = "notificacio.numExpedient", target = "numExpedient")
    @Mapping(source = "notificacio.usuariCodi", target = "usuariCodi")
    @Mapping(source = "notificacio.procediment.codi", target = "procedimentCodi")
    @Mapping(source = "notificacio.procediment.tipusAssumpte", target = "tipusAssumpte")
    @Mapping(source = "notificacio.procediment.codiAssumpte", target = "codiAssumpte")
    @Mapping(source = "notificacio", target = "codiDir3Registre", qualifiedByName = "codiDir3Registre")
    @Mapping(source = "notificacio", target = "organismeRegistre", qualifiedByName = "organismeRegistre")
    @Mapping(source = "notificacio", target = "oficinaRegistre", qualifiedByName = "oficinaRegistre")
    @Mapping(source = "notificacio", target = "llibreRegistre", qualifiedByName = "llibreRegistre")
    @Mapping(source = ".", target = "interessat", qualifiedByName = "interessatRegistre")
    @Mapping(source = "notificacio", target = "documents", qualifiedByName = "documentsRegistre")
    public abstract EnviamentRegistreDto toDto(NotificacioEnviamentEntity enviament);

//    @Mapping(source = "entitatDir3Codi", target = "entidadCodigo")
//    @Mapping(source = "entitatNom", target = "entidadDenominacion")
//    @Mapping(source = "oficinaRegistre.codi", target = "entidadRegistralOrigenCodigo")
//    @Mapping(source = "oficinaRegistre.nom", target = "entidadRegistralOrigenDenominacion")
//    @Mapping(source = "llibreRegistre.codi", target = "libroCodigo")
//    @Mapping(source = "organismeRegistre", target = "unidadTramitacionOrigenCodigo")
//    @Mapping(source = "organismeRegistre", target = "unidadTramitacionOrigenDenominacion")
//    @Mapping(source = "organismeRegistre", target = "unidadTramitacionDestinoCodigo")
//    @Mapping(source = "organismeRegistre", target = "unidadTramitacionDestinoDenominacion")
//    @Mapping(target = "tipoRegistro", constant = "2L")
//    @Mapping(source = ".", target = "resumen", qualifiedByName = "resumen")
//    @Mapping(target = "tipoDocumentacionFisicaCodigo", constant = "3L")
//    @Mapping(source = "tipusAssumpte", target = "tipoAsunto")
//    @Mapping(source = "tipusAssumpte", target = "tipoAsuntoDenominacion")
//    @Mapping(source = "codiAssumpte", target = "codigoAsunto")
//    @Mapping(source = "codiAssumpte", target = "codigoAsuntoDenominacion")
//    @Mapping(source = "numExpedient", target = "numeroExpediente")
//    @Mapping(source = "procedimentCodi", target = "codigoSia")
//    @Mapping(source = "usuariCodi", target = "codigoUsuario")
//    @Mapping(source = ".", target = "aplicacionTelematica", qualifiedByName = "versio")
//    @Mapping(target = "aplicacion", constant = "RWE")
//    @Mapping(target = "version", constant = "3.1")
//    @Mapping(source = ".", target = "observaciones", qualifiedByName = "observacions")
//    @Mapping(target = "expone", constant = "")
//    @Mapping(target = "solicita", constant = "")
//    @Mapping(target = "presencial", constant = "false")
//    @Mapping(source = "notificacioEstat", target = "estado", qualifiedByName = "estat")
//    @Mapping(source = "descripcio", target = "motivo")
//    @Mapping(source = "interessat", target = "interesados", qualifiedByName = "interessat")
//    @Mapping(source = ".", target = "anexos", qualifiedByName = "documents")
//    public abstract AsientoRegistralBeanDto toAsientoRegistral(EnviamentRegistreDto enviamentRegistreDto);

    @Mapping(source = "notificacio.entitat.codi", target = "entidadCodigo")
    @Mapping(source = "notificacio.entitat.nom", target = "entidadDenominacion")
    @Mapping(target = "tipoRegistro", constant = "2L")
    @Mapping(source = ".", target = "resumen", qualifiedByName = "resumen")
    @Mapping(target = "tipoDocumentacionFisicaCodigo", constant = "3L")
    @Mapping(source = "notificacio.procediment.tipusAssumpte", target = "tipoAsunto")
    @Mapping(source = "notificacio.procediment.tipusAssumpte", target = "tipoAsuntoDenominacion")
    @Mapping(source = "notificacio.procediment.codiAssumpte", target = "codigoAsunto")
    @Mapping(source = "notificacio.procediment.codiAssumpte", target = "codigoAsuntoDenominacion")
    @Mapping(source = "notificacio.numExpedient", target = "numeroExpediente")
    @Mapping(source = "notificacio.procediment.codi", target = "codigoSia")
    @Mapping(source = "notificacio.usuariCodi", target = "codigoUsuario")
    @Mapping(source = "notificacio.idioma", target = "idioma", qualifiedByName = "idioma")
    @Mapping(source = ".", target = "aplicacionTelematica", qualifiedByName = "versio")
    @Mapping(target = "aplicacion", constant = "RWE")
    @Mapping(target = "version", constant = "3.1")
    @Mapping(source = ".", target = "observaciones", qualifiedByName = "observacions")
    @Mapping(target = "expone", constant = "")
    @Mapping(target = "solicita", constant = "")
    @Mapping(target = "presencial", constant = "false")
    @Mapping(source = "notificacio.estat", target = "estado", qualifiedByName = "estat")
    @Mapping(source = "notificacio.descripcio", target = "motivo")
    public abstract AsientoRegistralBeanDto toAsientoRegistral(NotificacioEnviamentEntity enviament);

    @AfterMapping
    protected void beforeSet(NotificacioEnviamentEntity enviament, @MappingTarget AsientoRegistralBeanDto asientoRegistralBeanDto) {
        if (enviament == null) {
            return;
        }

        var notificacio = enviament.getNotificacio();

        // Òrgan
        String organ = getOrganismeRegistre(notificacio);
        asientoRegistralBeanDto.setUnidadTramitacionOrigenCodigo(organ);
        asientoRegistralBeanDto.setUnidadTramitacionOrigenDenominacion(organ);
        asientoRegistralBeanDto.setUnidadTramitacionDestinoCodigo(organ);
        asientoRegistralBeanDto.setUnidadTramitacionDestinoDenominacion(organ);

        // LLibre
        CodiNomDto llibre = getLlibreRegistre(notificacio);
        RegistreSmHelper.llibres.put(llibre.getCodi(), llibre.getNom());
        asientoRegistralBeanDto.setLibroCodigo(llibre != null ? llibre.getCodi() : null);

        // Oficina
        CodiNomDto oficina = getOficinaRegistre(notificacio);
        asientoRegistralBeanDto.setEntidadRegistralOrigenCodigo(oficina != null ? oficina.getCodi() : null);
        asientoRegistralBeanDto.setEntidadRegistralOrigenDenominacion(oficina != null ? oficina.getNom() : null);

        // Interessat
        var interessat = getInteressatRegistre(enviament);
        if (interessat != null) {
            InteresadoWsDto interesadoWsDto = new InteresadoWsDto();
            if (interessat.getTitular() != null) {
                interesadoWsDto.setInteresado(personaMapper.toInteresadoWs(interessat.getTitular()));
            }
            if (interessat.getRepresentant() != null) {
                interesadoWsDto.setRepresentante(personaMapper.toInteresadoWs(interessat.getRepresentant()));
            }
            asientoRegistralBeanDto.setInteresados(List.of(interesadoWsDto));
        }

        // Documents
        var documents = getDocumentsRegistre(notificacio);
        if (documents != null) {
            asientoRegistralBeanDto.setAnexos(documents.stream().map(d -> documentMapper.toAnexoWs(d)).collect(Collectors.toList()));
        }
    }

    @Named("codiDir3Registre")
    public static String getCodiDir3Registre(NotificacioEntity notificacio) {
        if (notificacio == null) {
            return null;
        }
        if (notificacio.getEntitat() == null) {
            return null;
        }
        if (!Strings.isNullOrEmpty(notificacio.getEntitat().getDir3CodiReg())) {
            return notificacio.getEntitat().getDir3CodiReg();
        }
        return notificacio.getEntitat().getDir3Codi();
    }

    @Named("organismeRegistre")
    public static String getOrganismeRegistre(NotificacioEntity notificacio) {
        if (notificacio == null) {
            return null;
        }
        if (notificacio.getProcediment() != null && notificacio.getProcediment().getOrganGestor() != null) {
            return notificacio.getProcediment().getOrganGestor().getCodi();
        }
        return notificacio.getOrganGestor() != null ? notificacio.getOrganGestor().getCodi() : null;
    }

    @Named("oficinaRegistre")
    protected CodiNomDto getOficinaRegistre(NotificacioEntity notificacio) {
        if (notificacio == null) {
            return null;
        }
        if (notificacio.getEntitat() == null) {
            return null;
        }
        var entitat = notificacio.getEntitat();

        if (entitat.isOficinaEntitat() && entitat.getOficina() != null) {
            return CodiNomDto.builder().codi(entitat.getOficina()).nom(entitat.getOficina()).build();
        }

        var organGestor = notificacio.getOrganGestor();
        if (!entitat.isOficinaEntitat() && organGestor != null && organGestor.getOficina() != null) {
            return CodiNomDto.builder().codi(organGestor.getOficina()).nom(organGestor.getOficinaNom()).build();
        }

        var dir3Codi = getCodiDir3Registre(notificacio);
        if (dir3Codi != null) {
            try {
                var oficinaVirtual = pluginHelper.llistarOficinaVirtual(dir3Codi, notificacio.getEntitat().getNomOficinaVirtual(), TipusRegistreRegweb3Enum.REGISTRE_SORTIDA);
                if (oficinaVirtual != null) {
                    return CodiNomDto.builder().codi(oficinaVirtual.getCodi()).nom(oficinaVirtual.getNom()).build();
                }
            } catch (Exception ex) {
                log.error("Error obtenint oficina virtual", ex);
            }
        }

        return null;
    }

    @Named("llibreRegistre")
    protected CodiNomDto getLlibreRegistre(NotificacioEntity notificacio) {
        if (notificacio == null) {
            return null;
        }
        if (notificacio.getEntitat() == null) {
            return null;
        }

        String organGestorCodi = null;
        var entitat = notificacio.getEntitat();
        var dir3Codi = getCodiDir3Registre(notificacio);

        if (entitat.isLlibreEntitat()) {
            if(entitat.getLlibre() != null) {
                return CodiNomDto.builder().codi(entitat.getLlibre()).nom(entitat.getLlibreNom()).build();
            }
            organGestorCodi = dir3Codi;
        }

        if (!entitat.isLlibreEntitat()) {
            var procediment = notificacio.getProcediment();
            var organGestor = procediment != null ? procediment.getOrganGestor() : notificacio.getOrganGestor();
            if (organGestor != null && organGestor.getLlibre() != null) {
                return CodiNomDto.builder().codi(organGestor.getLlibre()).nom(organGestor.getLlibreNom()).build();
            }
            if (organGestor != null) {
                organGestorCodi = organGestor.getCodi();
            }
        }

        if (dir3Codi != null && organGestorCodi != null) {
            try {
                var llibreOrganisme = pluginHelper.llistarLlibreOrganisme(dir3Codi, organGestorCodi);
                if (llibreOrganisme != null) {
                    return CodiNomDto.builder().codi(llibreOrganisme.getCodi()).nom(llibreOrganisme.getNomCurt()).build();
                }
            } catch (Exception ex) {
                log.error("Error obtenint llibre", ex);
            }
        }

        return null;
    }

    @Named("interessatRegistre")
    protected InteressatRegistreDto getInteressatRegistre(NotificacioEnviamentEntity enviament) {
        if (enviament == null) {
            return null;
        }
        if(enviament.getTitular() == null && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
            return null;
        }

        var interessat = InteressatRegistreDto.builder().build();
        var titular = enviament.getTitular();
        var representant = enviament.getDestinataris() != null && !enviament.getDestinataris().isEmpty() ? enviament.getDestinataris().get(0) : null;

        if(titular != null) {
            var titularRegistre = personaMapper.toDto(titular);
            interessat.setTitular(titularRegistre);
        }
        if(representant != null && titular != null && titular.isIncapacitat()) {
            var representantRegistre = personaMapper.toDto(representant);
            interessat.setRepresentant(representantRegistre);
        }
        return interessat;

    }

    @Named("documentsRegistre")
    protected List<DocumentRegistreDto> getDocumentsRegistre(NotificacioEntity notificacio) {
        if (notificacio == null) {
            return null;
        }
        if (notificacio.getEntitat() != null) {
            configHelper.setEntitatCodi(notificacio.getEntitat().getCodi());
        }
        var isComunicacioSir = EnviamentTipus.SIR.equals(notificacio.getEnviamentTipus());
        var isSirActivat = configHelper.getConfigAsBoolean("es.caib.notib.emprar.sir");
        var isSendDocumentsActive = configHelper.getConfigAsBoolean("es.caib.notib.plugin.registre.documents.enviar");
        var inclouDocuments = isSendDocumentsActive || (isSirActivat && isComunicacioSir);

        if (inclouDocuments && notificacio.getDocument() != null) {
            List<DocumentRegistreDto> documents = new ArrayList<>();
            if (notificacio.getDocument() != null) {
                documents.add(getDocumentRegistre(notificacio.getDocument(), 1, isComunicacioSir));
            }
            if (notificacio.getDocument2() != null) {
                documents.add(getDocumentRegistre(notificacio.getDocument2(), 2, isComunicacioSir));
            }
            if (notificacio.getDocument3() != null) {
                documents.add(getDocumentRegistre(notificacio.getDocument3(), 3, isComunicacioSir));
            }
            if (notificacio.getDocument4() != null) {
                documents.add(getDocumentRegistre(notificacio.getDocument4(), 4, isComunicacioSir));
            }
            if (notificacio.getDocument5() != null) {
                documents.add(getDocumentRegistre(notificacio.getDocument5(), 5, isComunicacioSir));
            }
            return documents;
        }
        return null;
    }

    protected DocumentRegistreDto getDocumentRegistre(DocumentEntity document, int idx, boolean isComunicacioSir) {

        if (document == null) {
            return null;
        }

        try {
            if (HibernateHelper.isProxy(document)) document = HibernateHelper.deproxy(document);

            var enviarContingut = !isComunicacioSir || (isComunicacioSir && (TOT.equals(getEnviamentSirTipusDocumentEnviar()) || BINARI.equals(getEnviamentSirTipusDocumentEnviar())));
            var isUuid = document.getUuid() != null;
            var isCsv = document.getCsv() != null;
            var isContingut = !Strings.isNullOrEmpty(document.getArxiuGestdocId());
            var titol = "Annex " + idx;

            if(isUuid || isCsv) {
                var csv = document.getCsv();
                var docDetall = pluginHelper.arxiuDocumentConsultar(isUuid ? document.getUuid() : document.getCsv(), null, enviarContingut, isUuid);

                if (isUuid && docDetall != null && docDetall.getMetadades() != null) { // && enviarCsv ) {
                    // Recuperar csv
                    var metadadesAddicionals = docDetall.getMetadades().getMetadadesAddicionals();
                    if (metadadesAddicionals != null) {
                        if (metadadesAddicionals.containsKey("csv")) {
                            csv = (String) metadadesAddicionals.get("csv");
                        } else if (metadadesAddicionals.containsKey("eni:csv")) {
                            csv = (String) metadadesAddicionals.get("eni:csv");
                        }
                    }
                }

                DocumentRegistreDtoBuilder builder = DocumentRegistreDto.builder()
                        .titol(titol)
                        .csv(csv)
                        .nom(document.getArxiuNom());
                if (enviarContingut && docDetall.getContingut() != null) {
                    builder.nom(docDetall.getContingut().getArxiuNom())
                            .contingut(docDetall.getContingut().getContingut())
                            .mimeType(document.getMediaType());
                }
                if (docDetall != null && docDetall.getMetadades() != null) {
                    if (docDetall.getMetadades().getTipusDocumental() != null) {
                        builder.tipusDocumental(TipusDocumentalEnum.valorAsEnum(docDetall.getMetadades().getTipusDocumental().toString()));
                    } else if (docDetall.getMetadades().getTipusDocumentalAddicional() != null) {
                        builder.tipusDocumental(TipusDocumentalEnum.valorAsEnum(docDetall.getMetadades().getTipusDocumentalAddicional()));
                    }
                    builder.origen(OrigenEnum.valorAsEnum(docDetall.getMetadades().getOrigen().ordinal()))
                            .dataCaptura(docDetall.getMetadades().getDataCaptura())
                            .validesa(estatElaboracioToValidesa(docDetall.getMetadades().getEstatElaboracio()))
                            .modeFirma("application/pdf".equals(document.getMediaType()) && docDetall.getFirmes() != null && !docDetall.getFirmes().isEmpty());
                }

                return builder.build();
            }

            if(isContingut) {
                var output = new ByteArrayOutputStream();
                pluginHelper.gestioDocumentalGet(document.getArxiuGestdocId(), pluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, output);
                return DocumentRegistreDto.builder()
                        .titol(titol)
                        .nom(document.getArxiuNom())
                        .contingut(output.toByteArray())
                        .mimeType(document.getMediaType())
                        .tipusDocumental(document.getTipoDocumental() != null ? document.getTipoDocumental() : TipusDocumentalEnum.NOTIFICACIO)
                        .origen(document.getOrigen() != null ? document.getOrigen() : OrigenEnum.ADMINISTRACIO)
                        .validesa(document.getValidesa() != null ? document.getValidesa() : ValidesaEnum.ORIGINAL)
                        .modeFirma(document.getModoFirma() != null ? document.getModoFirma() : false)
                        .dataCaptura(new Date())
                        .build();
            }

            return null;
        } catch (Exception ex) {
            var msg = "Error obtenint les dades del document '" + (document != null ? document.getId() : "") + "': " + ex.getMessage();
            throw new SistemaExternException(IntegracioCodiEnum.REGISTRE.name(), msg, ex.getCause());
        }
    }

    protected EnviamentSirTipusDocumentEnviarEnumDto getEnviamentSirTipusDocumentEnviar() {

        var tipus = TOT;
        try {
            var tipusStr = configHelper.getConfig("es.caib.notib.plugin.registre.enviamentSir.tipusDocumentEnviar");
            if (tipusStr != null && !tipusStr.isEmpty()) {
                tipus = EnviamentSirTipusDocumentEnviarEnumDto.valueOf(tipusStr);
            }
        } catch (Exception ex) {
            log.error("No s'ha pogut obtenir el tipus de document a enviar per a l'enviament SIR per defecte. S'utilitzarà el tipus TOT (CSV i binari).");
        }
        return tipus;
    }

    protected static ValidesaEnum estatElaboracioToValidesa(DocumentEstatElaboracio estatElaboracio) {

        if (estatElaboracio == null) {
            return ValidesaEnum.ORIGINAL; // Valor per defecte
        }
        switch (estatElaboracio) {
            case COPIA_CF:
            case COPIA_DP:
            case COPIA_PR:
                return ValidesaEnum.COPIA_AUTENTICA;
            case ALTRES:
                return ValidesaEnum.COPIA;
            case ORIGINAL:
            default:
                return ValidesaEnum.ORIGINAL;
        }
    }

    @Named("estat")
    protected Long getEstat(NotificacioEstatEnumDto estat) {
        if (estat == null) {
            return null;
        }
        return estat.getLongVal();
    }

    @Named("versio")
    protected String getVersio(NotificacioEnviamentEntity enviament) {
        return "NOTIB v." + CacheHelper.getAppVersion();
    }

    @Named("idioma")
    protected Long getIdioma(Idioma idioma) {
        if (idioma == null)
            return 0L;
        return Long.valueOf(idioma.ordinal());
    }

    @Named("observacions")
    protected String getObservacions(NotificacioEnviamentEntity enviament) {
        if (enviament == null || enviament.getNotificacio().getUsuariCodi() == null) {
            return "Notib: ";
        }
        return "Notib: " + enviament.getNotificacio().getUsuariCodi();
    }

    @Named("resumen")
    protected String getResumen(NotificacioEnviamentEntity enviament) {
        if (enviament == null || enviament.getNotificacio() == null || enviament.getNotificacio().getEnviamentTipus() == null) {
            return null;
        }
        return (EnviamentTipus.NOTIFICACIO == enviament.getNotificacio().getEnviamentTipus() ? "Notificacio" : "Comunicacio") + " - " + enviament.getNotificacio().getConcepte();
    }

    @Named("interessat")
    protected List<InteresadoWsDto> toInteresadoWs(InteressatRegistreDto interessat) {
        if (interessat == null) {
            return null;
        }
        InteresadoWsDto interesadoWsDto = new InteresadoWsDto();
        if (interessat.getTitular() != null) {
            interesadoWsDto.setInteresado(personaMapper.toInteresadoWs(interessat.getTitular()));
        }
        if (interessat.getRepresentant() != null) {
            interesadoWsDto.setRepresentante(personaMapper.toInteresadoWs(interessat.getRepresentant()));
        }
        return List.of(interesadoWsDto);
    }

    @Named("documents")
    protected List<AnexoWsDto> toAnexoWs(EnviamentRegistreDto enviament) {
        var documents = new ArrayList<AnexoWsDto>();
        var inclouDocuments = isSendDocumentsActive() || EnviamentTipus.SIR.equals(enviament.getTipusEnviament());
        if (enviament.getDocuments() == null || enviament.getDocuments().isEmpty() || !inclouDocuments) {
            return documents;
        }

        enviament.getDocuments().forEach(d -> documents.add(documentMapper.toAnexoWs(d)));
        return documents;
    }

    public boolean isSendDocumentsActive() {
        return configHelper.getConfigAsBoolean("es.caib.notib.plugin.registre.documents.enviar");
    }

//    @AfterMapping
//    public void addOficinaILlibre(NotificacioEnviamentEntity enviament, @MappingTarget EnviamentRegistreDto dto) {
//
//    }
}
