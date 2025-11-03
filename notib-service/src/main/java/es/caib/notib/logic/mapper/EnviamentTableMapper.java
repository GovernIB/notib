package es.caib.notib.logic.mapper;

import es.caib.notib.logic.helper.EnviamentTableHelper;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.persist.entity.EnviamentTableEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Slf4j
@Mapper(componentModel = "spring",
        uses = {CommonConversor.class},
        builder = @org.mapstruct.Builder(disableBuilder = true),
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public abstract class EnviamentTableMapper {

    @Autowired
    private EnviamentTableHelper enviamentTableHelper;
    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;


    @Mapping(target = "createdDate", source = "env.createdDate", qualifiedByName = "optionalDate")
//    @Mapping(target = "enviadaDate", source = "env.enviament.ultimEvent.data")
//    @Mapping(target = "notificacioId", source = "env.notId")
//    @Mapping(target = "procedimentNom", source = "env.notificacio.procediment.nom")
//    @Mapping(target = "organNom", source = "env.notificacio.organGestor.nom")
    @Mapping(target = "codiNotibEnviament", source = "env.notificaReferencia")
//    @Mapping(target = "referenciaNotificacio", source = "env.notificacio.referencia")
    public abstract NotEnviamentTableItemDto toNotEnviamentTableItemDto(EnviamentTableEntity env);

    public abstract List<NotEnviamentTableItemDto> toNotEnviamentsTableItemDto(List<EnviamentTableEntity> envs);

    @Named("optionalProcName")
    String optionalProcName(ProcSerEntity procediment) {
        return procediment != null ? procediment.getNom() : null;
    }

    Date mapDate(Date value) {
        return value; // Implement any specific logic here, or modify as needed
    }

    @AfterMapping
    protected void actualtizarCamps(EnviamentTableEntity env, @MappingTarget NotEnviamentTableItemDto dto) {

        if (env.getAnulable() == null) {
//            var enviament = notificacioEnviamentRepository.findById(env.getId()).orElse(null);
//            if (enviament != null) {
            var anulable = enviamentTableHelper.isAnulable(env.getEnviament());
                env.setAnulable(anulable);
                dto.setAnulable(anulable);
//                notificacioEnviamentRepository.saveAndFlush(enviament);
//            }
        }
        if (Strings.isNullOrEmpty(env.getOrganNom())) {
            var nom = env.getEnviament().getNotificacio().getOrganGestor().getNom();
            env.setOrganNom(nom);
            dto.setOrganNom(nom);
        }
        if (Strings.isNullOrEmpty(env.getProcedimentNom()) && !Strings.isNullOrEmpty(env.getProcedimentCodiNotib())) {
            var nom = env.getEnviament().getNotificacio().getProcediment().getNom();
            env.setProcedimentNom(nom);
            dto.setProcedimentNom(nom);
        }
        if (Strings.isNullOrEmpty(env.getReferenciaNotificacio())) {
            var referencia = env.getNotificacio().getReferencia();
            env.setReferenciaNotificacio(referencia);
            dto.setReferenciaNotificacio(referencia);
        }
        if (env.getNotificacioId() == null) {
            var notificacioId = env.getNotificacio().getId();
            env.setNotificacioId(notificacioId);
            dto.setNotificacioId(notificacioId);
        }
        if (env.getEnviadaDate() == null) {
            var event =  env.getEnviament().getUltimEvent();
            var data = event != null ? event.getData() : null;
            env.setEnviadaDate(data);
            dto.setEnviadaDate(data);
        }

    }
}
