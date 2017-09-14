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
import es.caib.notib.core.api.dto.NotificacioDestinatariEstatEnumDto;
import es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEventEntity;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioDestinatariRepository;
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
		name = "AdviserWS",
		serviceName = "AdviserWSService",
		portName = "AdviserWSServicePort",
		endpointInterface = "es.caib.notib.core.wsdl.adviser.AdviserWS",
		targetNamespace = "https://administracionelectronica.gob.es/notifica/ws/notifica/1.0/")
public class NotificaAdviserWsImpl implements AdviserWS {

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private NotificacioDestinatariRepository notificacioDestinatariRepository;

	@Autowired
	private PluginHelper pluginHelper;



	@Override
	@Transactional
	public void datadoOrganismo(
			DatadoRequest datadoOrganismo,
			Holder<String> codigoRespuesta,
			Holder<String> textoRespuesta) {
		NotificacioDestinatariEntity notificacioDestinatari = null;
		NotificacioEventEntity event = null;
		try {
			if (datadoOrganismo.getOrganismoEmisor().getCodigoDir3() != null) {
				EntitatEntity entitat = entitatRepository.findByDir3Codi(
						datadoOrganismo.getOrganismoEmisor().getCodigoDir3());
				if (entitat != null) {
					notificacioDestinatari = notificacioDestinatariRepository.findByNotificacioEntitatAndNotificaIdentificador(
							entitat,
							datadoOrganismo.getIdentificadorDestinatario());
					if (notificacioDestinatari != null) {
						String receptorNombre = null;
						String receptorNif = null;
						if (datadoOrganismo.getReceptor() != null) {
							receptorNombre = datadoOrganismo.getReceptor().getNombre();
							receptorNif = datadoOrganismo.getReceptor().getNif();
						}
						NotificacioDestinatariEstatEnumDto notificaEstat = null;
						if ("pendiente_envio".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.PENDENT_ENVIAMENT;
						} else if ("enviado_ci".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.ENVIADA_CI;
						} else if ("notificada".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.NOTIFICADA;
						} else if ("extraviada".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.EXTRAVIADA;
						} else if ("rehusada".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.REBUTJADA;
						} else if ("desconocido".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.DESCONEGUT;
						} else if ("fallecido".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.MORT;
						} else if ("ausente".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.ABSENT;
						} else if ("direccion_incorrecta".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.ADRESA_INCORRECTA;
						} else if ("sin_informacion".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.SENSE_INFORMACIO;
						} else if ("error".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.ERROR_ENTREGA;
						} else if ("pendiente_sede".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.PENDENT_SEU;
						} else if ("enviado_deh".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.ENVIADA_DEH;
						} else if ("leida".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.LLEGIDA;
						} else if ("envio_programado".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.ENVIAMENT_PROGRAMAT;
						} else if ("pendiente_cie".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.PENDENT_CIE;
						} else if ("pendiente_deh".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.PENDENT_DEH;
						} else if ("entregado_op".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.ENTREGADA_OP;
						} else if ("expirada".equals(datadoOrganismo.getResultado())) {
							notificaEstat = NotificacioDestinatariEstatEnumDto.EXPIRADA;
						}
						notificacioDestinatari.updateNotificaEstat(
								notificaEstat,
								toDate(datadoOrganismo.getFecha()),
								receptorNombre,
								receptorNif,
								datadoOrganismo.getModo(),
								null);
						event = NotificacioEventEntity.getBuilder(
								NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
								notificacioDestinatari.getNotificacio()).
								notificacioDestinatari(notificacioDestinatari).
								descripcio(datadoOrganismo.getResultado()).
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
			if (notificacioDestinatari != null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
						notificacioDestinatari.getNotificacio()).
						notificacioDestinatari(notificacioDestinatari).
						descripcio(datadoOrganismo.getResultado()).
						error(true).
						errorDescripcio(ExceptionUtils.getStackTrace(ex)).
						build();
				notificacioDestinatari.updateNotificaError(
						true,
						event);
			}
			codigoRespuesta.value = "666";
			textoRespuesta.value = "Error procesando peticion";
		}
		if (notificacioDestinatari != null) {
			if (event == null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT,
						notificacioDestinatari.getNotificacio()).
						notificacioDestinatari(notificacioDestinatari).
						descripcio(datadoOrganismo.getResultado()).
						error(true).
						errorDescripcio("Error retornat cap a Notifica: [" + codigoRespuesta.value + "] " + textoRespuesta.value).
						build();
				notificacioDestinatari.updateNotificaError(
						true,
						event);
			}
			notificacioDestinatari.getNotificacio().updateEventAfegir(event);
		}
	}

	@Override
	@Transactional
	public void certificacionOrganismo(
			CertificadoRequest certificacionOrganismo,
			Holder<String> codigoRespuesta,
			Holder<String> textoRespuesta) {
		NotificacioDestinatariEntity notificacioDestinatari = null;
		NotificacioEventEntity event = null;
		try {
			if (certificacionOrganismo.getOrganismoEmisor() != null) {
				EntitatEntity entitat = entitatRepository.findByDir3Codi(
						certificacionOrganismo.getOrganismoEmisor());
				if (entitat != null) {
					notificacioDestinatari = notificacioDestinatariRepository.findByNotificacioEntitatAndNotificaIdentificador(
							entitat,
							certificacionOrganismo.getIdentificadorDestinatario());
					if (notificacioDestinatari != null) {
						//certificacionOrganismo.getHashSha1(); // Hash document certificacio
						String gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
								PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
								new ByteArrayInputStream(
										Base64.decode(
												certificacionOrganismo.getCertificacion().getBytes())));
						notificacioDestinatari.updateNotificaCertificacio(
								NotificaCertificacioTipusEnumDto.toEnum(certificacionOrganismo.getAcuseOSobre()),
								NotificaCertificacioArxiuTipusEnumDto.PDF,
								gestioDocumentalId,
								null,
								new Date());
						event = NotificacioEventEntity.getBuilder(
								NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
								notificacioDestinatari.getNotificacio()).
								notificacioDestinatari(notificacioDestinatari).
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
			if (notificacioDestinatari != null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
						notificacioDestinatari.getNotificacio()).
						notificacioDestinatari(notificacioDestinatari).
						error(true).
						errorDescripcio(ExceptionUtils.getStackTrace(ex)).
						build();
				notificacioDestinatari.updateNotificaError(
						true,
						event);
			}
			codigoRespuesta.value = "666";
			textoRespuesta.value = "Error procesando peticion";
		}
		if (notificacioDestinatari != null) {
			if (event == null) {
				event = NotificacioEventEntity.getBuilder(
						NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO,
						notificacioDestinatari.getNotificacio()).
						notificacioDestinatari(notificacioDestinatari).
						error(true).
						errorDescripcio("Error retornat cap a Notifica: [" + codigoRespuesta.value + "] " + textoRespuesta.value).
						build();
				notificacioDestinatari.updateNotificaError(
						true,
						event);
			}
			notificacioDestinatari.getNotificacio().updateEventAfegir(event);
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
