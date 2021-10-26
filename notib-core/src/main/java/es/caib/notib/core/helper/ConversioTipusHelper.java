/**
 * 
 */
package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.cie.*;
import es.caib.notib.core.api.dto.notenviament.EnviamentInfoDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.core.api.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.core.api.dto.notificacio.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.dto.procediment.ProcSerOrganDto;
import es.caib.notib.core.api.ws.notificacio.EntregaPostal;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.entity.auditoria.NotificacioAudit;
import es.caib.notib.core.entity.auditoria.NotificacioEnviamentAudit;
import es.caib.notib.core.entity.cie.*;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.unitat.ObjetoDirectorio;
import ma.glasnost.orika.*;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Locale;
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
		
		mapperFactory.classMap(EntitatEntity.class, EntitatDto.class)
			.byDefault()
			.customize(new EntitatEntitytoMapper())
			.register();
		
//		mapperFactory.classMap(NotificacioEntity.class, NotificacioDto.class).
//			field("notificaErrorEvent.data", "notificaErrorData").
//			field("notificaErrorEvent.errorDescripcio", "notificaErrorDescripcio").
//			field("organGestor.codi", "organGestor").
//			field("organGestor.nom", "organGestorNom").
//			exclude("destinataris").
//			byDefault().
//			register();
		mapperFactory.classMap(NotificacioEntity.class, NotificacioInfoDto.class).
				field("organGestor.codi", "organGestorCodi").
				field("organGestor.nom", "organGestorNom").
				byDefault().
				register();

		mapperFactory.classMap(EntregaPostalEntity.class, EntregaPostalDto.class).
				field("domiciliViaTipus", "viaTipus").
				field("domiciliViaNom", "viaNom").
				field("domiciliNumeracioNumero", "numeroCasa").
				field("domiciliNumeracioQualificador", "numeroQualificador").
				field("domiciliNumeracioPuntKm", "puntKm").
				field("domiciliApartatCorreus", "apartatCorreus").
				field("domiciliPortal", "portal").
				field("domiciliEscala", "escala").
				field("domiciliPlanta", "planta").
				field("domiciliPorta", "porta").
				field("domiciliBloc", "bloc").
				field("domiciliComplement", "complement").
				field("domiciliCodiPostal", "codiPostal").
//				field("", "codiPostalNorm").
				field("domiciliPoblacio", "poblacio").
				field("domiciliMunicipiCodiIne", "municipiCodi").
				field("domiciliProvinciaCodi", "provincia").
				field("domiciliPaisCodiIso", "paisCodi").
				field("domiciliLinea1", "linea1").
				field("domiciliLinea2", "linea2").
				field("formatSobre", "formatSobre").
				field("formatFulla", "formatFulla").
				field("domiciliCie", "cie").
//				field("", "activa").
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

		mapperFactory.classMap(NotificacioTableEntity.class, NotificacioTableItemDto.class).
				field("createdBy.nom", "createdByNom").
				field("createdBy.codi", "createdByCodi").
				byDefault().
				register();

		mapperFactory.classMap(NotificacioMassivaEntity.class, NotificacioMassivaTableItemDto.class).
				field("createdBy.nom", "createdByNom").
				field("createdBy.codi", "createdByCodi").
				byDefault().
				register();

		mapperFactory.classMap(EnviamentTableEntity.class, NotEnviamentTableItemDto.class).
				field("notificaReferencia", "codiNotibEnviament").
				field("notificacio.id", "notificacioId").
				field("csv_uuid", "csvUuid").
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
		mapperFactory.classMap(NotificacioEnviamentEntity.class, EnviamentInfoDto.class).
				field("notificacio.estat", "notificacioEstat").
				customize(new NotificacioEnviamentEntitytoInfoMapper()).
				byDefault().
				register();

		mapperFactory.classMap(EntregaPostalDto.class, EntregaPostal.class).
				field("domiciliConcretTipus", "tipus").
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
			field("oficinaNom", "oficina.nom")
			.customize(
				new CustomMapper<OrganGestorEntity, OrganGestorDto>() {
					public void mapAtoB(OrganGestorEntity a, OrganGestorDto b, MappingContext context) {
						// add your custom mapping code here
						b.setEntregaCieActiva(a.getEntregaCie() != null);
						if (a.getEntregaCie() != null) {
							b.setOperadorPostalId(a.getEntregaCie().getOperadorPostalId());
							b.setCieId(a.getEntregaCie().getCieId());
						}
					}
				})
			.byDefault()
			.register();
		
		mapperFactory.classMap(ProcedimentEntity.class, ProcSerDto.class).
			field("organGestor.codi", "organGestor")
				.field("organGestor.nom", "organGestorNom")
				.customize(
				new CustomMapper<ProcedimentEntity, ProcSerDto>() {
					public void mapAtoB(ProcedimentEntity a, ProcSerDto b, MappingContext context) {
						// add your custom mapping code here
						b.setEntregaCieActiva(a.getEntregaCie() != null);
						if (a.getEntregaCie() != null) {
							b.setOperadorPostalId(a.getEntregaCie().getOperadorPostalId());
							b.setCieId(a.getEntregaCie().getCieId());
						}
					}
				})
				.byDefault()
				.register();

		mapperFactory.classMap(ServeiEntity.class, ProcSerDto.class).
				field("organGestor.codi", "organGestor")
				.field("organGestor.nom", "organGestorNom")
				.customize(
						new CustomMapper<ServeiEntity, ProcSerDto>() {
							public void mapAtoB(ServeiEntity a, ProcSerDto b, MappingContext context) {
								// add your custom mapping code here
								b.setEntregaCieActiva(a.getEntregaCie() != null);
								if (a.getEntregaCie() != null) {
									b.setOperadorPostalId(a.getEntregaCie().getOperadorPostalId());
									b.setCieId(a.getEntregaCie().getCieId());
								}
							}
						})
				.byDefault()
				.register();

		mapperFactory.classMap(GrupEntity.class, GrupDto.class).
			field("entitat.id", "entitatId").
			field("organGestor.id", "organGestorId").
			field("organGestor.codi", "organGestorCodi").
			byDefault().
			register();

		mapperFactory.classMap(PagadorCieEntity.class, CieDto.class).
			field("entitat.id", "entitatId").
			field("organGestor.id", "organGestorId").
			byDefault().
			register();

		mapperFactory.classMap(PagadorCieEntity.class, CieTableItemDto.class)
			.customize(
				new CustomMapper<PagadorCieEntity, CieTableItemDto>() {
					public void mapAtoB(PagadorCieEntity a, CieTableItemDto b, MappingContext context) {
						if (a.getOrganismePagador() == null) {
							b.setOrganismePagador(a.getOrganismePagadorCodi() + " - ORGAN GESTOR NO TROBAT");
						} else {
							b.setOrganismePagador(a.getOrganismePagadorCodi() + " - " + a.getOrganismePagador().getNom());
						}
					}
				})
			.byDefault()
			.register();

		mapperFactory.classMap(PagadorPostalEntity.class, OperadorPostalDto.class).
			field("entitat.id", "entitatId").
			byDefault().
			register();

		mapperFactory.classMap(PagadorPostalEntity.class, OperadorPostalTableItemDto.class)
			.customize(
				new CustomMapper<PagadorPostalEntity, OperadorPostalTableItemDto>() {
					public void mapAtoB(PagadorPostalEntity a, OperadorPostalTableItemDto b, MappingContext context) {
						if (a.getOrganismePagador() == null) {
							b.setOrganismePagador(a.getOrganismePagadorCodi() + " - ORGAN GESTOR NO TROBAT");
						} else {
							b.setOrganismePagador(a.getOrganismePagadorCodi() + " - " + a.getOrganismePagador().getNom());
						}
					}
				})
			.byDefault()
			.register();

		mapperFactory.classMap(PagadorCieFormatFullaEntity.class, CieFormatFullaDto.class).
			field("pagadorCie.id", "pagadorCieId").
			byDefault().
			register();
		
		mapperFactory.classMap(PagadorCieFormatSobreEntity.class, CieFormatSobreDto.class).
			field("pagadorCie.id", "pagadorCieId").
			byDefault().
			register();
		mapperFactory.classMap(PagadorPostalEntity.class, IdentificadorTextDto.class)
				.customize(
				new CustomMapper<PagadorPostalEntity, IdentificadorTextDto>() {
					public void mapAtoB(PagadorPostalEntity a, IdentificadorTextDto b, MappingContext context) {
						b.setText(a.getNom() + " - " + a.getContracteNum());
					}
				})
				.byDefault()
				.register();
		mapperFactory.classMap(PagadorCieEntity.class, IdentificadorTextDto.class)
				.customize(
						new CustomMapper<PagadorCieEntity, IdentificadorTextDto>() {
							public void mapAtoB(PagadorCieEntity a, IdentificadorTextDto b, MappingContext context) {
								// add your custom mapping code here
								b.setText(a.getNom() + " (Fins el " + a.getContracteDataVig() + ")");
							}
						})
				.byDefault()
				.register();
		mapperFactory.classMap(NodeDir3.class, OrganGestorDto.class).
			field("denominacio", "nom").
			field("tieneOficinaSir", "sir").
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

		mapperFactory.classMap(NotificacioAudit.class, NotificacioAuditDto.class).
				field("createdBy.codi", "createdBy").
				field("lastModifiedBy.codi", "lastModifiedBy").
				byDefault().
				register();

		mapperFactory.classMap(NotificacioEnviamentAudit.class, NotificacioEnviamentAuditDto.class).
				field("createdBy.codi", "createdBy").
				field("lastModifiedBy.codi", "lastModifiedBy").
				byDefault().
				register();

		mapperFactory.classMap(ProcSerOrganEntity.class, ProcSerOrganDto.class).
//				field("procser", "procSer").
				byDefault().
				register();

		defineConverters();
	}

	private void defineConverters(){
		ConverterFactory converterFactory = mapperFactory.getConverterFactory();
		converterFactory.registerConverter(new StringToOrganGestorEstatEnum());
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
				entitatDto.setEntregaCieActiva(entitatEntity.getEntregaCie() != null);
				if (entitatEntity.getEntregaCie() != null) {
					entitatDto.setOperadorPostalId(entitatEntity.getEntregaCie().getOperadorPostalId());
					entitatDto.setCieId(entitatEntity.getEntregaCie().getCieId());
				}
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
	public class NotificacioEnviamentEntitytoInfoMapper extends CustomMapper<NotificacioEnviamentEntity, EnviamentInfoDto> {
		@Override
		public void mapAtoB(
				NotificacioEnviamentEntity notificacioEnviamentEntity,
				EnviamentInfoDto notificacioEnviamentDto,
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
	public static class StringToOrganGestorEstatEnum extends CustomConverter<String, OrganGestorEstatEnum> {

		@Override
		public OrganGestorEstatEnum convert(String source, Type<? extends OrganGestorEstatEnum> destinationType) {
			if (source == null){
				return OrganGestorEstatEnum.ALTRES;
			}

			source = source.toLowerCase(Locale.ROOT);
			if (source.equals("v") || source.equals("vigente")) {
				return OrganGestorEstatEnum.VIGENT;
			}

			return OrganGestorEstatEnum.ALTRES;
		}
	}
	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
