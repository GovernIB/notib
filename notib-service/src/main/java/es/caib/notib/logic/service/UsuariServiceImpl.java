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
    public void updateUsuariCodi(String codiAntic, String codiNou) {

        var usuariAntic = usuariRepository.findByCodi(codiAntic);
        if (usuariAntic == null) {
            throw new NotFoundException(codiAntic, UsuariEntity.class);
        }
        var usuariNou = cloneUsuari(codiNou, usuariAntic);
        // Actualitzam la informació de auditoria de les taules:
        updateUsuariAuditoria(codiAntic, codiNou);
        // Actualitazam els permisos assignats per ACL
        updateUsuariPermisos(codiAntic, codiNou);
        // Actualitzam les referencis a l'usuari a taules:
        updateUsuariReferencies(codiAntic, codiNou);
        // Eliminam l'usuari antic
        usuariRepository.delete(usuariAntic);
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

    private void updateUsuariAuditoria(String codiAntic, String codiNou) {

        log.info(">>> UPDATE USUARIS AUDITORIA:");
        //		NOT_PAGADOR_POSTAL
        Long t0 = System.currentTimeMillis();
        pagadorPostalRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PAGADOR_POSTAL: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_FORMATS_SOBRE
        t0 = System.currentTimeMillis();
        pagadorCieFormatSobreRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_FORMATS_SOBRE: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_FORMATS_FULLA
        t0 = System.currentTimeMillis();
        pagadorCieFormatFullaRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_FORMATS_FULLA: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_PAGADOR_CIE
        t0 = System.currentTimeMillis();
        pagadorCieRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PAGADOR_CIE: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_ENTREGA_POSTAL
        t0 = System.currentTimeMillis();
        entregaPostalRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_ENTREGA_POSTAL: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_PRO_ORGAN
        t0 = System.currentTimeMillis();
        procSerOrganRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PRO_ORGAN: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_PROCEDIMENT
        t0 = System.currentTimeMillis();
        procedimentRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PROCEDIMENT: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_PERSONA
        t0 = System.currentTimeMillis();
        personaRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PERSONA: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        notificacioRepository.updateCreatedByCodi(codiAntic, codiNou);
        notificacioRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO_TABLE
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        notificacioTableViewRepository.updateCreatedByCodi(codiAntic, codiNou);
        notificacioTableViewRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO_TABLE: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO_MASSIVA
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        notificacioMassivaRepository.updateCreatedByCodi(codiAntic, codiNou);
        notificacioMassivaRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO_MASSIVA: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO_EVENT
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        notificacioEventRepository.updateCreatedByCodi(codiAntic, codiNou);
        notificacioEventRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO_EVENT: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO_ENV
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        notificacioEnviamentRepository.updateCreatedByCodi(codiAntic, codiNou);
        notificacioEnviamentRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO_ENV: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_NOTIFICACIO_ENV_TABLE
        t0 = System.currentTimeMillis();
        // Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
        enviamentTableRepository.updateCreatedByCodi(codiAntic, codiNou);
        enviamentTableRepository.updateLastModifiedByCodi(codiAntic, codiNou);
        log.info("> NOT_NOTIFICACIO_ENV_TABLE: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_PRO_GRUP
        t0 = System.currentTimeMillis();
        grupProcSerRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_PRO_GRUP: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_GRUP
        t0 = System.currentTimeMillis();
        grupRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_GRUP: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_ENTITAT_TIPUS_DOC
        t0 = System.currentTimeMillis();
        entitatTipusDocRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_ENTITAT_TIPUS_DOC: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_ENTITAT
        t0 = System.currentTimeMillis();
        entitatRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_ENTITAT: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_DOCUMENT
        t0 = System.currentTimeMillis();
        documentRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_DOCUMENT: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_COLUMNES
        t0 = System.currentTimeMillis();
        columnesRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_COLUMNES: " + (System.currentTimeMillis() - t0) + " ms");
        //		NOT_AVIS
        t0 = System.currentTimeMillis();
        avisRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_AVIS: " + (System.currentTimeMillis() - t0) + " ms");
//		NOT_APLICACIO
        t0 = System.currentTimeMillis();
        aplicacioRepository.updateUsuariAuditoria(codiAntic, codiNou);
        log.info("> NOT_APLICACIO: " + (System.currentTimeMillis() - t0) + " ms");

    }

    private void updateUsuariPermisos(String codiAntic, String codiNou) {

        log.info("Actualizar usuari permisos de l'usuari");
        usuariRepository.updateUsuariPermis(codiAntic, codiNou);
        usuariRepository.flush();
    }

    private void updateUsuariReferencies(String codiAntic, String codiNou) {

        log.info(">>> UPDATE USUARIS TAULES AMB REFERENCIES:");
        //		NOT_COLUMNES.USUARI_CODI
        Long t0 = System.currentTimeMillis();
        columnesRepository.updateUsuariCodi(codiAntic, codiNou);
        log.info("> NOT_COLUMNES.USUARI_CODI: " + (System.currentTimeMillis() - t0) + " ms");
    }

}
