/**
 * 
 */
package es.caib.notib.war.helper;

import es.caib.notib.core.api.dto.DocumentDto;
import es.caib.notib.core.api.dto.InteressatTipusEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.war.command.DocumentCommand;
import es.caib.notib.war.command.EnviamentCommand;
import es.caib.notib.war.command.NotificacioCommandV2;
import es.caib.notib.war.command.PersonaCommand;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

		mapperFactory.classMap(NotificacioDtoV2.class, NotificacioCommandV2.class)
				.byDefault()
				.customize(new CustomMapper<NotificacioDtoV2, NotificacioCommandV2>() {
					@Override
					public void mapAtoB(NotificacioDtoV2 notificacioDto, NotificacioCommandV2 notificacioCommand, MappingContext context) {
						int i = 0;
						// Documents
						DocumentCommand[] documents = new DocumentCommand[5];
						documents[0] = DocumentCommand.asCommand(notificacioDto.getDocument());
						documents[1] = DocumentCommand.asCommand(notificacioDto.getDocument2());
						documents[2] = DocumentCommand.asCommand(notificacioDto.getDocument3());
						documents[3] = DocumentCommand.asCommand(notificacioDto.getDocument4());
						documents[4] = DocumentCommand.asCommand(notificacioDto.getDocument5());
						notificacioCommand.setDocuments(documents);
					}
					@Override
					public void mapBtoA(NotificacioCommandV2 notificacioCommand, NotificacioDtoV2 notificacioDto, MappingContext context) {
						// Documents
						List<DocumentDto> documents = new ArrayList<>();
						DocumentDto document = DocumentCommand.asDto(notificacioCommand.getDocuments()[0]);
						if (document != null)
							documents.add(document);
						DocumentDto document2 = DocumentCommand.asDto(notificacioCommand.getDocuments()[1]);
						if (document2 != null)
							documents.add(document2);
						DocumentDto document3 = DocumentCommand.asDto(notificacioCommand.getDocuments()[2]);
						if (document3 != null)
							documents.add(document3);
						DocumentDto document4 = DocumentCommand.asDto(notificacioCommand.getDocuments()[3]);
						if (document4 != null)
							documents.add(document4);
						DocumentDto document5 = DocumentCommand.asDto(notificacioCommand.getDocuments()[4]);
						if (document5 != null)
							documents.add(document5);
						notificacioDto.setDocument(documents.size() > 0 ? documents.get(0) : null);
						notificacioDto.setDocument2(documents.size() > 1 ? documents.get(1) : null);
						notificacioDto.setDocument3(documents.size() > 2 ? documents.get(2) : null);
						notificacioDto.setDocument4(documents.size() > 3 ? documents.get(3) : null);
						notificacioDto.setDocument5(documents.size() > 4 ? documents.get(4) : null);
					}
				})
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
