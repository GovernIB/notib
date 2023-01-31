/**
 * 
 */
package es.caib.notib.back.helper;

import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.back.command.DocumentCommand;
import es.caib.notib.back.command.EntregapostalCommand;
import es.caib.notib.back.command.EnviamentCommand;
import es.caib.notib.back.command.NotificacioCommand;
import es.caib.notib.back.command.PersonaCommand;
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
@Component("backConversioTipusHelper")
public class ConversioTipusHelper {

	private static MapperFactory mapperFactory;

	public ConversioTipusHelper() {
//		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory = new DefaultMapperFactory.Builder().compilerStrategy(new CustomJavassistCompilerStrategy()).build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(
							DateTime source,
							Type<? extends Date> destinationClass,
							MappingContext mappingContext) {
						return source.toDate();
					}
				});
		
		mapperFactory.classMap(PersonaCommand.class, PersonaDto.class)
		.byDefault()
		.customize(new CustomMapper<PersonaCommand, PersonaDto>() {
            @Override
            public void mapAtoB(PersonaCommand personaCommand, PersonaDto personaDto, MappingContext context) {
                if (InteressatTipus.JURIDICA.equals(personaCommand.getInteressatTipus())) {
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
                if (InteressatTipus.JURIDICA.equals(personaDto.getInteressatTipus())) {
                	personaCommand.setNom(personaDto.getRaoSocial() != null ? personaDto.getRaoSocial() : personaDto.getNomInput());
                }
            }                   
        })
		.register();
		
		mapperFactory.classMap(EnviamentCommand.class, NotEnviamentDatabaseDto.class)
			.fieldAToB("entregaPostal.activa", "entregaPostalActiva")
			.field("entregaDeh.activa", "entregaDehActiva")
			.byDefault()
			.customize(new CustomMapper<EnviamentCommand, NotEnviamentDatabaseDto>() {
				@Override
				public void mapAtoB(EnviamentCommand command, NotEnviamentDatabaseDto dto, MappingContext context) {

				}
				@Override
				public void mapBtoA(NotEnviamentDatabaseDto dto, EnviamentCommand command, MappingContext context) {
					EntregapostalCommand epCommand = command.getEntregaPostal() == null ? new EntregapostalCommand() : command.getEntregaPostal();
					epCommand.setActiva(dto.getEntregaPostal() != null);
					command.setEntregaPostal(epCommand);
				}
			})
			.register();
		mapperFactory.classMap(EnviamentCommand.class, NotificacioEnviamentDtoV2.class)
				.fieldAToB("entregaPostal.activa", "entregaPostalActiva")
				.field("entregaDeh.activa", "entregaDehActiva")
				.byDefault()
				.customize(new CustomMapper<EnviamentCommand, NotificacioEnviamentDtoV2>() {
					@Override
					public void mapAtoB(EnviamentCommand command, NotificacioEnviamentDtoV2 dto, MappingContext context) {

					}
					@Override
					public void mapBtoA(NotificacioEnviamentDtoV2 dto, EnviamentCommand command, MappingContext context) {
						EntregapostalCommand epCommand = command.getEntregaPostal() == null ? new EntregapostalCommand() : command.getEntregaPostal();
						epCommand.setActiva(dto.getEntregaPostal() != null);
						command.setEntregaPostal(epCommand);
					}
				})
				.register();
		mapperFactory.classMap(NotificacioDtoV2.class, NotificacioCommand.class)
				.byDefault()
				.customize(new CustomMapper<NotificacioDtoV2, NotificacioCommand>() {
					@Override
					public void mapAtoB(NotificacioDtoV2 notificacioDto, NotificacioCommand notificacioCommand, MappingContext context) {
						int i = 0;
						// Documents
						DocumentCommand[] documents = new DocumentCommand[5];
						documents[0] = DocumentCommand.asCommand(notificacioDto.getDocument());
						documents[1] = DocumentCommand.asCommand(notificacioDto.getDocument2());
						documents[2] = DocumentCommand.asCommand(notificacioDto.getDocument3());
						documents[3] = DocumentCommand.asCommand(notificacioDto.getDocument4());
						documents[4] = DocumentCommand.asCommand(notificacioDto.getDocument5());
						notificacioCommand.setDocuments(documents);
						notificacioCommand.setTipusProcSer(notificacioDto.getProcediment() != null ? notificacioDto.getProcediment().getTipus().name() : null);
					}
					@Override
					public void mapBtoA(NotificacioCommand notificacioCommand, NotificacioDtoV2 notificacioDto, MappingContext context) {
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
						if (TipusEnviamentEnumDto.NOTIFICACIO.equals(notificacioCommand.getEnviamentTipus())){
							notificacioDto.setEnviamentTipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO);
						} else {
							notificacioDto.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
						}

					}
				})
				.register();
		mapperFactory.classMap(NotificacioDatabaseDto.class, NotificacioCommand.class)
				.exclude("enviamentTipus")
				.byDefault()
				.customize(new CustomMapper<NotificacioDatabaseDto, NotificacioCommand>() {
					@Override
					public void mapAtoB(NotificacioDatabaseDto notificacioDto, NotificacioCommand notificacioCommand, MappingContext context) {
						int i = 0;
						// Documents
						DocumentCommand[] documents = new DocumentCommand[5];
						documents[0] = DocumentCommand.asCommand(notificacioDto.getDocument());
						documents[1] = DocumentCommand.asCommand(notificacioDto.getDocument2());
						documents[2] = DocumentCommand.asCommand(notificacioDto.getDocument3());
						documents[3] = DocumentCommand.asCommand(notificacioDto.getDocument4());
						documents[4] = DocumentCommand.asCommand(notificacioDto.getDocument5());
						notificacioCommand.setDocuments(documents);
						if (NotificaEnviamentTipusEnumDto.NOTIFICACIO.equals(notificacioDto.getEnviamentTipus())){
							notificacioCommand.setEnviamentTipus(TipusEnviamentEnumDto.NOTIFICACIO);
						} else {
							notificacioCommand.setEnviamentTipus(TipusEnviamentEnumDto.COMUNICACIO);
							if (notificacioDto.getEnviaments() != null && !notificacioDto.getEnviaments().isEmpty() &&
									notificacioDto.getEnviaments().get(0).getTitular()!= null){
								PersonaDto titular = notificacioDto.getEnviaments().get(0).getTitular();
								if (InteressatTipus.ADMINISTRACIO.equals(titular.getInteressatTipus())) {
									notificacioCommand.setEnviamentTipus(TipusEnviamentEnumDto.COMUNICACIO_SIR);
								}
							}
						}
					}
					@Override
					public void mapBtoA(NotificacioCommand notificacioCommand, NotificacioDatabaseDto notificacioDto, MappingContext context) {
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
						notificacioDto.setOrganGestorCodi(notificacioCommand.getOrganGestor());
						if (TipusEnviamentEnumDto.NOTIFICACIO.equals(notificacioCommand.getEnviamentTipus())){
							notificacioDto.setEnviamentTipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO);
						} else {
							notificacioDto.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
						}
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
