/**
 * 
 */
package es.caib.notib.core.helper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.NotificacioDto;
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
		/*mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<EntregaPostalTipusEnum, NotificaDomiciliTipusEnumDto>() {
					public NotificaDomiciliTipusEnumDto convert(
							EntregaPostalTipusEnum source,
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
				new CustomConverter<NotificaDomiciliTipusEnumDto, EntregaPostalTipusEnum>() {
					public EntregaPostalTipusEnum convert(
							NotificaDomiciliTipusEnumDto source,
							Type<? extends EntregaPostalTipusEnum> destinationClass) {
						switch (source) {
						case CONCRETO:
							return EntregaPostalTipusEnum.CONCRET;
						case FISCAL:
							return EntregaPostalTipusEnum.FISCAL;
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
				new CustomConverter<EntregaPostalNumeracioTipusEnum, NotificaDomiciliNumeracioTipusEnumDto>() {
					public NotificaDomiciliNumeracioTipusEnumDto convert(
							EntregaPostalNumeracioTipusEnum source,
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
				new CustomConverter<NotificaDomiciliNumeracioTipusEnumDto, EntregaPostalNumeracioTipusEnum>() {
					public EntregaPostalNumeracioTipusEnum convert(
							NotificaDomiciliNumeracioTipusEnumDto source,
							Type<? extends EntregaPostalNumeracioTipusEnum> destinationClass) {
						switch (source) {
						case APARTADO_CORREOS:
							return EntregaPostalNumeracioTipusEnum.APARTAT_CORREUS;
						case NUMERO:
							return EntregaPostalNumeracioTipusEnum.NUMERO;
						case PUNTO_KILOMETRICO:
							return EntregaPostalNumeracioTipusEnum.PUNT_KILOMETRIC;
						case SIN_NUMERO:
							return EntregaPostalNumeracioTipusEnum.SENSE_NUMERO;
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
				});*/
		mapperFactory.classMap(NotificacioEntity.class, NotificacioDto.class).
		field("notificaErrorEvent.data", "errorNotificaData").
		field("notificaErrorEvent.errorDescripcio", "errorNotificaError").
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
