package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.dto.PermisEnum;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.api.service.PermisosService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioTableEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import es.caib.notib.core.helper.FiltreHelper.FiltreField;
import es.caib.notib.core.helper.FiltreHelper.StringField;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.repository.ServeiRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NotificacioListHelper {

    @Autowired
    private ProcedimentService procedimentService;
    @Autowired
    private PermisosService permisosService;
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private ServeiRepository serveiRepository;
    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private OrganGestorService organGestorService;
    @Autowired
    private NotificacioRepository notificacioRepository;

    public Pageable getMappeigPropietats(PaginacioParamsDto paginacioParams) {
        Map<String, String[]> mapeigPropietatsOrdenacio = new HashMap<String, String[]>();
        mapeigPropietatsOrdenacio.put("procediment.organGestor", new String[] {"pro.organGestor.codi"});
        mapeigPropietatsOrdenacio.put("organGestorDesc", new String[] {"organCodi"});
        mapeigPropietatsOrdenacio.put("procediment.nom", new String[] {"procedimentNom"});
        mapeigPropietatsOrdenacio.put("procedimentDesc", new String[] {"procedimentCodi"});
        mapeigPropietatsOrdenacio.put("createdByComplet", new String[] {"createdBy"});
        return paginacioHelper.toSpringDataPageable(paginacioParams, mapeigPropietatsOrdenacio);
    }
    public PaginaDto<NotificacioTableItemDto> complementaNotificacions(
            EntitatEntity entitatEntity,
            String usuariCodi,
            Page<NotificacioTableEntity> notificacions) {

        if (notificacions == null) {
            return paginacioHelper.getPaginaDtoBuida(NotificacioTableItemDto.class);
        }

        List<String> codisProcedimentsProcessables = new ArrayList<>();
        List<CodiValorOrganGestorComuDto> procSersAmbPermis = permisosService.getProcSersAmbPermis(entitatEntity.getId(), usuariCodi, PermisEnum.PROCESSAR);
        if (procSersAmbPermis != null) {
            for (CodiValorOrganGestorComuDto procedimentOrgan : procSersAmbPermis) {
                codisProcedimentsProcessables.add(procedimentOrgan.getCodi());
            }
        }

//        List<ProcSerSimpleDto> procedimentsProcessables = procedimentService.findProcedimentServeisWithPermis(entitatEntity.getId(),
//                usuariCodi, PermisEnum.PROCESSAR);
//        if (procedimentsProcessables != null)
//            for (ProcSerSimpleDto procediment : procedimentsProcessables) {
//                codisProcedimentsProcessables.add(procediment.getCodi());
//            }
//        List<ProcSerOrganDto> procedimentOrgansProcessables = procedimentService.findProcedimentsOrganWithPermis(entitatEntity.getId(), usuariCodi, PermisEnum.PROCESSAR);
//        if (procedimentOrgansProcessables != null) {
//            for (ProcSerOrganDto procedimentOrgan : procedimentOrgansProcessables) {
//                codisProcedimentsProcessables.add(procedimentOrgan.getProcSer().getCodi());
//            }
//        }
//
//        List<String> codisOrgansProcessables = new ArrayList<String>();
//        List<OrganGestorDto> organsProcessables = organGestorService.findOrgansGestorsWithPermis(entitatEntity.getId(), usuariCodi, PermisEnum.PROCESSAR);
//        if (organsProcessables != null)
//            for (OrganGestorDto organ : organsProcessables) {
//            	codisOrgansProcessables.add(organ.getCodi());
//            }
        
        for (NotificacioTableEntity notificacio : notificacions) {
            boolean permisProcessar = false;
            if (notificacio.getProcedimentCodi() != null && NotificacioEstatEnumDto.FINALITZADA.equals(notificacio.getEstat())) {
                permisProcessar = codisProcedimentsProcessables.contains(notificacio.getProcedimentCodi());
            }

//            // Si no te permís de processar per procediment, mirar si té permís de processar per òrgan gestor
//            if (!permisProcessar && NotificacioEstatEnumDto.FINALITZADA.equals(notificacio.getEstat())) {
//                permisProcessar = codisOrgansProcessables.contains(notificacio.getOrganCodi());
//            }
            notificacio.setPermisProcessar(permisProcessar);
            List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacio.getId());
            if (enviamentsPendents != null && !enviamentsPendents.isEmpty()) {
                notificacio.setHasEnviamentsPendentsRegistre(true);
            }
            notificacio.setDocumentId(notificacioRepository.findDOcumentId(notificacio.getId()));
        }
        return paginacioHelper.toPaginaDto(notificacions, NotificacioTableItemDto.class);
    }

    public NotificacioFiltre getFiltre(NotificacioFiltreDto filtreDto) {

        OrganGestorEntity organGestor = null;
        if (filtreDto.getOrganGestor() != null && !filtreDto.getOrganGestor().isEmpty()) {
            organGestor = organGestorRepository.findOne(Long.parseLong(filtreDto.getOrganGestor()));
        }
        ProcSerEntity procediment = null;
        if (filtreDto.getProcedimentId() != null) {
            procediment = procedimentRepository.findById(filtreDto.getProcedimentId());
        } else if (filtreDto.getServeiId() != null) {
            procediment = serveiRepository.findById(filtreDto.getServeiId());
        }
        NotificacioEstatEnumDto estat = filtreDto.getEstat();
        Boolean hasZeronotificaEnviamentIntent = null;
        boolean isEstatNull = estat == null;
        boolean nomesSenseErrors = false;
        boolean nomesAmbErrors = filtreDto.isNomesAmbErrors();
        if (!isEstatNull && estat.equals(NotificacioEstatEnumDto.ENVIANT)) {
            estat = NotificacioEstatEnumDto.PENDENT;
            hasZeronotificaEnviamentIntent = true;
            nomesSenseErrors = true;

        } else if (!isEstatNull && estat.equals(NotificacioEstatEnumDto.PENDENT)) {
            hasZeronotificaEnviamentIntent = false;
//					nomesAmbErrors = true;
        }
        return NotificacioFiltre.builder()
                .entitatId(new FiltreField<>(filtreDto.getEntitatId()))
                .comunicacioTipus(new FiltreField<>(filtreDto.getComunicacioTipus()))
                .enviamentTipus(new FiltreField<>(filtreDto.getEnviamentTipus()))
                .estat(new FiltreField<>(estat, isEstatNull))
                .concepte(new StringField(filtreDto.getConcepte()))
                .dataInici(new FiltreField<>(FiltreHelper.toIniciDia(filtreDto.getDataInici())))
                .dataFi(new FiltreField<>(FiltreHelper.toFiDia(filtreDto.getDataFi())))
                .titular(new StringField(filtreDto.getTitular()))
                .organGestor(new FiltreField<>(organGestor))
                .procediment(new FiltreField<>(procediment))
                .tipusUsuari(new FiltreField<>(filtreDto.getTipusUsuari()))
                .numExpedient(new StringField(filtreDto.getNumExpedient()))
                .creadaPer(new StringField(filtreDto.getCreadaPer()))
                .identificador(new StringField(filtreDto.getIdentificador()))
                .nomesAmbErrors(new FiltreField<>(nomesAmbErrors))
                .nomesSenseErrors(new FiltreField<>(nomesSenseErrors))
                .hasZeronotificaEnviamentIntent(new FiltreField<>(hasZeronotificaEnviamentIntent))
                .referencia(new StringField(filtreDto.getReferencia()))
                .build();
    }

    @Builder
    @Getter
    @Setter
    public static class NotificacioFiltre implements Serializable {
        private FiltreField<Long> entitatId;
        private FiltreField<NotificacioComunicacioTipusEnumDto> comunicacioTipus;
        private FiltreField<NotificaEnviamentTipusEnumDto> enviamentTipus;
        private FiltreField<NotificacioEstatEnumDto> estat;
        private StringField concepte;
        private FiltreField<Date> dataInici;
        private FiltreField<Date> dataFi;
        private StringField titular;
        private FiltreField<OrganGestorEntity> organGestor;
        private FiltreField<ProcSerEntity> procediment;
        private FiltreField<TipusUsuariEnumDto> tipusUsuari;
        private StringField numExpedient;
        private StringField creadaPer;
        private StringField identificador;
        private StringField referencia;
        private FiltreField<Boolean> nomesAmbErrors;
        private FiltreField<Boolean> nomesSenseErrors;
        private FiltreField<Boolean> hasZeronotificaEnviamentIntent;
    }

}
