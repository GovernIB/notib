package es.caib.notib.plugin.arxiu;

import es.caib.plugins.arxiu.api.ArxiuException;

@SuppressWarnings("serial")
public class ArxiuCaibException extends ArxiuException {

	private String metode;
	private int httpStatus;
	private String arxiuCodi;
	private String arxiuDescripcio;

	public ArxiuCaibException(
			String metode,
			String message) {
		super(message);
		this.metode = metode;
	}

	public ArxiuCaibException(
			String metode,
			String message,
			Throwable cause) {
		super(message, cause);
		this.metode = metode;
	}

	public ArxiuCaibException(
			String metode,
			int httpStatus,
			String arxiuCodi,
			String arxiuDescripcio) {
		super("[HTTP_" + httpStatus + ", " + arxiuCodi + "] " + arxiuDescripcio);
		this.metode = metode;
		this.arxiuCodi = arxiuCodi;
		this.arxiuDescripcio = arxiuDescripcio;
	}

	public String getMetode() {
		return metode;
	}
	public int getHttpStatus() {
		return httpStatus;
	}
	public String getArxiuCodi() {
		return arxiuCodi;
	}
	public String getArxiuDescripcio() {
		return arxiuDescripcio;
	}

}