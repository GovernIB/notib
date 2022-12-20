package es.caib.notib.core.api.service;

import es.caib.notib.core.api.dto.EnviamentAdviser;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;

public interface AdviserService {

    void sincronitzarEnviament(EnviamentAdviser env);
}
