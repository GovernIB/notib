/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliNumeracioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaDomiciliTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.ws.notificacio2.DomiciliConcretTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.DomiciliNumeracioTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.DomiciliTipusEnum;
import es.caib.notib.core.api.ws.notificacio2.ServeiTipusEnum;
import es.caib.notib.core.entity.NotificacioEntity;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
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
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DomiciliTipusEnum, NotificaDomiciliTipusEnumDto>() {
					public NotificaDomiciliTipusEnumDto convert(
							DomiciliTipusEnum source,
							Type<? extends NotificaDomiciliTipusEnumDto> destinationClass) {
						switch (source) {
						case CONCRET:
							return NotificaDomiciliTipusEnumDto.CONCRETO;
						case FISCAL:
							return NotificaDomiciliTipusEnumDto.FISCAL;
						default:
							return null;
						}
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<NotificaDomiciliTipusEnumDto, DomiciliTipusEnum>() {
					public DomiciliTipusEnum convert(
							NotificaDomiciliTipusEnumDto source,
							Type<? extends DomiciliTipusEnum> destinationClass) {
						switch (source) {
						case CONCRETO:
							return DomiciliTipusEnum.CONCRET;
						case FISCAL:
							return DomiciliTipusEnum.FISCAL;
						default:
							return null;
						}
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DomiciliConcretTipusEnum, NotificaDomiciliConcretTipusEnumDto>() {
					public NotificaDomiciliConcretTipusEnumDto convert(
							DomiciliConcretTipusEnum source,
							Type<? extends NotificaDomiciliConcretTipusEnumDto> destinationClass) {
						switch (source) {
						case APARTAT_CORREUS:
							return NotificaDomiciliConcretTipusEnumDto.APARTADO_CORREOS;
						case ESTRANGER:
							return NotificaDomiciliConcretTipusEnumDto.EXTRANJERO;
						case NACIONAL:
							return NotificaDomiciliConcretTipusEnumDto.NACIONAL;
						case SENSE_NORMALITZAR:
							return NotificaDomiciliConcretTipusEnumDto.SIN_NORMALIZAR;
						default:
							return null;
						}
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<NotificaDomiciliConcretTipusEnumDto, DomiciliConcretTipusEnum>() {
					public DomiciliConcretTipusEnum convert(
							NotificaDomiciliConcretTipusEnumDto source,
							Type<? extends DomiciliConcretTipusEnum> destinationClass) {
						switch (source) {
						case APARTADO_CORREOS:
							return DomiciliConcretTipusEnum.APARTAT_CORREUS;
						case EXTRANJERO:
							return DomiciliConcretTipusEnum.ESTRANGER;
						case NACIONAL:
							return DomiciliConcretTipusEnum.NACIONAL;
						case SIN_NORMALIZAR:
							return DomiciliConcretTipusEnum.SENSE_NORMALITZAR;
						default:
							return null;
						}
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DomiciliNumeracioTipusEnum, NotificaDomiciliNumeracioTipusEnumDto>() {
					public NotificaDomiciliNumeracioTipusEnumDto convert(
							DomiciliNumeracioTipusEnum source,
							Type<? extends NotificaDomiciliNumeracioTipusEnumDto> destinationClass) {
						switch (source) {
						case APARTAT_CORREUS:
							return NotificaDomiciliNumeracioTipusEnumDto.APARTADO_CORREOS;
						case NUMERO:
							return NotificaDomiciliNumeracioTipusEnumDto.NUMERO;
						case PUNT_KILOMETRIC:
							return NotificaDomiciliNumeracioTipusEnumDto.PUNTO_KILOMETRICO;
						case SENSE_NUMERO:
							return NotificaDomiciliNumeracioTipusEnumDto.SIN_NUMERO;
						default:
							return null;
						}
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<NotificaDomiciliNumeracioTipusEnumDto, DomiciliNumeracioTipusEnum>() {
					public DomiciliNumeracioTipusEnum convert(
							NotificaDomiciliNumeracioTipusEnumDto source,
							Type<? extends DomiciliNumeracioTipusEnum> destinationClass) {
						switch (source) {
						case APARTADO_CORREOS:
							return DomiciliNumeracioTipusEnum.APARTAT_CORREUS;
						case NUMERO:
							return DomiciliNumeracioTipusEnum.NUMERO;
						case PUNTO_KILOMETRICO:
							return DomiciliNumeracioTipusEnum.PUNT_KILOMETRIC;
						case SIN_NUMERO:
							return DomiciliNumeracioTipusEnum.SENSE_NUMERO;
						default:
							return null;
						}
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ServeiTipusEnum, NotificaServeiTipusEnumDto>() {
					public NotificaServeiTipusEnumDto convert(
							ServeiTipusEnum source,
							Type<? extends NotificaServeiTipusEnumDto> destinationClass) {
						switch (source) {
						case NORMAL:
							return NotificaServeiTipusEnumDto.NORMAL;
						case URGENT:
							return NotificaServeiTipusEnumDto.URGENTE;
						default:
							return null;
						}
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<NotificaServeiTipusEnumDto, ServeiTipusEnum>() {
					public ServeiTipusEnum convert(
							NotificaServeiTipusEnumDto source,
							Type<? extends ServeiTipusEnum> destinationClass) {
						switch (source) {
						case NORMAL:
							return ServeiTipusEnum.NORMAL;
						case URGENTE:
							return ServeiTipusEnum.URGENT;
						default:
							return null;
						}
					}
				});
		mapperFactory.classMap(NotificacioEntity.class, NotificacioDto.class).
		exclude("destinataris").
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



	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
