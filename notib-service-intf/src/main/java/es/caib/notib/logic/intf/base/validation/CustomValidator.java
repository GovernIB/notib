package es.caib.notib.logic.intf.base.validation;

/**
 * Interfície per a validacions personalitzades.
 *
 * @param <T> la classe sobre la que s'executa la validació.
 *
 * @author Límit Tecnologies
 */
public interface CustomValidator<T> {

	boolean validate(T value);

	default String getMessage() {
		return "{" + this.getClass().getName() + "}";
	}

}
