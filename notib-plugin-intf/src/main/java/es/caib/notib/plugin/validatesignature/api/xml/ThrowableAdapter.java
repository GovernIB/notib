package es.caib.notib.plugin.validatesignature.api.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ThrowableAdapter extends XmlAdapter<String, Throwable> {
    private HexBinaryAdapter hexAdapter = new HexBinaryAdapter();

    @Override
    public String marshal(Throwable v) throws Exception {

        try (var baos = new ByteArrayOutputStream(); var oos = new ObjectOutputStream(baos);) {
            oos.writeObject(v);
            oos.close();
            var serializedBytes = baos.toByteArray();
            return hexAdapter.marshal(serializedBytes);
        }
    }

    @Override
    public Throwable unmarshal(String v) throws Exception {

        var serializedBytes = hexAdapter.unmarshal(v);
        try (var bais = new ByteArrayInputStream(serializedBytes); var ois = new ObjectInputStream(bais);) {
            Throwable result = (Throwable) ois.readObject();
            return result;
        }
    }
}