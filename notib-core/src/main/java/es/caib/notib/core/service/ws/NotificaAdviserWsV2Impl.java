/**
 * 
 */
package es.caib.notib.core.service.ws;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.Date;

import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.wsdl.adviser.Acuse;
import es.caib.notib.core.wsdl.adviser.AdviserWsV2PortType;
import es.caib.notib.core.wsdl.adviser.Opciones;
import es.caib.notib.core.wsdl.adviser.Receptor;

/**
 * Implementació del servei adviser de Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@WebService(
		name = "adviserWsV2",
		serviceName = "AdviserWsV2Service",
		portName = "AdviserWsV2PortType",
		endpointInterface = "es.caib.notib.core.wsdl.adviser.AdviserWsV2PortType",
		targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/")
public class NotificaAdviserWsV2Impl implements AdviserWsV2PortType {

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private NotificaHelper notificaHelper;


	@Override
	public void sincronizarEnvio(
			String organismoEmisor, 
			Holder<String> identificador, 
			BigInteger tipoEntrega,
			BigInteger modoNotificacion, 
			String estado, 
			XMLGregorianCalendar fechaEstado, 
			Receptor receptor,
			Acuse acusePDF, 
			Acuse acuseXML, 
			Opciones opcionesSincronizarEnvio, 
			Holder<String> codigoRespuesta,
			Holder<String> descripcionRespuesta, 
			Holder<Opciones> opcionesResultadoSincronizarEnvio) {
		NotificacioEnviamentEntity enviament = null;
		NotificacioEventEntity event = null;
		try {
			if (organismoEmisor != null) {
				EntitatEntity entitat = entitatRepository.findByDir3Codi(organismoEmisor);
				
				if (entitat != null) {
					enviament = notificacioEnviamentRepository.findByNotificacioEntitatAndNotificaIdentificador(
							entitat,
							identificador.value);
					//TODO: Revisar pq arriba la notificació sempre null
					if (enviament != null && enviament.getNotificacio() == null) {
						NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacioId());
						enviament.setNotificacio(notificacio);
					}
					if (enviament != null) {
						String receptorNombre = null;
						String receptorNif = null;
						if (receptor != null) {
							receptorNombre = receptor.getNombreReceptor();
							receptorNif = receptor.getNifReceptor();
						}
						NotificacioEnviamentEstatEnumDto notificaEstat = null;
						if ("pendiente_envio".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.PENDENT_ENVIAMENT;
						} else if ("enviado_ci".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ENVIADA_CI;
						} else if ("notificada".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.NOTIFICADA;
						} else if ("extraviada".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.EXTRAVIADA;
						} else if ("rehusada".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.REBUTJADA;
						} else if ("desconocido".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.DESCONEGUT;
						} else if ("fallecido".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.MORT;
						} else if ("ausente".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ABSENT;
						} else if ("direccion_incorrecta".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ADRESA_INCORRECTA;
						} else if ("sin_informacion".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.SENSE_INFORMACIO;
						} else if ("error".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ERROR_ENTREGA;
						} else if ("pendiente_sede".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.PENDENT_SEU;
						} else if ("enviado_deh".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ENVIADA_DEH;
						} else if ("leida".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.LLEGIDA;
						} else if ("envio_programado".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ENVIAMENT_PROGRAMAT;
						} else if ("pendiente_cie".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.PENDENT_CIE;
						} else if ("pendiente_deh".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.PENDENT_DEH;
						} else if ("entregado_op".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ENTREGADA_OP;
						} else if ("expirada".equals(estado)) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.EXPIRADA;
						}
						//Update enviament
						notificaHelper.enviamentUpdateDatat(
								notificaEstat,
								toDate(fechaEstado),
								estado,
								getModoNotificacion(modoNotificacion),
								receptorNif,
								receptorNombre,
								null,
								null,
								enviament);
						event = NotificacioEventEntity.getBuilder(
								NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
								enviament.getNotificacio()).
								enviament(enviament).
								descripcio(estado).
								callbackInicialitza().
								build();
						codigoRespuesta.value = "000";
						descripcionRespuesta.value = "OK";
					} else {
						logger.error(
								"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
								"identificadorDestinatario=" + identificador + "): " +
								"No s'ha trobat cap destinatari de notificació amb l'identificador especificat (" + identificador + ").");
						codigoRespuesta.value = "002";
						descripcionRespuesta.value = "Identificador no encontrado";
					}
				} else {
					logger.error(
							"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
							"identificadorDestinatario=" + identificador + "): " +
							"No s'ha trobat cap entitat amb el codi DIR3 especificat (" + organismoEmisor + ").");
					codigoRespuesta.value = "001";
					descripcionRespuesta.value = "Organismo Desconocido";
				}
			} else {
				logger.error(
						"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
						"identificadorDestinatario=" + identificador + "): " +
						"No s'ha trobat el camp amb l'organisme emissor a dins la petició rebuda.");
				codigoRespuesta.value = "001";
				descripcionRespuesta.value = "Organismo Desconocido";
			}
			//if datado + certificació
			if (tipoEntrega.equals(BigInteger.valueOf(2L))) {
				certificacionOrganismo(
						acusePDF,
						organismoEmisor,
						modoNotificacion,
						identificador,
						codigoRespuesta,
						descripcionRespuesta);
			}
		} catch (DatatypeConfigurationException ex) {
			codigoRespuesta.value = "004";
			descripcionRespuesta.value = "Fecha incorrecta";
		} catch (Exception ex) {
			logger.error(
					"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
					"identificadorDestinatario=" + identificador + ")",
					ex);
			if (enviament != null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
						enviament.getNotificacio()).
						enviament(enviament).
						descripcio(estado).
						error(true).
						errorDescripcio(ExceptionUtils.getStackTrace(ex)).
						build();
				enviament.updateNotificaError(
						true,
						event);
			}
			codigoRespuesta.value = "666";
			descripcionRespuesta.value = "Error procesando peticion";
		}
		if (enviament != null) {
			if (event == null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
						enviament.getNotificacio()).
						enviament(enviament).
						descripcio(estado).
						error(true).
						errorDescripcio("Error retornat cap a Notifica: [" + codigoRespuesta.value + "] " + descripcionRespuesta.value).
						build();
				enviament.updateNotificaError(
						true,
						event);
			}
			enviament.getNotificacio().updateEventAfegir(event);
		}
	}

	@Transactional
	private void certificacionOrganismo(
			Acuse acusePDF,
			String organismoEmisor,
			BigInteger modoNotificacion,
			Holder<String> identificador,
			Holder<String> codigoRespuesta,
			Holder<String> descripcionRespuesta) {
		NotificacioEnviamentEntity enviament = null;
		NotificacioEventEntity event = null;
		try {
			if (acusePDF != null) {
				EntitatEntity entitat = entitatRepository.findByDir3Codi(organismoEmisor);
				if (entitat != null) {
					enviament = notificacioEnviamentRepository.findByNotificacioEntitatAndNotificaIdentificador(
							entitat,
							identificador.value);
					//Problema hibernate
					if (enviament != null && enviament.getNotificacio() == null) {
						NotificacioEntity notificacio = notificacioRepository.findById(enviament.getNotificacioId());
						enviament.setNotificacio(notificacio);
					}
					if (enviament != null) {
						//si hi ha una certificació
						if (enviament.getNotificaCertificacioArxiuId() != null) {
							pluginHelper.gestioDocumentalDelete(
									enviament.getNotificaCertificacioArxiuId(),
									PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
						}
						//certificacionOrganismo.getHashSha1(); // Hash document certificacio
						String gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
								PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
								new ByteArrayInputStream(
										Base64.encode(
												acusePDF.getContenido())));
						enviament.updateNotificaCertificacio(
								new Date(),
								gestioDocumentalId,
								acusePDF.getHash(), // hash
								getModoNotificacion(modoNotificacion), // origen
								null, // metadades
								acusePDF.getCsvResguardo(), // csv
								null, // tipus mime
								null, // tamany
								NotificaCertificacioTipusEnumDto.ACUSE,
								NotificaCertificacioArxiuTipusEnumDto.PDF,
								null); // núm. seguiment
						event = NotificacioEventEntity.getBuilder(
								NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
								enviament.getNotificacio()).
								enviament(enviament).
								callbackInicialitza().
								build();
						codigoRespuesta.value = "000";
						descripcionRespuesta.value = "OK";
					} else {
						logger.error(
								"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
								"identificadorDestinatario=" + identificador + "): " +
								"No s'ha trobat cap destinatari de notificació amb l'identificador especificat (" + identificador + ").");
						codigoRespuesta.value = "002";
						descripcionRespuesta.value = "Identificador no encontrado";
					}
				} else {
					logger.error(
							"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
							"identificadorDestinatario=" + identificador + "): " +
							"No s'ha trobat cap entitat amb el codi DIR3 especificat (" + organismoEmisor + ").");
					codigoRespuesta.value = "001";
					descripcionRespuesta.value = "Organismo Desconocido";
				}
			} else {
				logger.error(
						"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
						"identificadorDestinatario=" + identificador + "): " +
						"No s'ha trobat el camp amb l'organisme emissor a dins la petició rebuda.");
				codigoRespuesta.value = "001";
				descripcionRespuesta.value = "Organismo Desconocido";
			}
		} catch (Exception ex) {
			logger.error(
					"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
					"identificadorDestinatario=" + identificador + ")",
					ex);
			if (enviament != null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
						enviament.getNotificacio()).
						enviament(enviament).
						error(true).
						errorDescripcio(ExceptionUtils.getStackTrace(ex)).
						build();
				enviament.updateNotificaError(
						true,
						event);
			}
			codigoRespuesta.value = "666";
			descripcionRespuesta.value = "Error procesando peticion";
		}
		if (enviament != null) {
			if (event == null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
						enviament.getNotificacio()).
						enviament(enviament).
						error(true).
						errorDescripcio("Error retornat cap a Notifica: [" + codigoRespuesta.value + "] " + descripcionRespuesta.value).
						build();
				enviament.updateNotificaError(
						true,
						event);
			}
			enviament.getNotificacio().updateEventAfegir(event);
		}
	}

	private String getModoNotificacion(BigInteger modo) {
		String modoNotificacion = null;
		switch (modo.intValue()) {
		case 1:
			modoNotificacion = "sede";
			break;
		case 2:
			modoNotificacion = "funcionario_habilitado";
			break;
		case 3:
			modoNotificacion = "postal";
			break;
		case 4:
			modoNotificacion = "electronico";
			break;
		case 5:
			modoNotificacion = "carpeta";
			break;
		}
		return modoNotificacion;
	}

	private Date toDate(XMLGregorianCalendar calendar) throws DatatypeConfigurationException {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificaAdviserWsV2Impl.class);

}
