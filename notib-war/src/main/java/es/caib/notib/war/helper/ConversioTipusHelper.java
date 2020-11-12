/**
 * 
 */
package es.caib.notib.war.helper;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.war.command.EnviamentCommand;
import es.caib.notib.war.command.PersonaCommand;
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

	private static MapperFactory mapperFactory;

	public ConversioTipusHelper() {
//		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory = new DefaultMapperFactory.Builder().compilerStrategy(new CustomJavassistCompilerStrategy()).build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(
							DateTime source,
							Type<? extends Date> destinationClass) {
						return source.toDate();
					}
				});
		
		mapperFactory.classMap(PersonaCommand.class, PersonaDto.class)
		.byDefault()
		.customize(new CustomMapper<PersonaCommand, PersonaDto>() {
            @Override
            public void mapAtoB(PersonaCommand personaCommand, PersonaDto personaDto, MappingContext context) {
                if (InteressatTipusEnumDto.JURIDICA.equals(personaCommand.getInteressatTipus())) {
                	personaDto.setRaoSocial(personaCommand.getNom());
                	personaDto.setNom(null);
                }
            }                   
        })
		.register();
		
		mapperFactory.classMap(PersonaDto.class, PersonaCommand.class)
		.byDefault()
		.customize(new CustomMapper<PersonaDto, PersonaCommand>() {
            @Override
            public void mapAtoB(PersonaDto personaDto, PersonaCommand personaCommand, MappingContext context) {
                if (InteressatTipusEnumDto.JURIDICA.equals(personaDto.getInteressatTipus())) {
                	personaCommand.setNom(personaDto.getRaoSocial() != null ? personaDto.getRaoSocial() : personaDto.getNom());
                }
            }                   
        })
		.register();
		
		mapperFactory.classMap(EnviamentCommand.class, NotificacioEnviamentDtoV2.class)
		.field("entregaPostal.activa", "entregaPostalActiva")
		.field("entregaDeh.activa", "entregaDehActiva")
		.byDefault()
		.register();
	}
	
	public static <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public static <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public static <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}



	private static MapperFacade getMapperFacade() {
		if (mapperFactory == null)
			mapperFactory = new DefaultMapperFactory.Builder().compilerStrategy(new CustomJavassistCompilerStrategy()).build();
		return mapperFactory.getMapperFacade();
	}
	
}
