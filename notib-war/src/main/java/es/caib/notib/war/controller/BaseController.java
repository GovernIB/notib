/**
 * 
 */
package es.caib.notib.war.controller;

import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.notib.core.api.exception.PluginException;
import es.caib.notib.war.helper.AjaxHelper;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.ModalHelper;

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

	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getAjaxControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getAjaxControllerReturnValueError(
				request,
				url,
				messageKey,
				null,
				null);
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Throwable error) {
		return getAjaxControllerReturnValueError(
				request,
				url,
				messageKey,
				null,
				error);
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		return getAjaxControllerReturnValueError(
				request,
				url,
				messageKey,
				messageArgs,
				null);
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs,
			Throwable error) {
		String errorMsg = "";
		if (error != null) {
			errorMsg = "... " + error.getMessage();
		}
		if (messageKey != null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs) + errorMsg);
		}
		
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueError(
				request,
				url,
				messageKey,
				null);
	}
	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueErrorWithDescription(
			HttpServletRequest request,
			String url,
			String messageKey,
			String description) {
		MissatgesHelper.error(
				request, 
				getMessage(request, messageKey) + " - " + description);
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}
	
	protected void writeFileToResponse(
			String fileName,
			byte[] fileContent,
			HttpServletResponse response) throws IOException {
		if (fileContent == null) {
			throw new PluginException("No s'ha pogut descarregar el fitxer");
		}
		response.setHeader("Pragma", "");
		response.setHeader("Expires", "");
		response.setHeader("Cache-Control", "");
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
		if (fileName != null && !fileName.isEmpty())
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		response.getOutputStream().write(fileContent);
	}

	protected String getMessage(
			HttpServletRequest request,
			String key,
			Object[] args) {
		String message = messageSource.getMessage(
				key,
				args,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}
	protected String getMessage(
			HttpServletRequest request,
			String key) {
		return getMessage(request, key, null);
	}



	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
