package es.caib.notib.logic.intf.exception;

@SuppressWarnings("serial")
public class PluginException extends RuntimeException {

	public PluginException() {
		super();
	}

	public PluginException(Throwable cause) {
		super(cause);
	}

	public PluginException(String message) {
		super(message);
	}

	public PluginException(String message, Throwable cause) {
		super(message, cause);
	}

}
