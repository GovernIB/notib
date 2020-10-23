/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.AplicacioDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFormatFullaDto;
import es.caib.notib.core.api.dto.PagadorCieFormatSobreDto;
import es.caib.notib.core.api.dto.PagadorPostalDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.PagadorCieEntity;
import es.caib.notib.core.entity.PagadorCieFormatFullaEntity;
import es.caib.notib.core.entity.PagadorCieFormatSobreEntity;
import es.caib.notib.core.entity.PagadorPostalEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.UsuariEntity;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelper {

	private MapperFactory mapperFactory;

	public ConversioTipusHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(
							DateTime source,
							Type<? extends Date> destinationClass) {
						return source.toDate();
					}
				});
		
		mapperFactory.classMap(EntitatEntity.class, EntitatDto.class).
			customize(new EntitatEntitytoMapper()).
			byDefault().
			register();
		
		mapperFactory.classMap(NotificacioEntity.class, NotificacioDto.class).
			field("notificaErrorEvent.data", "notificaErrorData").
			field("notificaErrorEvent.errorDescripcio", "notificaErrorDescripcio").
			field("organGestor.codi", "organGestor").
			field("organGestor.nom", "organGestorNom").
			exclude("destinataris").
			byDefault().
			register();
		
		mapperFactory.classMap(NotificacioEntity.class, NotificacioDtoV2.class).
			field("organGestor.codi", "organGestor").
			field("organGestor.nom", "organGestorNom").
			byDefault().
			register();
		
		mapperFactory.classMap(NotificacioEnviamentEntity.class, NotificacioEnviamentDto.class).
			customize(new NotificacioEnviamentEntitytoMapper()).
			byDefault().
			register();
		
		mapperFactory.classMap(AplicacioEntity.class, AplicacioDto.class).
			field("entitat.id", "entitatId").
			byDefault().
			register();
		
		mapperFactory.classMap(UsuariEntity.class, UsuariDto.class).
			customize(new UsuariEntitytoMapper()).
			byDefault().
			register();
		
		mapperFactory.classMap(OrganGestorEntity.class, OrganGestorDto.class).
			field("entitat.id", "entitatId").
			field("entitat.nom", "entitatNom").
			byDefault().
			register();
		
		mapperFactory.classMap(ProcedimentEntity.class, ProcedimentDto.class).
			field("organGestor.codi", "organGestor").
			field("organGestor.nom", "organGestorNom").
			byDefault().
			register();
		
		mapperFactory.classMap(GrupEntity.class, GrupDto.class).
			field("entitat.id", "entitatId").
			field("organGestor.id", "organGestorId").
			field("organGestor.codi", "organGestorCodi").
			byDefault().
			register();

		mapperFactory.classMap(PagadorCieEntity.class, PagadorCieDto.class).
			field("entitat.id", "entitatId").
			field("organGestor.id", "organGestorId").
			field("organGestor.codi", "organGestorCodi").
			byDefault().
			register();
		
		mapperFactory.classMap(PagadorPostalEntity.class, PagadorPostalDto.class).
			field("entitat.id", "entitatId").
			field("organGestor.id", "organGestorId").
			field("organGestor.codi", "organGestorCodi").
			byDefault().
			register();
		
		mapperFactory.classMap(PagadorCieFormatFullaEntity.class, PagadorCieFormatFullaDto.class).
			field("pagadorCie.id", "pagadorCieId").
			byDefault().
			register();
		
		mapperFactory.classMap(PagadorCieFormatSobreEntity.class, PagadorCieFormatSobreDto.class).
			field("pagadorCie.id", "pagadorCieId").
			byDefault().
			register();
	
	}

	public <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}

	public class UsuariEntitytoMapper extends CustomMapper<UsuariEntity, UsuariDto> {
		@Override
		public void mapAtoB(
				UsuariEntity usuariEntity, 
				UsuariDto usuariDto, 
				MappingContext context) {
			if (usuariEntity.getNomSencer() != null && !usuariEntity.getNomSencer().isEmpty() && usuariEntity.getNomSencer().trim().length() > 0) {
				usuariDto.setNom(usuariEntity.getNomSencer());
			} else if (usuariEntity.getLlinatges() != null && !usuariEntity.getLlinatges().isEmpty() && usuariEntity.getLlinatges().trim().length() > 0) {
				usuariDto.setNom(usuariEntity.getNom() + " " + usuariEntity.getLlinatges());
			}
		}
	}
	
	public class EntitatEntitytoMapper extends CustomMapper<EntitatEntity, EntitatDto> {
		@Override
		public void mapAtoB(
				EntitatEntity entitatEntity, 
				EntitatDto entitatDto, 
				MappingContext context) {
			if (entitatEntity.getTipusDocDefault() != null) {
				TipusDocumentDto tipusDocumentDto = new TipusDocumentDto();
				tipusDocumentDto.setEntitat(entitatEntity.getId());
				tipusDocumentDto.setTipusDocEnum(entitatEntity.getTipusDocDefault());
				entitatDto.setTipusDocDefault(tipusDocumentDto);
			}
		}
	}
	
	public class NotificacioEnviamentEntitytoMapper extends CustomMapper<NotificacioEnviamentEntity, NotificacioEnviamentDto> {
		@Override
		public void mapAtoB(
				NotificacioEnviamentEntity notificacioEnviamentEntity, 
				NotificacioEnviamentDto notificacioEnviamentDto, 
				MappingContext context) {
			if (notificacioEnviamentEntity.isNotificaError()) {
				NotificacioEventEntity event = notificacioEnviamentEntity.getNotificacioErrorEvent();
				if (event != null) {
					notificacioEnviamentDto.setNotificaErrorData(event.getData());
					notificacioEnviamentDto.setNotificaErrorDescripcio(event.getErrorDescripcio());
				}
			}
		}
	}
	
	
	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
