package es.caib.notib.back.helper;

public class ExceptionHelper {

	/**
	 * Defineix el nombre d'excepcions anidades que vols comprovar
	 */
	private static final int N_CHECKED_NESTED_EXCEPTIONS = 3;
	
	public static boolean isExceptionOrCauseInstanceOf(Exception e, Class<? extends Exception> exceptionClass) {
		return isExceptionOrCauseInstanceOf(e, exceptionClass, N_CHECKED_NESTED_EXCEPTIONS);
	}
	
	public static boolean isExceptionOrCauseInstanceOf(Exception e, Class<? extends Exception> exceptionClass, int nCheckedNestedExceptions) {

		var i = 0;
		var isTheException = exceptionClass.isInstance(e);
		Throwable t = e;
		while (i < nCheckedNestedExceptions && !isTheException && t.getCause() != null) {
			t = t.getCause();
			isTheException = exceptionClass.isInstance(t);
			i++;
		}
		return isTheException;
	}

	public static Throwable findThrowableInstance(Exception e, Class<? extends Exception> exceptionClass) {
		return findThrowableInstance(e, exceptionClass, N_CHECKED_NESTED_EXCEPTIONS);
	}

	public static Throwable findThrowableInstance(Exception e, Class<? extends Exception> exceptionClass, int nCheckedNestedExceptions) {

		var i = 0;
		Throwable exception = null;
		var isTheException = exceptionClass.isInstance(e);
		if (isTheException) {
			exception = e;
		}
		Throwable t = e;
		while (i < nCheckedNestedExceptions && !isTheException && t.getCause() != null) {
			t = t.getCause();
			isTheException = exceptionClass.isInstance(t);
			i++;
			if (isTheException) {
				exception = t;
			}
		}
		return exception;
	}
	
}
