package es.caib.notib.plugin.validatesignature.afirmacxf;

/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */

import es.gob.afirma.i18n.Language;
import es.gob.afirma.transformers.TransformersException;
import es.gob.afirma.transformers.xmlTransformers.IXmlTransformer;
import java.util.Properties;
import org.apache.log4j.Logger;

public final class XmlTransformersFactory {

    private static final Logger logger = Logger.getLogger(XmlTransformersFactory.class);

    private XmlTransformersFactory() {
    }

    public static Class<?> getXmlTransformer(String serviceReq, String method, String type, String version) throws TransformersException {
        Class<?> res;
        try {
            if (serviceReq == null || method == null || version == null) {
                throw new TransformersException(Language.getFormatResIntegra("XTF001",
                        new Object[]{serviceReq, method, version}));
            }
            String transformerClass = XmlTransformersFactory.getTransformerClassName(serviceReq, method, type, version);
            if (transformerClass == null) {
                throw new TransformersException(Language.getFormatResIntegra("XTF002",
                        new Object[]{serviceReq, method, version}));
            }
            res = Class.forName(transformerClass);
            Class<?>[] interfaces = res.getInterfaces();
            boolean found = false;
            for (int i = 0; i < interfaces.length && !found; ++i) {
                Class<?> c = interfaces[i];
                if (!c.getName().equals(IXmlTransformer.class.getName())) continue;
                found = true;
            }
            if (!found) {
                throw new TransformersException(Language.getFormatResIntegra("XTF003",
                        new Object[]{transformerClass, IXmlTransformer.class.getName()}));
            }
            logger.debug(Language.getFormatResIntegra("XTF004", new Object[]{transformerClass}));
        }
        catch (ClassNotFoundException e) {
            logger.error(e);
            throw new TransformersException(e.getMessage(), e);
        }
        return res;
    }

    private static String getTransformerClassName(String serviceReq, String method, String type, String version) {
        Properties properties = new Properties();
        if (type.equals("request")) {
            properties = TransformersProperties.getMethodRequestTransformersProperties(serviceReq, method, version);
        } else if (type.equals("response")) {
            properties = TransformersProperties.getMethodResponseTransformersProperties(serviceReq, method, version);
        }
        StringBuilder transfClassName = new StringBuilder(serviceReq).append(".");
        transfClassName.append(method).append(".");
        transfClassName.append(version).append(".");
        transfClassName.append(type).append(".");
        transfClassName.append("transformerClass");

        if (logger.isDebugEnabled()) {
            logger.debug(" transformerClass [PROPERTY] => " + transfClassName.toString());
        }
        String res = properties.getProperty(transfClassName.toString());
        if (logger.isDebugEnabled()) {
            logger.debug(" transformerClass [PROPERTY VALUE] => " + res);
        }
        return res;
    }
}

