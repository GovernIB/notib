package es.caib.notib.core.api.dto;

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
	
}
