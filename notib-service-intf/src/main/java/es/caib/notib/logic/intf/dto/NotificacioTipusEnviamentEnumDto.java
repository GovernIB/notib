package es.caib.notib.logic.intf.dto;

import es.caib.notib.client.domini.EnviamentTipus;

import java.io.Serializable;

public enum NotificacioTipusEnviamentEnumDto implements Serializable {
	
	buit(""),
	notificacio("Notificació"),
    comunicacio("Comunicació");
	
	private final String displayName;
	
	NotificacioTipusEnviamentEnumDto(final String display)
    {
        this.displayName = display;
    }
	
	@Override public String toString()
    {
        return this.displayName;
    }
	
	public static int getNumVal(NotificacioTipusEnviamentEnumDto tipus) {

		if(tipus.toString().toUpperCase().equals("NOTIFICACIÓ")) {
			return 0;
		}else if(tipus.toString().toUpperCase().equals("COMUNICACIÓ")) {
			return 1;
		}else {
			return 0;
		}
	}
	
	public static int getNumVal(EnviamentTipus tipus) {

		var t = tipus.toString().toUpperCase();
		if(t.equals("NOTIFICACIO")) {
			return 0;
		}
		if(t.equals("COMUNICACIO")) {
			return 1;
		}
		return 0;
	}
	
	
}
