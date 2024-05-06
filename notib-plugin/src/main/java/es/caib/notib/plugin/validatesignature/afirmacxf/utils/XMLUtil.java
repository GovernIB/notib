package es.caib.notib.plugin.validatesignature.afirmacxf.utils;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class XMLUtil {

    static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();

    public static boolean isXml(byte[] data) throws ParserConfigurationException, IOException {
        if (data == null || data.length < 7 || data[0] != '<') {
            return false;
        }

        try {
            DocumentBuilder builder = DBF.newDocumentBuilder();
            builder.parse(new ByteArrayInputStream(data));
            return true;
        } catch (SAXException e) {
            return false;
        }
    }
}