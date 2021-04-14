/**
 * 
 */
package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.core.api.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.core.entity.*;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.unitat.ObjetoDirectorio;
import ma.glasnost.orika.*;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

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

		mapperFactory.classMap(NotificacioEntity.class, NotificacioDatabaseDto.class).
				field("organGestor.codi", "organGestorCodi").
				byDefault().
				register();

		mapperFactory.classMap(NotificacioEntity.class, NotificacioTableItemDto.class).
				field("notificaErrorEvent.data", "notificaErrorData").
				field("notificaErrorEvent.errorDescripcio", "notificaErrorDescripcio").
				field("organGestor.codi", "organGestor").
				field("organGestor.nom", "organGestorNom").
//				field("entitat.id", "entitatId").
				field("entitat.nom", "entitatNom").
				field("procediment.codi", "procedimentCodi").
				field("procediment.nom", "procedimentNom").
				field("createdBy.nom", "createdByNom").
				field("createdBy.codi", "createdByCodi").
				byDefault().
				register();
		
		mapperFactory.classMap(NotificacioEnviamentEntity.class, NotificacioEnviamentDto.class).
			customize(new NotificacioEnviamentEntitytoMapper()).
			byDefault().
			register();

		mapperFactory.classMap(NotificacioEnviamentEntity.class, NotificacioEnviamentDatatableDto.class).
				field("notificacio.estat", "notificacioEstat").
				customize(new NotificacioEnviamentEntitytoDatatableMapper()).
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
			field("entitat.oficina", "oficinaNom").
			field("oficina", "oficina.codi").
			field("oficinaNom", "oficina.nom").
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
		
//		mapperFactory.classMap(ProcedimentOrganEntity.class, ProcedimentOrganDto.class).
//			field("procediment.id", "procedimentId").
//			field("organGestor.codi", "organGestor").
//			byDefault().
//			register();

		mapperFactory.classMap(NodeDir3.class, OrganGestorDto.class).
			field("denominacio", "nom").
			byDefault().
			register();
		
		mapperFactory.classMap(ObjetoDirectorio.class, OrganGestorDto.class).
			field("denominacio", "nom").
			byDefault().
			register();
		
		mapperFactory.classMap(CodiValor.class, CodiValorDto.class).
			field("id", "codi").
			field("descripcio", "valor").
			byDefault().
			register();
		mapperFactory.classMap(NotificacioEnviamentEntity.class, NotificacioEnviamentDtoV2.class).
				field("notificacio.id", "notificacioId").
				customize(new NotificacioEnviamentEntitytoDtoV2Mapper()).
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

	public class NotificacioEnviamentEntitytoDatatableMapper extends CustomMapper<NotificacioEnviamentEntity, NotificacioEnviamentDatatableDto> {
		@Override
		public void mapAtoB(
				NotificacioEnviamentEntity notificacioEnviamentEntity,
				NotificacioEnviamentDatatableDto notificacioEnviamentDto,
				MappingContext context) {
			if (notificacioEnviamentEntity.isNotificaError()) {
				NotificacioEventEntity event = notificacioEnviamentEntity.getNotificacioErrorEvent();
				if (event != null) {
					notificacioEnviamentDto.setNotificacioErrorData(event.getData());
					notificacioEnviamentDto.setNotificacioErrorDescripcio(event.getErrorDescripcio());
				}
			}
		}
	}

	public class NotificacioEnviamentEntitytoDtoV2Mapper extends CustomMapper<NotificacioEnviamentEntity, NotificacioEnviamentDtoV2> {
		@Override
		public void mapAtoB(
				NotificacioEnviamentEntity notificacioEnviamentEntity,
				NotificacioEnviamentDtoV2 notificacioEnviamentDto,
				MappingContext context) {
			NotificacioEventEntity errorEvent = notificacioEnviamentEntity.getNotificacioErrorEvent();
			NotificacioEntity notificacio = notificacioEnviamentEntity.getNotificacio();
			if (errorEvent == null && notificacio.getRegistreEnviamentIntent() == 0 &&
					notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT)) {
				notificacioEnviamentDto.setEnviant(true);
			} else {
				notificacioEnviamentDto.setEnviant(false);
			}
		}
	}
	
	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
