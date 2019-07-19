package es.caib.notib.core.api.util;

public class UtilitatsNotib {
	
	/**
	 * Indica si l'excepció o la seva causa son del tipus indicat per paràmetre
	 * @param e Excepció a comprovar
	 * @param exceptionClass Class a comparar
	 * @return
	 */
	public static boolean isExceptionOrCauseInstanceOf(Exception e, Class<? extends Exception> exceptionClass) {
		boolean isException = exceptionClass.isInstance(e);
		 
		if (!isException && e.getCause() != null) {
			Throwable t = e.getCause();
			isException = exceptionClass.isInstance(t);
			if (!isException && t.getCause() != null) { // && t.getClass().getName().equals("javax.trasaction.RollbackException")) {
				isException = exceptionClass.isInstance(t.getCause());
			}
		}
		
		return isException;
//		return exceptionClass.isInstance(e) || e.getCause() != null && exceptionClass.isInstance(e.getCause());
	}
	
	public static String getMessageExceptionOrCauseInstanceOf(Exception e, Class<? extends Exception> exceptionClass) {
		
		String msgKey = null;
		
		if (exceptionClass.isInstance(e)) {
			return e.getMessage();
		}else{		 
			if (e.getCause() != null) {
				Throwable t = e.getCause();
				if (exceptionClass.isInstance(t)) {
					return t.getMessage();
				}else{
					if (t.getCause() != null) { // && t.getClass().getName().equals("javax.trasaction.RollbackException")) {
						if (exceptionClass.isInstance(t.getCause())) {
							return t.getCause().getMessage();
						}
					}
				}
			}
		}
		
		return msgKey;
//		return exceptionClass.isInstance(e) || e.getCause() != null && exceptionClass.isInstance(e.getCause());
	}
	
	public static Throwable getExceptionOrCauseInstanceOf(Exception e, Class<? extends Exception> exceptionClass) {
		
		if (exceptionClass.isInstance(e)) {
			return e;
		}else{		 	
			if (e.getCause() != null) {
				Throwable t = e.getCause();
				if (exceptionClass.isInstance(t)) {
					return t;
				}else{
					if (t.getCause() != null) { // && t.getClass().getName().equals("javax.trasaction.RollbackException")) {
						if (exceptionClass.isInstance(t.getCause())) {
							return t.getCause();
						}
					}
				}
			}
		}
		
		return null;
	}
}