package es.caib.notib.logic.service;

import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.permis.PermisCodivalorOrganGestorComu;
import es.caib.notib.logic.intf.dto.permis.PermisosUsuari;
import es.caib.notib.logic.intf.dto.permis.PermisosUsuarisFiltre;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.intf.service.ProcedimentService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    private PermisosService permisosService;
    @Autowired
    private OrganGestorService organGestorService;
    @Autowired
    private CacheHelper cacheHelper;
    @Autowired
    private ProcedimentService procedimentService;
    @Autowired
    private OrganGestorCachable organGestorCachable;

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

    @Override
    public PaginaDto<UsuariDto> findAmbFiltre(PermisosUsuarisFiltre filtre, PaginacioParamsDto paginacioParams) {

        try {
            var pageable = getMappeigPropietats(paginacioParams);
            var usuaris = usuariRepository.findAmbFiltre(filtre, pageable);
            return paginacioHelper.toPaginaDto(usuaris, UsuariDto.class);
        } catch (Exception ex) {
            var msg = "Error carregant les dades de la taula de permisos d'usuari";
            log.error(msg, ex);
            throw ex;
        }
    }

    @Override
    public PermisosUsuari getPermisosUsuari(EntitatDto entitat, String usuariCodi, OrganGestorDto organAdmin) {

        try {
            var permisosUsuari = new PermisosUsuari();
            var organsAmbPermis = permisosService.getOrgansAmbPermis(entitat.getId(), usuariCodi, true);
            Map<String, List<PermisDto>> permisosOrgans = new HashMap<>();
            Map<String, List<String>> organsFills = new HashMap<>();
            List<String> organsFillsNom = new ArrayList<>();
            var rols = cacheHelper.findRolsUsuariAmbCodi(usuariCodi);
            List<PermisDto> p;
            var isOrganAdmin = organAdmin != null;
            OrganGestorDto organEntity;
            for (var organ : organsAmbPermis) {
                if (isOrganAdmin && !organAdmin.getId().equals(Long.valueOf(organ.getCodi()))) {
                    continue;
                }
                var permisos = organGestorService.permisFind(entitat.getId(), Long.valueOf(organ.getCodi()));
                if (permisos.isEmpty()) {
                    continue;
                }
                p = new ArrayList<>();
                for (var permis : permisos) {
                    if (permis.getPrincipal().equals(usuariCodi) || rols.contains(permis.getPrincipal())) {
                        permis.setOrganNom(organ.getValor());
                        p.add(permis);
                    }
                }
                var codi = organ.getValor().split(" - ")[0];
                List<String> codiFills = organGestorCachable.getCodisOrgansGestorsFillsByOrgan(entitat.getDir3Codi(), codi);
                organsFillsNom = new ArrayList<>();
                for (var codiFill : codiFills) {
                    if (codi.equals(codiFill)) {
                        continue;
                    }
                    organEntity = organGestorService.findByCodi(entitat.getId(), codiFill);
                    organsFillsNom.add(organEntity.getCodi() + " - " + organEntity.getNom());
                }
                organsFills.put(organ.getCodi(), organsFillsNom);
                permisosOrgans.put(organ.getCodi(), p);


            }
            var objectMapper = new ObjectMapper();
            String map = "";
            map = objectMapper.writeValueAsString(permisosOrgans);
            permisosUsuari.setPermisosOrgans(map);
            map = objectMapper.writeValueAsString(organsFills);
            permisosUsuari.setOrgansFills(map);
            var procedimentsAmbPermis = permisosService.getProcedimentsAmbPermis(entitat.getId(), usuariCodi);
            Map<String, List<PermisDto>> permisosProcediment = new HashMap<>();
            Map<String, List<CodiValorOrganGestorComuDto>> procSerOrgan = new HashMap<>();
            List<PermisCodivalorOrganGestorComu> procSerOrganList = new ArrayList<>();
            for (var procediment : procedimentsAmbPermis) {
                if (isOrganAdmin && !organAdmin.getCodi().equals(procediment.getOrganGestor())) {
                    continue;
                }
                var permisos = procedimentService.permisFind(entitat.getId(), false, procediment.getId(), procediment.getOrganGestor(), procediment.getOrganGestor(), null, null);
                if (permisos.isEmpty()) {
                    var organ = organGestorService.findByCodi(entitat.getId(), procediment.getOrganGestor());
                    var permisosOrgan = organGestorService.permisFind(entitat.getId(), organ.getId());
                    if (permisosOrgan.isEmpty()) {
                        var organFill = organsAmbPermis.stream().filter(x -> x.getCodi().equals(organ.getId()+"")).collect(Collectors.toList());
                        if (organFill.isEmpty()) {
                            continue;
                        }
                        var fill = organFill.get(0);
                        var key = organsFills.keySet().stream().filter(o -> !organsFills.get(o).stream().filter(c -> c.equals(fill.getValor())).collect(Collectors.toList()).isEmpty()).collect(Collectors.toList());
                        if (!key.isEmpty())
                            permisosOrgan = permisosOrgans.get(key.get(0));
                    }
                    for (var permisOrgan : permisosOrgan) {
                        if (permisOrgan.getPrincipal().equals(usuariCodi) || rols.contains(permisOrgan.getPrincipal())) {
                            procSerOrganList.add(PermisCodivalorOrganGestorComu.builder().codiValor(procediment).permis(permisOrgan).build());
                        }
                    }
                    continue;
                }
                p = new ArrayList<>();
                for (var permis : permisos) {
                    if (permis.getPrincipal().equals(usuariCodi) || rols.contains(permis.getPrincipal())) {
                        permis.setOrganNom(procediment.getValor());
                        p.add(permis);
                    }
                }
                if (!permisos.isEmpty()) {
                    permisosProcediment.put(procediment.getCodi(), p);
                }
            }
            map = objectMapper.writeValueAsString(permisosProcediment);
            permisosUsuari.setProcSerOrgan(procSerOrganList);
            permisosUsuari.setPermisosProcediment(map);
            return permisosUsuari;
        } catch (Exception ex) {
            log.error("Error obtinguent else permisos de l'usuari " + usuariCodi + " de l'entitat " + entitat.getCodi(), ex);
            return new PermisosUsuari();
        }
    }

    private Pageable getMappeigPropietats(PaginacioParamsDto paginacioParams) {

        Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
        mapeigPropietatsOrdenacio.put("usuariCodi", new String[] {"usuariCodi"});
//        mapeigPropietatsOrdenacio.put("endpoint", new String[] {"usuariCodi"});
//        mapeigPropietatsOrdenacio.put("data", new String[] {"data"});

//        return paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
        return paginacioHelper.toSpringDataPageable(paginacioParams);
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
