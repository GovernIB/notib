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
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.entity.AplicacioEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
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
			exclude("destinataris").
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
	
	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
