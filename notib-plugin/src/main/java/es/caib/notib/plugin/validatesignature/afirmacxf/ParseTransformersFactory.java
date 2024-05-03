/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package es.caib.notib.plugin.validatesignature.afirmacxf;

import es.gob.afirma.i18n.Language;
import es.gob.afirma.transformers.TransformersException;
import es.gob.afirma.transformers.parseTransformers.IParseTransformer;
import es.gob.afirma.utils.GenericUtils;
import org.apache.log4j.Logger;

import java.util.Properties;

public final class ParseTransformersFactory {
    private static Logger logger = Logger.getLogger(ParseTransformersFactory.class);

    private ParseTransformersFactory() {
    }

    public static Class<Object> getParseTransformer(String serviceReq, String method, String version) throws TransformersException {
        Class<Object> res = null;
        try {
            if (!GenericUtils.assertStringValue(serviceReq) || !GenericUtils.assertStringValue(method)) {
                throw new TransformersException(Language.getFormatResIntegra("PTF001", new Object[]{serviceReq, version}));
            }
            String transformerClass = ParseTransformersFactory.getTransformerClassName(serviceReq, method, version);
            if (transformerClass == null) {
                throw new TransformersException(Language.getFormatResIntegra("PTF002", new Object[]{serviceReq, method, version}));
            }
            res = (Class<Object>) Class.forName(transformerClass);
            Class<?>[] interfaces = res.getInterfaces();
            boolean found = false;
            for (int i = 0; i < interfaces.length && !found; ++i) {
                Class c = interfaces[i];
                if (!c.getName().equals(IParseTransformer.class.getName())) continue;
                found = true;
            }
            if (!found) {
                res = null;
                throw new TransformersException(Language.getFormatResIntegra("PTF003", new Object[]{transformerClass, IParseTransformer.class.getName()}));
            }
            logger.debug((Object)Language.getFormatResIntegra("PTF004", new Object[]{transformerClass}));
        }
        catch (ClassNotFoundException e) {
            logger.error((Object)e);
            throw new TransformersException(e.getMessage(), e);
        }
        return res;
    }

    private static String getTransformerClassName(String serviceReq, String method, String version) {
        Properties properties = TransformersProperties.getMethodParseTransformersProperties(serviceReq, method, version);
        
        //logger.error("ParseTransformersFactory::getTransformerClassName [PROPERTY] => " + serviceReq + "." + method + "." + version + "." + "parser" + "." + "transformerClass");
        
        String res = properties.getProperty(serviceReq + "." + method + "." + version + "." + "parser" + "." + "transformerClass");
        
        //logger.error(" ParseTransformersFactory::getTransformerClassName [PROPERTY VALUE] => " + res);
        return res;
    }
}

