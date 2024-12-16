package es.caib.notib.logic.wsdl.notificaV2.ampliarPlazoOE;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarAdapter extends XmlAdapter<String, XMLGregorianCalendar> {

    @Override
    public XMLGregorianCalendar unmarshal(String v) throws Exception {
        if (v == null || v.trim().isEmpty()) {
            return null;
        }
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(v);
    }

    @Override
    public String marshal(XMLGregorianCalendar v) throws Exception {
        if (v == null) {
            return null;
        }
        return v.toXMLFormat();
    }
}

