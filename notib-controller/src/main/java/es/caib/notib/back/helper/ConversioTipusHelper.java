/**
 * 
 */
package es.caib.notib.back.helper;

import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.back.command.DocumentCommand;
import es.caib.notib.back.command.EntregapostalCommand;
import es.caib.notib.back.command.EnviamentCommand;
import es.caib.notib.back.command.NotificacioCommand;
import es.caib.notib.back.command.PersonaCommand;
import es.caib.notib.logic.intf.dto.notificacio.Document;
import es.caib.notib.logic.intf.dto.notificacio.Enviament;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
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
        }).register();
		
		mapperFactory.classMap(PersonaDto.class, PersonaCommand.class)
		.byDefault()
		.customize(new CustomMapper<PersonaDto, PersonaCommand>() {
            @Override
            public void mapAtoB(PersonaDto personaDto, PersonaCommand personaCommand, MappingContext context) {
                if (InteressatTipus.JURIDICA.equals(personaDto.getInteressatTipus())) {
                	personaCommand.setNom(personaDto.getRaoSocial() != null ? personaDto.getRaoSocial() : personaDto.getNomInput());
                }
            }                   
        }).register();
		
		mapperFactory.classMap(EnviamentCommand.class, Enviament.class)
			.fieldAToB("entregaPostal.activa", "entregaPostalActiva")
			.field("entregaDeh.activa", "entregaDehActiva")
			.byDefault()
			.customize(new CustomMapper<EnviamentCommand, Enviament>() {
				@Override
				public void mapAtoB(EnviamentCommand command, Enviament dto, MappingContext context) {
					// empty
				}
				@Override
				public void mapBtoA(Enviament dto, EnviamentCommand command, MappingContext context) {
					var epCommand = command.getEntregaPostal() == null ? new EntregapostalCommand() : command.getEntregaPostal();
					epCommand.setActiva(dto.getEntregaPostal() != null);
					command.setEntregaPostal(epCommand);
				}
			}).register();

		mapperFactory.classMap(EnviamentCommand.class, NotificacioEnviamentDtoV2.class)
				.fieldAToB("entregaPostal.activa", "entregaPostalActiva")
				.field("entregaDeh.activa", "entregaDehActiva")
				.byDefault()
				.customize(new CustomMapper<EnviamentCommand, NotificacioEnviamentDtoV2>() {
					@Override
					public void mapAtoB(EnviamentCommand command, NotificacioEnviamentDtoV2 dto, MappingContext context) {
						// empty
					}
					@Override
					public void mapBtoA(NotificacioEnviamentDtoV2 dto, EnviamentCommand command, MappingContext context) {
						var epCommand = command.getEntregaPostal() == null ? new EntregapostalCommand() : command.getEntregaPostal();
						epCommand.setActiva(dto.getEntregaPostal() != null);
						command.setEntregaPostal(epCommand);
					}
				}).register();

		mapperFactory.classMap(Notificacio.class, NotificacioCommand.class)
				.byDefault()
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(Notificacio notificacioDto, NotificacioCommand notificacioCommand, MappingContext context) {
						// Documents
						var documents = new DocumentCommand[5];
						documents[0] = DocumentCommand.asCommand(notificacioDto.getDocument());
						documents[1] = DocumentCommand.asCommand(notificacioDto.getDocument2());
						documents[2] = DocumentCommand.asCommand(notificacioDto.getDocument3());
						documents[3] = DocumentCommand.asCommand(notificacioDto.getDocument4());
						documents[4] = DocumentCommand.asCommand(notificacioDto.getDocument5());
						notificacioCommand.setDocuments(documents);
//						notificacioCommand.setTipusProcSer(notificacioDto.getProcediment() != null ? notificacioDto.getProcediment().getTipus().name() : null);
					}
					@Override
					public void mapBtoA(NotificacioCommand notificacioCommand, Notificacio notificacioDto, MappingContext context) {
						// Documents
						List<Document> documents = new ArrayList<>();
						var document = DocumentCommand.asDto(notificacioCommand.getDocuments()[0]);
						if (document != null) {
							documents.add(document);
						}
						var document2 = DocumentCommand.asDto(notificacioCommand.getDocuments()[1]);
						if (document2 != null) {
							documents.add(document2);
						}
						var document3 = DocumentCommand.asDto(notificacioCommand.getDocuments()[2]);
						if (document3 != null) {
							documents.add(document3);
						}
						var document4 = DocumentCommand.asDto(notificacioCommand.getDocuments()[3]);
						if (document4 != null) {
							documents.add(document4);
						}
						var document5 = DocumentCommand.asDto(notificacioCommand.getDocuments()[4]);
						if (document5 != null) {
							documents.add(document5);
						}
						notificacioDto.setDocument(!documents.isEmpty() ? documents.get(0) : null);
						notificacioDto.setDocument2(documents.size() > 1 ? documents.get(1) : null);
						notificacioDto.setDocument3(documents.size() > 2 ? documents.get(2) : null);
						notificacioDto.setDocument4(documents.size() > 3 ? documents.get(3) : null);
						notificacioDto.setDocument5(documents.size() > 4 ? documents.get(4) : null);
//						if (EnviamentTipus.NOTIFICACIO.equals(notificacioCommand.getEnviamentTipus())){
//							notificacioDto.setEnviamentTipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO);
//						} else {
//							notificacioDto.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
//						}

					}
				}).register();

//		mapperFactory.classMap(NotificacioV2.class, NotificacioCommand.class)
//				.exclude("enviamentTipus")
//				.byDefault()
//				.customize(new CustomMapper<NotificacioV2, NotificacioCommand>() {
//					@Override
//					public void mapAtoB(NotificacioV2 notificacioDto, NotificacioCommand notificacioCommand, MappingContext context) {
//						// Documents
//						var documents = new DocumentCommand[5];
//						documents[0] = DocumentCommand.asCommand(notificacioDto.getDocument());
//						documents[1] = DocumentCommand.asCommand(notificacioDto.getDocument2());
//						documents[2] = DocumentCommand.asCommand(notificacioDto.getDocument3());
//						documents[3] = DocumentCommand.asCommand(notificacioDto.getDocument4());
//						documents[4] = DocumentCommand.asCommand(notificacioDto.getDocument5());
//						notificacioCommand.setDocuments(documents);
//						if (EnviamentTipus.NOTIFICACIO.equals(notificacioDto.getEnviamentTipus())){
//							notificacioCommand.setEnviamentTipus(TipusEnviamentEnumDto.NOTIFICACIO);
//							return;
//						}
//						notificacioCommand.setEnviamentTipus(TipusEnviamentEnumDto.COMUNICACIO);
//						if (notificacioDto.getEnviaments() != null && !notificacioDto.getEnviaments().isEmpty() &&
//								notificacioDto.getEnviaments().get(0).getTitular()!= null){
//
//							var titular = notificacioDto.getEnviaments().get(0).getTitular();
//							if (InteressatTipus.ADMINISTRACIO.equals(titular.getInteressatTipus())) {
//								notificacioCommand.setEnviamentTipus(TipusEnviamentEnumDto.COMUNICACIO_SIR);
//							}
//						}
//					}
//					@Override
//					public void mapBtoA(NotificacioCommand notificacioCommand, NotificacioV2 notificacioDto, MappingContext context) {
//						// Documents
//						List<DocumentV2> documents = new ArrayList<>();
//						var document = DocumentCommand.asDto(notificacioCommand.getDocuments()[0]);
//						if (document != null) {
//							documents.add(document);
//						}
//						var document2 = DocumentCommand.asDto(notificacioCommand.getDocuments()[1]);
//						if (document2 != null) {
//							documents.add(document2);
//						}
//						var document3 = DocumentCommand.asDto(notificacioCommand.getDocuments()[2]);
//						if (document3 != null) {
//							documents.add(document3);
//						}
//						var document4 = DocumentCommand.asDto(notificacioCommand.getDocuments()[3]);
//						if (document4 != null) {
//							documents.add(document4);
//						}
//						var document5 = DocumentCommand.asDto(notificacioCommand.getDocuments()[4]);
//						if (document5 != null)
//							documents.add(document5);
//						notificacioDto.setDocument(!documents.isEmpty() ? documents.get(0) : null);
//						notificacioDto.setDocument2(documents.size() > 1 ? documents.get(1) : null);
//						notificacioDto.setDocument3(documents.size() > 2 ? documents.get(2) : null);
//						notificacioDto.setDocument4(documents.size() > 3 ? documents.get(3) : null);
//						notificacioDto.setDocument5(documents.size() > 4 ? documents.get(4) : null);
//						notificacioDto.setOrganGestor(notificacioCommand.getOrganGestor());
//						if (TipusEnviamentEnumDto.NOTIFICACIO.equals(notificacioCommand.getEnviamentTipus())){
//							notificacioDto.setEnviamentTipus(EnviamentTipus.NOTIFICACIO);
//						} else {
//							notificacioDto.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
//						}
//					}
//				}).register();
	}

	
	public static <T> T convertir(Object source, Class<T> targetType) {
		return source != null ? getMapperFacade().map(source, targetType) : null;
	}

	public static <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		return items != null ? getMapperFacade().mapAsList(items, targetType) : null;
	}

	public static <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		return items != null ? getMapperFacade().mapAsSet(items, targetType) : null;
	}

	private static MapperFacade getMapperFacade() {
		return mapperFactory != null ? mapperFactory.getMapperFacade()
			: new DefaultMapperFactory.Builder().compilerStrategy(new CustomJavassistCompilerStrategy()).build().getMapperFacade();
	}

}
