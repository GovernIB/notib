package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.utils.DatesUtils;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.filtres.FiltreNotificacio;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.ProcedimentRepository;
import es.caib.notib.persist.repository.ServeiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class NotificacioListHelper {

    @Autowired
    private PermisosService permisosService;
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    private EntityComprovarHelper entityComprovarHelper;
    @Autowired
    private ProcSerHelper procedimentHelper;
    @Autowired
    private OrganGestorHelper organGestorHelper;
    @Autowired
    private OrganigramaHelper organigramaHelper;

    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private ServeiRepository serveiRepository;
    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private NotificacioEventRepository eventRepository;


    public Pageable getMappeigPropietats(PaginacioParamsDto paginacioParams) {

        Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<>();
        mapeigPropietatsOrdenacio.put("procediment.organGestor", new String[] {"pro.organGestor.codi"});
        mapeigPropietatsOrdenacio.put("organGestorDesc", new String[] {"organCodi"});
        mapeigPropietatsOrdenacio.put("procediment.nom", new String[] {"procedimentNom"});
        mapeigPropietatsOrdenacio.put("procedimentDesc", new String[] {"procedimentCodi"});
        mapeigPropietatsOrdenacio.put("createdByComplet", new String[] {"createdBy"});
        mapeigPropietatsOrdenacio.put("estatString", new String[] {"estat"});
        return paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
    }

    public List<String> getCodisProcedimentsAndOrgansAmpPermisProcessar(Long entitatId, String usuariCodi) {
        List<String> codis = new ArrayList<>();
        var procSersAmbPermis = permisosService.getProcSersAmbPermis(entitatId, usuariCodi, PermisEnum.PROCESSAR);
        var organs =  permisosService.getOrgansAmbPermis(entitatId, usuariCodi, PermisEnum.PROCESSAR);
        if (procSersAmbPermis != null) {
            for (var procedimentOrgan : procSersAmbPermis) {
                codis.add(procedimentOrgan.getCodi());
            }
        }
        if (organs != null && !organs.isEmpty()) {
            for (var organ : organs) {
                codis.add(organ.getCodi());
            }
        }
        return codis;
    }

    public FiltreNotificacio getFiltre(NotificacioFiltreDto f, Long entitatId, RolEnumDto rol, String usuariCodi, List<String> rols) {

        OrganGestorEntity organGestor = null;
        if (f.getOrganGestor() != null && !f.getOrganGestor().isEmpty()) {
            try {
                var id = Long.valueOf(f.getOrganGestor());
                organGestor = organGestorRepository.findById(id).orElse(null);
            } catch (NumberFormatException ex) {
                organGestor = organGestorRepository.findByCodi(f.getOrganGestor());
            }
        }
        ProcSerEntity procediment = null;
        if (f.getProcedimentId() != null) {
            procediment = procedimentRepository.findById(f.getProcedimentId()).orElse(null);
        } else if (f.getServeiId() != null) {
            procediment = serveiRepository.findById(f.getServeiId()).orElse(null);
        }
        var isUsuari = RolEnumDto.tothom.equals(rol);
        var isUsuariEntitat = RolEnumDto.NOT_ADMIN.equals(rol);
        var isSuperAdmin = RolEnumDto.NOT_SUPER.equals(rol);
        var isAdminOrgan = RolEnumDto.NOT_ADMIN_ORGAN.equals(rol);
        var entitatActual = entityComprovarHelper.comprovarEntitat(entitatId,false, isUsuariEntitat,false);
        var nomesSenseErrors = false;
        var nomesAmbErrors = f.isNomesAmbErrors();
        var nomesFiReintents = f.isNomesFiReintents();
        var deleted = f.isDeleted();
        List<String> codisProcedimentsDisponibles = new ArrayList<>();
        List<String> codisOrgansGestorsDisponibles = new ArrayList<>();
        List<String> codisProcedimentsOrgans = new ArrayList<>();
        List<String> codisOrgansGestorsComunsDisponibles = new ArrayList<>();
        if (isUsuari && entitatActual != null) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            entityComprovarHelper.getPermissionsFromName(PermisEnum.CONSULTA);
            // Procediments accessibles per qualsevol òrgan gestor
            codisProcedimentsDisponibles = procedimentHelper.findCodiProcedimentsWithPermis(auth, entitatActual, PermisEnum.CONSULTA);
            // Òrgans gestors dels que es poden consultar tots els procediments que no requereixen permís directe
            codisOrgansGestorsDisponibles = organGestorHelper.findCodiOrgansGestorsWithPermisPerConsulta(auth, entitatActual, PermisEnum.CONSULTA);
            codisOrgansGestorsComunsDisponibles = organGestorHelper.findCodiOrgansGestorsWithPermisPerConsulta(auth, entitatActual, PermisEnum.COMUNS);
            // Procediments comuns que es poden consultar per a òrgans gestors concrets
            codisProcedimentsOrgans = permisosService.getProcedimentsOrgansAmbPermis(entitatActual.getId(), auth.getName(), PermisEnum.CONSULTA);
        } else if (isAdminOrgan && entitatActual != null && organGestor != null) {
            codisOrgansGestorsDisponibles = organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestor.getCodi());
        }

        var esProcedimentsCodisNotibNull = (codisProcedimentsDisponibles == null || codisProcedimentsDisponibles.isEmpty());
        var esOrgansGestorsCodisNotibNull = (codisOrgansGestorsDisponibles == null || codisOrgansGestorsDisponibles.isEmpty());
        var esOrgansGestorsComunsCodisNotibNull = (codisOrgansGestorsComunsDisponibles == null || codisOrgansGestorsComunsDisponibles.isEmpty());
        var esProcedimentOrgansAmbPermisNull = (codisProcedimentsOrgans == null || codisProcedimentsOrgans.isEmpty());
        var organs = isAdminOrgan && organGestor != null ? organigramaHelper.getCodisOrgansGestorsFillsExistentsByOrgan(entitatActual.getDir3Codi(), organGestor.getCodi()) : null;
        var entitatsActives = isSuperAdmin ? entitatRepository.findByActiva(true) : null;
        var entitatFiltre = isUsuariEntitat || isUsuari ? entitatId : f.getEntitatId();
        f.setDataFi(DatesUtils.incrementarDataFi(f.getDataFi()));

        var filtreNotificacio = FiltreNotificacio.builder()
                .entitatIdNull(entitatFiltre == null)
                .entitatId(entitatFiltre)
                .entitat(entitatActual)
                .enviamentTipusNull(f.getEnviamentTipus() == null)
                .enviamentTipus(f.getEnviamentTipus())
                .concepteNull(Strings.isNullOrEmpty(f.getConcepte()))
                .concepte(f.getConcepte())
                .estatNull(f.getEstat() == null)
                .estatMask(f.getEstat() == null ? 0 : f.getEstat().getMask())
                .dataIniciNull(f.getDataInici() == null)
                .dataInici(f.getDataInici())
                .dataFiNull(f.getDataFi() == null)
                .dataFi(f.getDataFi())
                .titularNull(Strings.isNullOrEmpty(f.getTitular()))
                .titular(f.getTitular())
                .organCodiNull(organGestor == null)
                .organCodi(organGestor != null ? organGestor.getCodi() : null)
                .procedimentNull(procediment == null)
                .procedimentCodi(procediment != null ? procediment.getCodi() : null)
                .tipusUsuariNull(f.getTipusUsuari() == null)
                .tipusUsuari(f.getTipusUsuari())
                .numExpedientNull(Strings.isNullOrEmpty(f.getNumExpedient()))
                .numExpedient(f.getNumExpedient())
                .creadaPerNull(Strings.isNullOrEmpty(f.getCreadaPer()))
                .creadaPer(f.getCreadaPer())
                .identificadorNull(Strings.isNullOrEmpty(f.getIdentificador()))
                .identificador(f.getIdentificador())
                .registreNumNull(Strings.isNullOrEmpty(f.getRegistreNum()))
                .registreNum(f.getRegistreNum())
                .nomesAmbErrors(nomesAmbErrors)
                .nomesFiReintents(nomesFiReintents)
                .deleted(deleted)
                .nomesSenseErrors(nomesSenseErrors)
                .referenciaNull(Strings.isNullOrEmpty(f.getReferencia()))
                .referencia(f.getReferencia())
                .isUsuari(isUsuari)
                .procedimentsCodisNotibNull(esProcedimentsCodisNotibNull)
                .procedimentsCodisNotib(esProcedimentsCodisNotibNull ? null : codisProcedimentsDisponibles)
                .grupsProcedimentCodisNotib(rols)
                .organsGestorsCodisNotibNull(esOrgansGestorsCodisNotibNull)
                .organsGestorsCodisNotib(esOrgansGestorsCodisNotibNull ? null : codisOrgansGestorsDisponibles)
                .esOrgansGestorsComunsCodisNotibNull(esOrgansGestorsComunsCodisNotibNull)
                .organsGestorsComunsCodisNotib(codisOrgansGestorsComunsDisponibles)
                .procedimentOrgansIdsNotibNull(esProcedimentOrgansAmbPermisNull)
                .procedimentOrgansIdsNotib(esProcedimentOrgansAmbPermisNull ?  null : codisProcedimentsOrgans)
                .usuariCodi(usuariCodi)
                .isSuperAdmin(isSuperAdmin)
                .isUsuariEntitat(isUsuariEntitat)
                .entitatsActives(entitatsActives)
                .isAdminOrgan(isAdminOrgan)
                .organs(organs)
                .notMassivaIdNull(f.getNotMassivaId() == null)
                .notMassivaId(f.getNotMassivaId()).build();

        filtreNotificacio.crearProcedimentsCodisNotibSplit();
        return filtreNotificacio;
    }

}
