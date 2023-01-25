package es.caib.notib.war.validation;

import es.caib.notib.core.api.exception.SistemaExternException;

public class RestPreconditions {

	private RestPreconditions() {
		throw new AssertionError();
	}

	// API

	/**
	 * Ensures that an object reference passed as a parameter to the calling method is not null.
	 *
	 * @param reference
	 *            an object reference
	 *
	 * @return the non-null reference that was validated
	 *
	 * @throws MyResourceNotFoundException
	 *             if {@code reference} is null
	 */
	public static <T> T checkNotNull(final T reference) {
		return checkNotNull(reference, null);
	}

	/**
	 * Ensures that an object reference passed as a parameter to the calling method is not null.
	 *
	 * @param reference
	 *            an object reference
	 * @param message
	 *            the message of the exception if the check fails
	 *
	 * @return the non-null reference that was validated
	 *
	 * @throws MyResourceNotFoundException
	 *             if {@code reference} is null
	 */
	public static <T> T checkNotNull(final T reference, final String message) {
		if (reference == null) {
			throw new SistemaExternException("Notificacio REST", message);
		}
		return reference;
	}

}