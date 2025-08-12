package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.UsuariService;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.AvisRepository;
import es.caib.notib.persist.repository.ColumnesRepository;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EntitatTipusDocRepository;
import es.caib.notib.persist.repository.EntregaPostalRepository;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.GrupProcSerRepository;
import es.caib.notib.persist.repository.GrupRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioMassivaRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.PagadorCieFormatFullaRepository;
import es.caib.notib.persist.repository.PagadorCieFormatSobreRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;
import es.caib.notib.persist.repository.PagadorPostalRepository;
import es.caib.notib.persist.repository.PersonaRepository;
import es.caib.notib.persist.repository.ProcSerOrganRepository;
import es.caib.notib.persist.repository.ProcedimentRepository;
import es.caib.notib.persist.repository.UsuariRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UsuariServiceImpl implements UsuariService {

    @Autowired
    private UsuariRepository usuariRepository;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private PagadorPostalRepository pagadorPostalRepository;
    @Autowired
    private PagadorCieFormatSobreRepository pagadorCieFormatSobreRepository;
    @Autowired
    private PagadorCieFormatFullaRepository pagadorCieFormatFullaRepository;
    @Autowired
    private PagadorCieRepository pagadorCieRepository;
    @Autowired
    private EntregaPostalRepository entregaPostalRepository;
    @Autowired
    private ProcSerOrganRepository procSerOrganRepository;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private NotificacioRepository notificacioRepository;
    @Autowired
    private NotificacioTableViewRepository notificacioTableViewRepository;
    @Autowired
    private NotificacioMassivaRepository notificacioMassivaRepository;
    @Autowired
    private NotificacioEventRepository notificacioEventRepository;
    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Autowired
    private EnviamentTableRepository enviamentTableRepository;
    @Autowired
    private GrupProcSerRepository grupProcSerRepository;
    @Autowired
    private GrupRepository grupRepository;
    @Autowired
    private EntitatTipusDocRepository entitatTipusDocRepository;
    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ColumnesRepository columnesRepository;
    @Autowired
    private AvisRepository avisRepository;
    @Autowired
    private AplicacioRepository aplicacioRepository;
    @Autowired
    private SpringCacheBasedAclCache aclCache;

    @Override
    public UsuariDto findByCodi(String codi) {

        log.info("[UsuariService] Consultant usuari amb codi " + codi);
        try {
            var usuari = usuariRepository.findByCodi(codi);
            if (usuari == null) {
                return null;
            }
            return conversioTipusHelper.convertir(usuari, UsuariDto.class);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Long updateUsuariCodi(String codiAntic, String codiNou) {

        var usuariAntic = usuariRepository.findByCodi(codiAntic);
        if (usuariAntic == null) {
            throw new NotFoundException(codiAntic, UsuariEntity.class);
        }
        // Si han informat un codi d'usuari nou, actualitzem l'usuari i totes les seves referències en BBDD
        var usuariNou = usuariRepository.findByCodi(codiNou);
        if (usuariNou == null) {
            usuariNou = cloneUsuari(codiNou, usuariAntic);
        }

        log.info(">>>>> UPDATE CODI USUARI: " + codiAntic + " -> " + codiNou);
        var registresModificats = 0L;

        // Actualitzam la informació de auditoria de les taules:
        registresModificats += updateUsuariAuditoria(codiAntic, codiNou);
        // Actualitazam els permisos assignats per ACL
        registresModificats += updateUsuariPermisos(codiAntic, codiNou);
        // Actualitzam les referencis a l'usuari a taules:
        registresModificats += updateUsuariReferencies(codiAntic, codiNou, usuariAntic.getUltimaEntitat());
        // Eliminam l'usuari antic
        usuariRepository.delete(usuariAntic);
        aclCache.clearCache();
        return registresModificats;
    }

    private UsuariEntity cloneUsuari(String codiNou, UsuariEntity usuariAntic) {

        var usuariNou = UsuariEntity.builder()
                .codi(codiNou)
                .nom(usuariAntic.getNom())
                .nif(usuariAntic.getNif())
                .llinatges(usuariAntic.getLlinatges())
                .nomSencer(usuariAntic.getNomSencer())
                .email(usuariAntic.getEmail())
                .emailAlt(usuariAntic.getEmailAlt())
                .rebreEmailsNotificacio(usuariAntic.isRebreEmailsNotificacio())
                .rebreEmailsNotificacioCreats(usuariAntic.isRebreEmailsNotificacioCreats())
                .ultimRol(usuariAntic.getUltimRol())
                .ultimaEntitat(usuariAntic.getUltimaEntitat())
                .idioma(usuariAntic.getIdioma())
                .numElementsPaginaDefecte(usuariAntic.getNumElementsPaginaDefecte())
                .build();
        return usuariRepository.saveAndFlush(usuariNou);
    }

    private Long updateUsuariAuditoria(String codiAntic, String codiNou) {

        var registresModificats = 0L;
        log.info(">>> UPDATE USUARIS AUDITORIA:");
        //		NOT_PAGADOR_POSTAL
        var t0 = System.currentTimeMillis();
        registresModificats += pagadorPostalRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PAGADOR_POSTAL: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_FORMATS_SOBRE
        t0 = System.currentTimeMillis();
        registresModificats += pagadorCieFormatSobreRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_FORMATS_SOBRE: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_FORMATS_FULLA
        t0 = System.currentTimeMillis();
        registresModificats += pagadorCieFormatFullaRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_FORMATS_FULLA: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_PAGADOR_CIE
        t0 = System.currentTimeMillis();
        registresModificats += pagadorCieRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PAGADOR_CIE: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_ENTREGA_POSTAL
        t0 = System.currentTimeMillis();
        registresModificats += entregaPostalRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_ENTREGA_POSTAL: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_PRO_ORGAN
        t0 = System.currentTimeMillis();
        registresModificats += procSerOrganRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PRO_ORGAN: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_PROCEDIMENT
        t0 = System.currentTimeMillis();
        registresModificats += procedimentRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PROCEDIMENT: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_PERSONA
        t0 = System.currentTimeMillis();
        registresModificats += personaRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PERSONA: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        registresModificats += notificacioRepository.updateCreatedByCodi(codiAntic, codiNou);
        registresModificats += notificacioRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO_TABLE
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        registresModificats += notificacioTableViewRepository.updateCreatedByCodi(codiAntic, codiNou);
        registresModificats += notificacioTableViewRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO_TABLE: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO_MASSIVA
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        registresModificats += notificacioMassivaRepository.updateCreatedByCodi(codiAntic, codiNou);
        registresModificats += notificacioMassivaRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO_MASSIVA: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO_EVENT
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        registresModificats += notificacioEventRepository.updateCreatedByCodi(codiAntic, codiNou);
        registresModificats += notificacioEventRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO_EVENT: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO_ENV
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        registresModificats += notificacioEnviamentRepository.updateCreatedByCodi(codiAntic, codiNou);
        registresModificats += notificacioEnviamentRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO_ENV: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO_ENV_TABLE
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        registresModificats += enviamentTableRepository.updateCreatedByCodi(codiAntic, codiNou);
        registresModificats += enviamentTableRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO_ENV_TABLE: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_PRO_GRUP
        t0 = System.currentTimeMillis();
        registresModificats += grupProcSerRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PRO_GRUP: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_GRUP
        t0 = System.currentTimeMillis();
        registresModificats += grupRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_GRUP: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_ENTITAT_TIPUS_DOC
        t0 = System.currentTimeMillis();
        registresModificats += entitatTipusDocRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_ENTITAT_TIPUS_DOC: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_ENTITAT
        t0 = System.currentTimeMillis();
        registresModificats += entitatRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_ENTITAT: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_DOCUMENT
        t0 = System.currentTimeMillis();
        registresModificats += documentRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_DOCUMENT: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_COLUMNES
        t0 = System.currentTimeMillis();
        registresModificats += columnesRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_COLUMNES: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_AVIS
        t0 = System.currentTimeMillis();
        registresModificats += avisRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_AVIS: " + (System.currentTimeMillis() - t0) + " ms");
//		NOT_APLICACIO
        t0 = System.currentTimeMillis();
        registresModificats += aplicacioRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_APLICACIO: " + (System.currentTimeMillis() - t0) + " ms");

        return registresModificats;
    }

    private int updateUsuariPermisos(String codiAntic, String codiNou) {

        log.info("Actualizar usuari permisos de l'usuari");
        return usuariRepository.updateUsuariPermis(codiAntic, codiNou);
    }

    private Long updateUsuariReferencies(String codiAntic, String codiNou, Long entitatId) {

        var registresModificats = 0L;
        log.info(">>> UPDATE USUARIS TAULES AMB REFERENCIES:");
        var t0 = System.currentTimeMillis();
        var columnes = columnesRepository.existeixUsuariPerEntitat(codiNou, entitatId);
        if (columnes != null) {
//            columnes = columnesRepository.existeixUsuariPerEntitat(codiAntic, entitatId);
            columnesRepository.delete(columnes);
//            return 0L;
        }
        registresModificats += columnesRepository.updateUsuariCodi(codiAntic, codiNou);
        log.info("> NOT_COLUMNES.USUARI_CODI: " + (System.currentTimeMillis() - t0) + " ms");
        return registresModificats;
    }

}
