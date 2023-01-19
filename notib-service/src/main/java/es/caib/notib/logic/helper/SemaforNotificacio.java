package es.caib.notib.logic.helper;

import lombok.Synchronized;

import java.util.HashMap;
import java.util.Map;

public class SemaforNotificacio {

    public static Object creacioSemafor = new Object();

    public static Map<Long, Integer> semafors = new HashMap<>();

    public static Object getCreacioSemafor() {

        return creacioSemafor;
    }

    @Synchronized
    public static Long agafar(Long notificacioId) {

        semafors.put(notificacioId, !semafors.containsKey(notificacioId) ? 1 : semafors.get(notificacioId)+1);
        return notificacioId;
    }

    @Synchronized
    public static void alliberar(Long notificacioId) {

        Integer n = semafors.get(notificacioId);
        if (n-1 == 0) {
            semafors.remove(notificacioId);
            return;
        }
        semafors.put(notificacioId, n-1);
    }

    @Synchronized
    public static boolean isSemaforInUse(Long notificacioId) {

        return semafors.get(notificacioId) != null && semafors.get(notificacioId) > 0;
    }

    //	private boolean isSemaforInUse() {
//		boolean inUse = true;
//		synchronized(SemaforNotificacio.creacioSemafor()) {
//			inUse = false;
//		}
//		return inUse;
//	}
}
