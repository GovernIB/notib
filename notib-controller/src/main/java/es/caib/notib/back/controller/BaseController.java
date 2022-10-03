/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.exception.PluginException;
import es.caib.notib.back.helper.AjaxHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.ModalHelper;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controlador base que implementa funcionalitats comunes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseController implements MessageSourceAware {

	MessageSource messageSource;

	protected String modalUrlTancar() {
		return "redirect:" + ModalHelper.ACCIO_MODAL_TANCAR;
	}
	protected String ajaxUrlOk() {
		return "redirect:" + AjaxHelper.ACCIO_AJAX_OK;
	}

	protected String getAjaxControllerReturnValueSuccess(HttpServletRequest request, String url, String messageKey) {
		return getAjaxControllerReturnValueSuccess(request, url, messageKey, null);
	}

	protected String getAjaxControllerReturnValueSuccess(HttpServletRequest request, String url, String messageKey, Object[] messageArgs) {

		if (messageKey != null) {
			MissatgesHelper.success(request, getMessage(request, messageKey, messageArgs));
		}
		return AjaxHelper.isAjax(request) ? ajaxUrlOk() : url;
	}

	protected String getAjaxControllerReturnValueError(HttpServletRequest request, String url, String messageKey) {
		return getAjaxControllerReturnValueError(request, url, messageKey, null, null);
	}

	protected String getAjaxControllerReturnValueError(HttpServletRequest request, String url, String messageKey, Throwable error) {
		return getAjaxControllerReturnValueError(request, url, messageKey, null, error);
	}

	protected String getAjaxControllerReturnValueError(HttpServletRequest request, String url, String messageKey, Object[] messageArgs) {
		return getAjaxControllerReturnValueError(request, url, messageKey, messageArgs, null);
	}

	protected String getAjaxControllerReturnValueError(HttpServletRequest request, String url, String messageKey, Object[] messageArgs, Throwable error) {

		String errorMsg = "";
		if (error != null) {
			errorMsg = "... " + error.getMessage();
		}
		if (messageKey != null) {
			MissatgesHelper.error(request, getMessage(request, messageKey, messageArgs) + errorMsg);
		}
		return AjaxHelper.isAjax(request) ? ajaxUrlOk() : url;
	}

	protected String getAjaxControllerReturnValueErrorMessage(HttpServletRequest request, String url, String message) {

		if (message != null) {
			MissatgesHelper.error(request, message);
		}
		return AjaxHelper.isAjax(request) ? ajaxUrlOk() : url;
	}

	protected String getModalControllerReturnValueSuccess(HttpServletRequest request, String url, String messageKey) {
		return getModalControllerReturnValueSuccess(request, url, messageKey, null);
	}

	protected String getModalControllerReturnValueSuccess(HttpServletRequest request, String url, String messageKey, Object[] messageArgs) {

		if (messageKey != null) {
			MissatgesHelper.success(request, getMessage(request, messageKey, messageArgs));
		}
		return ModalHelper.isModal(request) ? modalUrlTancar() : url;
	}

	protected String getModalControllerReturnValueError(HttpServletRequest request, String url, String messageKey) {
		return getModalControllerReturnValueError(request, url, messageKey, null);
	}

	protected String getModalControllerReturnValueError(HttpServletRequest request, String url, String messageKey, Object[] messageArgs) {

		if (messageKey != null) {
			MissatgesHelper.error(request, getMessage(request, messageKey, messageArgs));
		}
		return ModalHelper.isModal(request) ? modalUrlTancar() : url;
	}

	protected String getModalControllerReturnValueErrorMessageText(HttpServletRequest request, String url, String message) {

		if (message != null) {
			MissatgesHelper.error(request, message);
		}
		return ModalHelper.isModal(request) ? modalUrlTancar() : url;
	}

	protected String getModalControllerReturnValueErrorWithDescription(HttpServletRequest request, String url, String messageKey, String description) {

		MissatgesHelper.error(request, getMessage(request, messageKey) + " - " + description);
		return ModalHelper.isModal(request) ? modalUrlTancar() : url;
	}
	
	protected void writeFileToResponse(String fileName, byte[] fileContent, HttpServletResponse response) throws IOException {

		if (fileContent == null) {
			throw new PluginException("No s'ha pogut descarregar el fitxer");
		}
		response.setHeader("Pragma", "");
		response.setHeader("Expires", "");
		response.setHeader("Cache-Control", "");
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
		if (fileName != null && !fileName.isEmpty()) {
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		}
		response.getOutputStream().write(fileContent);
	}

	protected String getMessage(HttpServletRequest request, String key, Object[] args) {
		return messageSource.getMessage(key, args, "???" + key + "???", new RequestContext(request).getLocale());
	}

	protected String getMessage(HttpServletRequest request, String key) {
		return getMessage(request, key, null);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
