package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.core.api.dto.procediment.ProcedimentOrganDto;
import es.caib.notib.core.api.dto.procediment.ProcedimentSimpleDto;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class NotificacioListHelper {

    @Autowired
    private ProcedimentService procedimentService;
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private OrganGestorRepository organGestorRepository;

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

        List<String> codisProcedimentsProcessables = new ArrayList<String>();
        List<ProcedimentSimpleDto> procedimentsProcessables = procedimentService.findProcedimentsWithPermis(entitatEntity.getId(),
                usuariCodi, PermisEnum.PROCESSAR);
        if (procedimentsProcessables != null)
            for (ProcedimentSimpleDto procediment : procedimentsProcessables) {
                codisProcedimentsProcessables.add(procediment.getCodi());
            }
        List<ProcedimentOrganDto> procedimentOrgansProcessables = procedimentService.findProcedimentsOrganWithPermis(entitatEntity.getId(), usuariCodi, PermisEnum.PROCESSAR);
        if (procedimentOrgansProcessables != null) {
            for (ProcedimentOrganDto procedimentOrgan : procedimentOrgansProcessables) {
                codisProcedimentsProcessables.add(procedimentOrgan.getProcediment().getCodi());
            }
        }

        for (NotificacioTableEntity notificacio : notificacions) {
            if (notificacio.getProcedimentCodi() != null && notificacio.getEstat() != NotificacioEstatEnumDto.PROCESSADA) {
                notificacio.setPermisProcessar(codisProcedimentsProcessables.contains(notificacio.getProcedimentCodi()));
            }

            List<NotificacioEnviamentEntity> enviamentsPendents = notificacioEnviamentRepository.findEnviamentsPendentsByNotificacioId(notificacio.getId());
            if (enviamentsPendents != null && !enviamentsPendents.isEmpty()) {
                notificacio.setHasEnviamentsPendentsRegistre(true);
            }
        }

        return paginacioHelper.toPaginaDto(
                notificacions,
                NotificacioTableItemDto.class);
    }

    public NotificacioFiltre getFiltre(NotificacioFiltreDto filtreDto) {

        OrganGestorEntity organGestor = null;
        if (filtreDto.getOrganGestor() != null && !filtreDto.getOrganGestor().isEmpty()) {
            organGestor = organGestorRepository.findOne(Long.parseLong(filtreDto.getOrganGestor()));
        }
        ProcedimentEntity procediment = null;
        if (filtreDto.getProcedimentId() != null) {
            procediment = procedimentRepository.findById(filtreDto.getProcedimentId());
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
                .concepte(new FiltreField<>(filtreDto.getConcepte(), filtreDto.getConcepte() == null || filtreDto.getConcepte().isEmpty()))
                .dataInici(new FiltreField<>(toIniciDia(filtreDto.getDataInici())))
                .dataFi(new FiltreField<>(toFiDia(filtreDto.getDataFi())))
                .titular(new FiltreField<>(filtreDto.getTitular(), filtreDto.getTitular() == null || filtreDto.getTitular().isEmpty()))
                .organGestor(new FiltreField<>(organGestor))
                .procediment(new FiltreField<>(procediment))
                .tipusUsuari(new FiltreField<>(filtreDto.getTipusUsuari()))
                .numExpedient(new FiltreField<>(filtreDto.getNumExpedient()))
                .creadaPer(new FiltreField<>(filtreDto.getCreadaPer()))
                .identificador(new FiltreField<>(filtreDto.getIdentificador()))
                .nomesAmbErrors(new FiltreField<>(nomesAmbErrors))
                .nomesSenseErrors(new FiltreField<>(nomesSenseErrors))
                .hasZeronotificaEnviamentIntent(new FiltreField<>(hasZeronotificaEnviamentIntent))
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
        private FiltreField<String> concepte;
        private FiltreField<Date> dataInici;
        private FiltreField<Date> dataFi;
        private FiltreField<String> titular;
        private FiltreField<OrganGestorEntity> organGestor;
        private FiltreField<ProcedimentEntity> procediment;
        private FiltreField<TipusUsuariEnumDto> tipusUsuari;
        private FiltreField<String> numExpedient;
        private FiltreField<String> creadaPer;
        private FiltreField<String> identificador;
        private FiltreField<Boolean> nomesAmbErrors;
        private FiltreField<Boolean> nomesSenseErrors;
        private FiltreField<Boolean> hasZeronotificaEnviamentIntent;
    }

    @Getter
    @AllArgsConstructor
    public static class FiltreField<T>{
        private T field;
        private Boolean isNull = null;

        public FiltreField(T field) {
            this.field = field;
        }

        public boolean isNull() {
            if (isNull == null) {
                if (field instanceof String){
                  return ((String) field).isEmpty();
                }
                return field == null;
            }
            return isNull;
        }
    }
    public Date toIniciDia(Date data) {
        if (data != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            data = cal.getTime();
        }
        return data;
    }

    public Date toFiDia(Date data) {
        if (data != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            cal.set(Calendar.HOUR, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            data = cal.getTime();
        }
        return data;
    }

}
