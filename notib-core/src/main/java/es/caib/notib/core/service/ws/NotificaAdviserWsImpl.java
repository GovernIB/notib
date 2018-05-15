/**
 * 
 */
package es.caib.notib.core.service.ws;

import java.io.ByteArrayInputStream;
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
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.wsdl.adviser.AdviserWS;
import es.caib.notib.core.wsdl.adviser.CertificadoRequest;
import es.caib.notib.core.wsdl.adviser.DatadoRequest;

/**
 * Implementació del servei adviser de Notifica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@WebService(
		name = "adviserWS",
		serviceName = "AdviserWSService",
		portName = "AdviserWSServicePort",
		endpointInterface = "es.caib.notib.core.wsdl.adviser.AdviserWS",
		targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/")
public class NotificaAdviserWsImpl implements AdviserWS {

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioEnviamentRepository;

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private NotificaHelper notificaHelper;



	@Override
	@Transactional
	public void datadoOrganismo(
			DatadoRequest datadoOrganismo,
			Holder<String> codigoRespuesta,
			Holder<String> textoRespuesta) {
		NotificacioEnviamentEntity enviament = null;
		NotificacioEventEntity event = null;
		try {
			if (datadoOrganismo.getOrganismoEmisor().getCodigoDir3() != null) {
				EntitatEntity entitat = entitatRepository.findByDir3Codi(
						datadoOrganismo.getOrganismoEmisor().getCodigoDir3());
				if (entitat != null) {
					enviament = notificacioEnviamentRepository.findByNotificacioEntitatAndNotificaIdentificador(
							entitat,
							datadoOrganismo.getIdentificadorDestinatario());
					if (enviament != null) {
						String receptorNombre = null;
						String receptorNif = null;
						if (datadoOrganismo.getReceptor() != null) {
							receptorNombre = datadoOrganismo.getReceptor().getNombre();
							receptorNif = datadoOrganismo.getReceptor().getNif();
						}
						NotificacioEnviamentEstatEnumDto notificaEstat = null;
						if ("pendiente_envio".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.PENDENT_ENVIAMENT;
						} else if ("enviado_ci".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ENVIADA_CI;
						} else if ("notificada".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.NOTIFICADA;
						} else if ("extraviada".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.EXTRAVIADA;
						} else if ("rehusada".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.REBUTJADA;
						} else if ("desconocido".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.DESCONEGUT;
						} else if ("fallecido".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.MORT;
						} else if ("ausente".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ABSENT;
						} else if ("direccion_incorrecta".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ADRESA_INCORRECTA;
						} else if ("sin_informacion".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.SENSE_INFORMACIO;
						} else if ("error".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ERROR_ENTREGA;
						} else if ("pendiente_sede".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.PENDENT_SEU;
						} else if ("enviado_deh".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ENVIADA_DEH;
						} else if ("leida".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.LLEGIDA;
						} else if ("envio_programado".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ENVIAMENT_PROGRAMAT;
						} else if ("pendiente_cie".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.PENDENT_CIE;
						} else if ("pendiente_deh".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.PENDENT_DEH;
						} else if ("entregado_op".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.ENTREGADA_OP;
						} else if ("expirada".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioEnviamentEstatEnumDto.EXPIRADA;
						}
						notificaHelper.enviamentUpdateDatat(
								notificaEstat,
								toDate(datadoOrganismo.getFecha()),
								null,
								datadoOrganismo.getModo(),
								receptorNif,
								receptorNombre,
								null,
								null,
								enviament);
						event = NotificacioEventEntity.getBuilder(
								NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
								enviament.getNotificacio()).
								enviament(enviament).
								descripcio(datadoOrganismo.getResultado()).
								callbackInicialitza().
								build();
						codigoRespuesta.value = "000";
						textoRespuesta.value = "OK";
					} else {
						logger.error(
								"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
								"identificadorDestinatario=" + datadoOrganismo.getIdentificadorDestinatario() + "): " +
								"No s'ha trobat cap destinatari de notificació amb l'identificador especificat (" + datadoOrganismo.getIdentificadorDestinatario() + ").");
						codigoRespuesta.value = "002";
						textoRespuesta.value = "Identificador no encontrado";
					}
				} else {
					logger.error(
							"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
							"identificadorDestinatario=" + datadoOrganismo.getIdentificadorDestinatario() + "): " +
							"No s'ha trobat cap entitat amb el codi DIR3 especificat (" + datadoOrganismo.getOrganismoEmisor().getCodigoDir3() + ").");
					codigoRespuesta.value = "001";
					textoRespuesta.value = "Organismo Desconocido";
				}
			} else {
				logger.error(
						"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
						"identificadorDestinatario=" + datadoOrganismo.getIdentificadorDestinatario() + "): " +
						"No s'ha trobat el camp amb l'organisme emissor a dins la petició rebuda.");
				codigoRespuesta.value = "001";
				textoRespuesta.value = "Organismo Desconocido";
			}
		} catch (DatatypeConfigurationException ex) {
			codigoRespuesta.value = "004";
			textoRespuesta.value = "Fecha incorrecta";
		} catch (Exception ex) {
			logger.error(
					"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
					"identificadorDestinatario=" + datadoOrganismo.getIdentificadorDestinatario() + ")",
					ex);
			if (enviament != null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
						enviament.getNotificacio()).
						enviament(enviament).
						descripcio(datadoOrganismo.getResultado()).
						error(true).
						errorDescripcio(ExceptionUtils.getStackTrace(ex)).
						build();
				enviament.updateNotificaError(
						true,
						event);
			}
			codigoRespuesta.value = "666";
			textoRespuesta.value = "Error procesando peticion";
		}
		if (enviament != null) {
			if (event == null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
						enviament.getNotificacio()).
						enviament(enviament).
						descripcio(datadoOrganismo.getResultado()).
						error(true).
						errorDescripcio("Error retornat cap a Notifica: [" + codigoRespuesta.value + "] " + textoRespuesta.value).
						build();
				enviament.updateNotificaError(
						true,
						event);
			}
			enviament.getNotificacio().updateEventAfegir(event);
		}
	}

	@Override
	@Transactional
	public void certificacionOrganismo(
			CertificadoRequest certificacionOrganismo,
			Holder<String> codigoRespuesta,
			Holder<String> textoRespuesta) {
		NotificacioEnviamentEntity enviament = null;
		NotificacioEventEntity event = null;
		try {
			if (certificacionOrganismo.getOrganismoEmisor() != null) {
				EntitatEntity entitat = entitatRepository.findByDir3Codi(
						certificacionOrganismo.getOrganismoEmisor());
				if (entitat != null) {
					enviament = notificacioEnviamentRepository.findByNotificacioEntitatAndNotificaIdentificador(
							entitat,
							certificacionOrganismo.getIdentificadorDestinatario());
					if (enviament != null) {
						//certificacionOrganismo.getHashSha1(); // Hash document certificacio
						String gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
								PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
								new ByteArrayInputStream(
										Base64.decode(
												certificacionOrganismo.getCertificacion().getBytes())));
						enviament.updateNotificaCertificacio(
								new Date(),
								gestioDocumentalId,
								certificacionOrganismo.getHashSha1(), // hash
								null, // origen
								null, // metadades
								null, // csv
								null, // tipus mime
								null, // tamany
								NotificaCertificacioTipusEnumDto.toEnum(certificacionOrganismo.getAcuseOSobre()),
								NotificaCertificacioArxiuTipusEnumDto.PDF,
								null); // núm. seguiment
						event = NotificacioEventEntity.getBuilder(
								NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
								enviament.getNotificacio()).
								enviament(enviament).
								callbackInicialitza().
								build();
						codigoRespuesta.value = "000";
						textoRespuesta.value = "OK";
					} else {
						logger.error(
								"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
								"identificadorDestinatario=" + certificacionOrganismo.getIdentificadorDestinatario() + "): " +
								"No s'ha trobat cap destinatari de notificació amb l'identificador especificat (" + certificacionOrganismo.getIdentificadorDestinatario() + ").");
						codigoRespuesta.value = "002";
						textoRespuesta.value = "Identificador no encontrado";
					}
				} else {
					logger.error(
							"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
							"identificadorDestinatario=" + certificacionOrganismo.getIdentificadorDestinatario() + "): " +
							"No s'ha trobat cap entitat amb el codi DIR3 especificat (" + certificacionOrganismo.getOrganismoEmisor() + ").");
					codigoRespuesta.value = "001";
					textoRespuesta.value = "Organismo Desconocido";
				}
			} else {
				logger.error(
						"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
						"identificadorDestinatario=" + certificacionOrganismo.getIdentificadorDestinatario() + "): " +
						"No s'ha trobat el camp amb l'organisme emissor a dins la petició rebuda.");
				codigoRespuesta.value = "001";
				textoRespuesta.value = "Organismo Desconocido";
			}
		} catch (Exception ex) {
			logger.error(
					"Error al processar petició datadoOrganismo dins el callback de Notifica (" +
					"identificadorDestinatario=" + certificacionOrganismo.getIdentificadorDestinatario() + ")",
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
			textoRespuesta.value = "Error procesando peticion";
		}
		if (enviament != null) {
			if (event == null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
						enviament.getNotificacio()).
						enviament(enviament).
						error(true).
						errorDescripcio("Error retornat cap a Notifica: [" + codigoRespuesta.value + "] " + textoRespuesta.value).
						build();
				enviament.updateNotificaError(
						true,
						event);
			}
			enviament.getNotificacio().updateEventAfegir(event);
		}
	}



	private Date toDate(XMLGregorianCalendar calendar) throws DatatypeConfigurationException {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificaAdviserWsImpl.class);

}
