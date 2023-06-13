/**
 * 
 */
package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.EntregaPostal;
import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.IdentificadorTextDto;
import es.caib.notib.logic.intf.dto.NotificacioAuditDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentAuditDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.NotificacioErrorTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.TipusDocumentDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatFullaDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.logic.intf.dto.cie.CieTableItemDto;
import es.caib.notib.logic.intf.dto.cie.EntregaPostalDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalTableItemDto;
import es.caib.notib.logic.intf.dto.notenviament.EnviamentInfoDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentTableItemDto;
import es.caib.notib.logic.intf.dto.notenviament.NotificacioEnviamentDatatableDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDataDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaTableItemDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.organisme.UnitatOrganitzativaDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerOrganDto;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.entity.CallbackEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.EnviamentTableEntity;
import es.caib.notib.persist.entity.GrupEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import es.caib.notib.persist.entity.NotificacioTableEntity;
import es.caib.notib.persist.entity.OficinaEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.ServeiEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.persist.entity.auditoria.NotificacioAudit;
import es.caib.notib.persist.entity.auditoria.NotificacioEnviamentAudit;
import es.caib.notib.persist.entity.cie.EntregaPostalEntity;
import es.caib.notib.persist.entity.cie.PagadorCieEntity;
import es.caib.notib.persist.entity.cie.PagadorCieFormatFullaEntity;
import es.caib.notib.persist.entity.cie.PagadorCieFormatSobreEntity;
import es.caib.notib.persist.entity.cie.PagadorPostalEntity;
import es.caib.notib.persist.repository.CallbackRepository;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.unitat.ObjetoDirectorio;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
@Component
public class ConversioTipusHelper {

	private MapperFactory mapperFactory;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private CallbackRepository callbackRepository;

	public ConversioTipusHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(DateTime source, Type<? extends Date> destinationClass, MappingContext context) {
						return source.toDate();
					}
				});

		mapperFactory.classMap(EntitatEntity.class, EntitatDto.class).byDefault().customize(new EntitatEntitytoMapper()).register();

		mapperFactory.classMap(NotificacioEntity.class, NotificacioInfoDto.class).
				field("organGestor.codi", "organGestorCodi").
				field("organGestor.nom", "organGestorNom")
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(NotificacioEntity a, NotificacioInfoDto b, MappingContext context) {
						DadesUsuari d = cacheHelper.findUsuariAmbCodi(a.getUsuariCodi());
						if (d != null) {
							b.setUsuariNom(d.getNomSencer());
						}
					}
				}).byDefault().register();

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
				field("domiciliPoblacio", "poblacio").
				field("domiciliMunicipiCodiIne", "municipiCodi").
				field("domiciliProvinciaCodi", "provincia").
				field("domiciliPaisCodiIso", "paisCodi").
				field("domiciliLinea1", "linea1").
				field("domiciliLinea2", "linea2").
				field("formatSobre", "formatSobre").
				field("formatFulla", "formatFulla").
				field("domiciliCie", "cie").byDefault().register();

		mapperFactory.classMap(NotificacioEntity.class, NotificacioDtoV2.class).
				field("organGestor.codi", "organGestor").
				field("organGestor.nom", "organGestorNom").byDefault().register();

		mapperFactory.classMap(NotificacioEntity.class, NotificacioDatabaseDto.class).
				field("organGestor.codi", "organGestorCodi").byDefault().register();

		mapperFactory.classMap(NotificacioTableEntity.class, NotificacioTableItemDto.class).byDefault()
				.customize(new CustomMapper<>() {
							@Override
							public void mapAtoB(NotificacioTableEntity entity, NotificacioTableItemDto dto, MappingContext context) {
								entity.getCreatedBy().ifPresent(usuari -> {
									dto.setCreatedByNom(usuari.getNom());
									dto.setCreatedByCodi(usuari.getCodi());
								});
							}
				}).register();

		mapperFactory.classMap(NotificacioMassivaEntity.class, NotificacioMassivaTableItemDto.class).byDefault()
				.customize(new CustomMapper<>() {
							@Override
							public void mapAtoB(NotificacioMassivaEntity entity, NotificacioMassivaTableItemDto dto, MappingContext context) {
								entity.getCreatedBy().ifPresent(usuari -> {
									dto.setCreatedByNom(usuari.getNom());
									dto.setCreatedByCodi(usuari.getCodi());
								});
							}
				}).register();

		mapperFactory.classMap(NotificacioMassivaEntity.class, NotificacioMassivaDataDto.class).byDefault().
				customize(new CustomMapper<>() {
							@Override
							public void mapAtoB(NotificacioMassivaEntity entity, NotificacioMassivaDataDto dto, MappingContext context) {
								entity.getCreatedBy().ifPresent(usuari -> {
									var usr = new UsuariDto();
									usr.setNom(usuari.getNomSencer());
									usr.setCodi(usuari.getCodi());
									dto.setCreatedBy(usr);
								});
							}
				}).register();

		mapperFactory.classMap(EnviamentTableEntity.class, NotEnviamentTableItemDto.class).
				field("notificaReferencia", "codiNotibEnviament").
				field("notificacio.referencia", "referenciaNotificacio").
				field("notificacio.id", "notificacioId").
				field("csv_uuid", "csvUuid").
				customize(new EnviamentTableItemMapper()).byDefault().register();

		mapperFactory.classMap(NotificacioEnviamentEntity.class, NotificacioEnviamentDto.class).
				customize(new NotificacioEnviamentEntitytoMapper()).byDefault().register();

		mapperFactory.classMap(NotificacioEnviamentEntity.class, NotificacioEnviamentDatatableDto.class).
				field("notificacio.estat", "notificacioEstat").
				customize(new NotificacioEnviamentEntitytoDatatableMapper()).byDefault().register();

		mapperFactory.classMap(NotificacioEnviamentEntity.class, EnviamentInfoDto.class).
				field("notificacio.estat", "notificacioEstat").
				customize(new NotificacioEnviamentEntitytoInfoMapper()).byDefault().register();

		mapperFactory.classMap(EntregaPostalDto.class, EntregaPostal.class).
				field("domiciliConcretTipus", "tipus").byDefault().register();

		mapperFactory.classMap(AplicacioEntity.class, AplicacioDto.class).
				field("entitat.id", "entitatId").
				byDefault().
				register();

		mapperFactory.classMap(UsuariEntity.class, UsuariDto.class).
				customize(new UsuariEntitytoMapper()).byDefault().register();

		mapperFactory.classMap(OrganGestorEntity.class, OrganGestorDto.class).
				field("entitat.id", "entitatId").
				field("entitat.nom", "entitatNom").
				field("entitat.oficina", "oficinaNom").
				field("oficina", "oficina.codi").
				field("oficinaNom", "oficina.nom")
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(OrganGestorEntity a, OrganGestorDto b, MappingContext context) {
						// add your custom mapping code here
						b.setEntregaCieActiva(a.getEntregaCie() != null);
						if (a.getEntregaCie() != null) {
							b.setOperadorPostalId(a.getEntregaCie().getOperadorPostalId());
							b.setCieId(a.getEntregaCie().getCieId());
						}
					}
				}).byDefault().register();

		mapperFactory.classMap(ProcedimentEntity.class, ProcSerDto.class).
				field("organGestor.codi", "organGestor")
				.field("organGestor.nom", "organGestorNom")
				.customize(new CustomMapper<>() {
					@Override
							public void mapAtoB(ProcedimentEntity a, ProcSerDto b, MappingContext context) {
						// add your custom mapping code here
						b.setEntregaCieActiva(a.getEntregaCie() != null);
						if (a.getEntregaCie() != null) {
							b.setOperadorPostalId(a.getEntregaCie().getOperadorPostalId());
							b.setCieId(a.getEntregaCie().getCieId());
						}
					}
				}).byDefault().register();

		mapperFactory.classMap(ServeiEntity.class, ProcSerDto.class).
				field("organGestor.codi", "organGestor")
				.field("organGestor.nom", "organGestorNom")
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(ServeiEntity a, ProcSerDto b, MappingContext context) {
						// add your custom mapping code here
						b.setEntregaCieActiva(a.getEntregaCie() != null);
						if (a.getEntregaCie() != null) {
							b.setOperadorPostalId(a.getEntregaCie().getOperadorPostalId());
							b.setCieId(a.getEntregaCie().getCieId());
						}
					}
				}).byDefault().register();

		mapperFactory.classMap(GrupEntity.class, GrupDto.class)
				.field("entitat.id", "entitatId")
				.field("organGestor.id", "organGestorId")
				.field("organGestor.codi", "organGestorCodi")
				.byDefault().register();

		mapperFactory.classMap(PagadorCieEntity.class, CieDto.class)
				.field("entitat.id", "entitatId")
				.field("organGestor.id", "organGestorId")
				.field("organGestor.codi", "organismePagadorCodi")
				.byDefault().register();

		mapperFactory.classMap(PagadorCieEntity.class, CieTableItemDto.class)
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(PagadorCieEntity a, CieTableItemDto b, MappingContext context) {
						b.setOrganismePagador(a.getOrganGestor() != null ? a.getOrganGestor().getCodi() + " - " + a.getOrganGestor().getNom() : "ORGAN GESTOR NO TROBAT");
					}
				}).byDefault().register();

		mapperFactory.classMap(PagadorPostalEntity.class, OperadorPostalDto.class)
				.field("entitat.id", "entitatId")
				.field("organGestor.codi", "organismePagadorCodi")
				.byDefault().register();

		mapperFactory.classMap(PagadorPostalEntity.class, OperadorPostalTableItemDto.class)
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(PagadorPostalEntity a, OperadorPostalTableItemDto b, MappingContext context) {
						b.setOrganismePagador(a.getOrganGestor() != null ? a.getOrganGestor().getCodi() + " - " + a.getOrganGestor().getNom() : "ORGAN GESTOR NO TROBAT");
					}
				}).byDefault().register();

		mapperFactory.classMap(PagadorCieFormatFullaEntity.class, CieFormatFullaDto.class).
				field("pagadorCie.id", "pagadorCieId").byDefault().register();

		mapperFactory.classMap(PagadorCieFormatSobreEntity.class, CieFormatSobreDto.class).
				field("pagadorCie.id", "pagadorCieId").byDefault().register();

		mapperFactory.classMap(PagadorPostalEntity.class, IdentificadorTextDto.class)
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(PagadorPostalEntity a, IdentificadorTextDto b, MappingContext context) {
						b.setText(a.getNom() + " - " + a.getContracteNum());
					}
				}).byDefault().register();

		mapperFactory.classMap(PagadorCieEntity.class, IdentificadorTextDto.class)
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(PagadorCieEntity a, IdentificadorTextDto b, MappingContext context) {
						// add your custom mapping code here
						b.setText(a.getNom() + " (Fins el " + a.getContracteDataVig() + ")");
					}
				}).byDefault().register();

		mapperFactory.classMap(NodeDir3.class, OrganGestorDto.class).
				field("denominacio", "nomEs").
				field("denominacionCooficial", "nom").
				field("tieneOficinaSir", "sir").byDefault().register();

		mapperFactory.classMap(OrganismeDto.class, OrganGestorDto.class).
				field("pare", "codiPare").byDefault().register();

		mapperFactory.classMap(ObjetoDirectorio.class, OrganGestorDto.class).
				field("denominacio", "nom").byDefault().register();

		mapperFactory.classMap(CodiValor.class, CodiValorDto.class).
				field("id", "codi").
				field("descripcio", "valor").byDefault().register();

		mapperFactory.classMap(NotificacioEnviamentEntity.class, NotificacioEnviamentDtoV2.class).
				field("notificacio.id", "notificacioId").
				customize(new NotificacioEnviamentEntitytoDtoV2Mapper()).byDefault().register();

		mapperFactory.classMap(NotificacioAudit.class, NotificacioAuditDto.class).byDefault()
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(NotificacioAudit entity, NotificacioAuditDto dto, MappingContext context) {
						entity.getCreatedBy().ifPresent(usuari -> dto.setCreatedBy(usuari.getCodi()));
						entity.getLastModifiedBy().ifPresent(usuari -> dto.setLastModifiedBy(usuari.getCodi()));
				}}).register();

		mapperFactory.classMap(NotificacioEnviamentAudit.class, NotificacioEnviamentAuditDto.class).byDefault()
				.customize(new CustomMapper<>() {
						@Override
						public void mapAtoB(NotificacioEnviamentAudit entity, NotificacioEnviamentAuditDto dto, MappingContext context) {
							entity.getCreatedBy().ifPresent(usuari -> dto.setCreatedBy(usuari.getCodi()));
							entity.getLastModifiedBy().ifPresent(usuari -> dto.setLastModifiedBy(usuari.getCodi()));
						}
					}).register();

		mapperFactory.classMap(ProcSerOrganEntity.class, ProcSerOrganDto.class).byDefault().register();

		mapperFactory.classMap(OrganGestorEntity.class, UnitatOrganitzativaDto.class)
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(OrganGestorEntity a, UnitatOrganitzativaDto b, MappingContext context) {
						// add your custom mapping code here
						b.setDenominacio(a.getNomEs());
						b.setDenominacioCooficial(a.getNom());
					}
				}).byDefault().register();

		mapperFactory.classMap(NodeDir3.class, UnitatOrganitzativaDto.class)
				.customize(new CustomMapper<>() {
					@Override
					public void mapAtoB(NodeDir3 a, UnitatOrganitzativaDto b, MappingContext context) {
						// add your custom mapping code here
						b.setDenominacio(a.getDenominacio());
						b.setDenominacioCooficial(a.getDenominacionCooficial());
					}
				}).byDefault().register();

		mapperFactory.classMap(OficinaEntity.class, OficinaDto.class).
				field("organGestor.codi", "organCodi").byDefault().register();

		mapperFactory.classMap(NotificacioEventEntity.class, NotificacioEventDto.class).
				field("enviament.id", "enviamentId").byDefault().register();

		defineConverters();
	}

	private void defineConverters() {

		var converterFactory = mapperFactory.getConverterFactory();
		converterFactory.registerConverter(new StringToOrganGestorEstatEnum());
	}

	public <T> T convertir(Object source, Class<T> targetType) {
		return source != null ? getMapperFacade().map(source, targetType) : null;
	}

	public <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		return items != null ? getMapperFacade().mapAsList(items, targetType) : null;
	}
	public <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		return items != null ? getMapperFacade().mapAsSet(items, targetType) : null;
	}

	public class UsuariEntitytoMapper extends CustomMapper<UsuariEntity, UsuariDto> {
		@Override
		public void mapAtoB(UsuariEntity usuariEntity, UsuariDto usuariDto, MappingContext context) {

			if (usuariEntity.getNomSencer() != null && !usuariEntity.getNomSencer().isEmpty() && usuariEntity.getNomSencer().trim().length() > 0) {
				usuariDto.setNom(usuariEntity.getNomSencer());
				return;
			}
			if (usuariEntity.getLlinatges() != null && !usuariEntity.getLlinatges().isEmpty() && usuariEntity.getLlinatges().trim().length() > 0) {
				usuariDto.setNom(usuariEntity.getNom() + " " + usuariEntity.getLlinatges());
			}
		}
	}

	public class EntitatEntitytoMapper extends CustomMapper<EntitatEntity, EntitatDto> {
		@Override
		public void mapAtoB(EntitatEntity entitatEntity, EntitatDto entitatDto, MappingContext context) {

			if (entitatEntity.getTipusDocDefault() != null) {
				var tipusDocumentDto = new TipusDocumentDto();
				tipusDocumentDto.setEntitat(entitatEntity.getId());
				tipusDocumentDto.setTipusDocEnum(entitatEntity.getTipusDocDefault());
				entitatDto.setTipusDocDefault(tipusDocumentDto);
			}
			entitatDto.setEntregaCieActiva(entitatEntity.getEntregaCie() != null);
			if (entitatEntity.getEntregaCie() != null) {
				entitatDto.setOperadorPostalId(entitatEntity.getEntregaCie().getOperadorPostalId());
				entitatDto.setCieId(entitatEntity.getEntregaCie().getCieId());
			}
		}
	}

	public class NotificacioEnviamentEntitytoMapper extends CustomMapper<NotificacioEnviamentEntity, NotificacioEnviamentDto> {
		@Override
		public void mapAtoB(NotificacioEnviamentEntity notificacioEnviamentEntity, NotificacioEnviamentDto notificacioEnviamentDto, MappingContext context) {

			if (!notificacioEnviamentEntity.isNotificaError()) {
				return;
			}
			try {
				NotificacioEventEntity event = notificacioEnviamentEntity.getNotificacioErrorEvent();
				if (event == null) {
					return;
				}
				notificacioEnviamentDto.setNotificaErrorData(event.getData());
				notificacioEnviamentDto.setNotificaErrorDescripcio(event.getErrorDescripcio());
				if (NotificacioEventTipusEnumDto.REGISTRE_ENVIAMENT.equals(event.getTipus()) || NotificacioEventTipusEnumDto.SIR_ENVIAMENT.equals(event.getTipus())) {
					notificacioEnviamentDto.getNotificacio().setNotificaErrorTipus(NotificacioErrorTipusEnumDto.ERROR_REGISTRE);
				}
			} catch (Exception ex) {
				log.error("[ConversioTipusHelper.NotificacioEnviamentDto] event no trobat.");
			}
		}
	}

	public class NotificacioEnviamentEntitytoDatatableMapper extends CustomMapper<NotificacioEnviamentEntity, NotificacioEnviamentDatatableDto> {
		@Override
		public void mapAtoB(NotificacioEnviamentEntity entity, NotificacioEnviamentDatatableDto dto, MappingContext context) {

			dto.setEstatColor(entity.getNotificaEstat().getColor());
			dto.setEstatIcona(entity.getNotificaEstat().getIcona());
			var event = entity.getNotificacioErrorEvent();
			if (event != null) {
				dto.setNotificacioErrorData(event.getData());
				dto.setNotificacioErrorDescripcio(event.getErrorDescripcio());
				if (Boolean.TRUE.equals(event.getFiReintents())) {
					var msg = messageHelper.getMessage("notificacio.event.fi.reintents");
					var tipus = messageHelper.getMessage("es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto." + event.getTipus());
					dto.setFiReintents(event.getFiReintents());
					dto.setFiReintentsDesc(msg + " -> " + tipus);
				}
			}
			var callback = callbackRepository.findByEnviamentIdAndEstat(dto.getId(), CallbackEstatEnumDto.ERROR);
			if (callback == null) {
				return;
			}
			dto.setCallbackFiReintents(true);
			dto.setCallbackFiReintentsDesc(messageHelper.getMessage("callback.fi.reintents"));
		}
	}

	public class NotificacioEnviamentEntitytoInfoMapper extends CustomMapper<NotificacioEnviamentEntity, EnviamentInfoDto> {
		@Override
		public void mapAtoB(NotificacioEnviamentEntity notificacioEnviamentEntity, EnviamentInfoDto notificacioEnviamentDto, MappingContext context) {

			if (notificacioEnviamentEntity.isNotificaError()) {
				var event = notificacioEnviamentEntity.getNotificacioErrorEvent();
				if (event != null) {
					notificacioEnviamentDto.setNotificacioErrorData(event.getData());
					notificacioEnviamentDto.setNotificacioErrorDescripcio(event.getErrorDescripcio());
				}
			}
			var count = callbackRepository.findByEnviamentIdAndEstat(notificacioEnviamentEntity.getId(), CallbackEstatEnumDto.ERROR);
			if (count != null) {
				notificacioEnviamentDto.setCallbackFiReintents(true);
				notificacioEnviamentDto.setCallbackFiReintentsDesc(messageHelper.getMessage("callback.fi.reintents"));
			}
		}
	}
	public class NotificacioEnviamentEntitytoDtoV2Mapper extends CustomMapper<NotificacioEnviamentEntity, NotificacioEnviamentDtoV2> {
		@Override
		public void mapAtoB(NotificacioEnviamentEntity notificacioEnviamentEntity, NotificacioEnviamentDtoV2 notificacioEnviamentDto, MappingContext context) {

			var errorEvent = notificacioEnviamentEntity.getNotificacioErrorEvent();
			var notificacio = notificacioEnviamentEntity.getNotificacio();
			var enviant = errorEvent == null && notificacio.getRegistreEnviamentIntent() == 0 && notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT);
			notificacioEnviamentDto.setEnviant(enviant);
		}
	}

	public class EnviamentTableItemMapper extends CustomMapper<EnviamentTableEntity, NotEnviamentTableItemDto> {
		@Override
		public void mapAtoB(EnviamentTableEntity enviamentTableEntity, NotEnviamentTableItemDto notEnviamentTableItemDto, MappingContext context) {

			if (enviamentTableEntity.getDestinataris() == null || enviamentTableEntity.getDestinataris().isEmpty()) {
				return;
			}
			var destinataris = enviamentTableEntity.getDestinataris().split("<br>");
			if (destinataris.length == 0 || destinataris[0].isEmpty() || !destinataris[0].contains(" - ")) {
				return;
			}
			StringBuilder destinatarisFormat = new StringBuilder();
			for(String destinatari: destinataris) {
				destinatarisFormat.append(getNomLlinatgeNif(destinatari)).append("<br>");
			}
			if (destinatarisFormat.length() > 4) {
				destinatarisFormat = new StringBuilder(destinatarisFormat.substring(0, destinatarisFormat.length() - 4));
			}
			enviamentTableEntity.setDestinataris(destinatarisFormat.toString());
			notEnviamentTableItemDto.setDestinataris(destinatarisFormat.toString());
		}

		private String getNomLlinatgeNif(String destinatari) {

			int idxSeparador = destinatari.indexOf(" - ");
			if (idxSeparador == -1) {
				return destinatari;
			}
			String nif = null;
			if (idxSeparador > 0) {
				nif = destinatari.substring(0, idxSeparador);
			}
			if (destinatari.length() < idxSeparador + 4) {
				return nif;
			}
			var llinatgeNom = destinatari.substring(idxSeparador + 3, destinatari.length() - 1);
			var nomLlinatge = llinatgeNom;
			if (llinatgeNom.contains(", ")) {
				idxSeparador = llinatgeNom.indexOf(", ");
				if (idxSeparador != -1) {
					nomLlinatge = idxSeparador == 0 ? llinatgeNom.substring(2) :
							llinatgeNom.substring(idxSeparador + 2) + " " + llinatgeNom.substring(0, idxSeparador);
				}
			}
			return nomLlinatge + (nif != null ? " (" + nif + ")" : "");
		}
	}

	public static class StringToOrganGestorEstatEnum extends CustomConverter<String, OrganGestorEstatEnum> {

		@Override
		public OrganGestorEstatEnum convert(String source, Type<? extends OrganGestorEstatEnum> destinationType, MappingContext context) {

			if (source == null){
				return OrganGestorEstatEnum.E;
			}
			source = source.substring(0, 1).toUpperCase(Locale.ROOT);
			switch (source) {
				case "V": return OrganGestorEstatEnum.V;
				case "T": return OrganGestorEstatEnum.T;
				case "A": return OrganGestorEstatEnum.A;
				default: return OrganGestorEstatEnum.E;
			}
		}
	}

	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
