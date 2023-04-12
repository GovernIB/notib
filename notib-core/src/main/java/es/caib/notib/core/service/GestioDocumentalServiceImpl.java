/**
 * 
 */
package es.caib.notib.core.service;

import es.caib.notib.core.api.service.GestioDocumentalService;
import es.caib.notib.core.helper.PluginHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;

/**
 * Implementació del servei de gestió de notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class GestioDocumentalServiceImpl implements GestioDocumentalService {

	@Autowired
	private PluginHelper pluginHelper;

	@Override
	@Transactional(rollbackFor=Exception.class)
	public String guardarArxiuTemporal(String contigut) {
		String documentGesdocId = null;
		try {
			if(contigut != null) {
				documentGesdocId = pluginHelper.gestioDocumentalCreate(
						PluginHelper.GESDOC_AGRUPACIO_TEMPORALS,
						Base64.decodeBase64(contigut));
			}
		} catch (Exception ex) {
			log.error(
					"Error al guardar l'arxiu temporal " + ex);
		} 
		return documentGesdocId;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public byte[] obtenirArxiuTemporal(String arxiuGestdocId) {
		return consultaArxiuGestioDocumental(arxiuGestdocId, PluginHelper.GESDOC_AGRUPACIO_TEMPORALS);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public byte[] obtenirArxiuNotificacio(String arxiuGestdocId) {
		return consultaArxiuGestioDocumental(arxiuGestdocId, PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS);
	}

	private byte[] consultaArxiuGestioDocumental(String arxiuGestdocId, String agrupacio) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			if(arxiuGestdocId != null) {
				pluginHelper.gestioDocumentalGet(
						arxiuGestdocId,
						agrupacio,
						output);
			}
		} catch (Exception ex) {
			log.error("Error al recuperar l'arxiu de l'agrupació: " + agrupacio);
			throw ex;
		}
		return output.toByteArray();
	}

}