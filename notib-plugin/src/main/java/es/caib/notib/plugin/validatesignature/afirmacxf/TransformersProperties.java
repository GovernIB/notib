/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package es.caib.notib.plugin.validatesignature.afirmacxf;

import es.gob.afirma.i18n.Language;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Logger;

public final class TransformersProperties {
    private static Logger logger = Logger.getLogger(TransformersProperties.class);
    //private static long propsFileLastUpdate = -1;
    private static Properties properties = null;

    private TransformersProperties() {
    }

    public static Properties getTransformersProperties() {
        //TransformersProperties.init();
        return properties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static synchronized void init(Properties prop) {
      /*  
      FileInputStream in = null;
        try {
            File file = TransformersProperties.getPropertiesResource();
            if (propsFileLastUpdate != file.lastModified()) {
                logger.debug((Object)Language.getResIntegra("TP001"));
                properties = new Properties();
                in = new FileInputStream(file);
                properties.load(in);
                propsFileLastUpdate = file.lastModified();
                logger.debug((Object)Language.getFormatResIntegra("TP002", new Object[]{properties}));
                logger.debug((Object)Language.getFormatResIntegra("TP003", new Object[]{new Date(propsFileLastUpdate)}));
            }
        }
        catch (URISyntaxException e) {
            String errorMsg = Language.getFormatResIntegra("TP004", new Object[]{"transformers.properties"});
            logger.error((Object)errorMsg, (Throwable)e);
            properties = new Properties();
        }
        catch (IOException e) {
            String errorMsg = Language.getFormatResIntegra("TP004", new Object[]{"transformers.properties"});
            logger.error((Object)errorMsg, (Throwable)e);
            properties = new Properties();
        }
        finally {
            UtilsResources.safeCloseInputStream(in);
        }
        */
      properties = prop;
      
    }

    private static File getPropertiesResource() throws URISyntaxException {
        URL url = TransformersProperties.class.getClassLoader().getResource("transformers.properties");
        if (url == null) {
            throw new URISyntaxException("Error", Language.getFormatResIntegra("TP005", new Object[]{"transformers.properties"}));
        }
        URI uri = new URI(url.toString());
        File res = new File(uri);
        return res;
    }

    public static Properties getMethodRequestTransformersProperties(String serviceName, String method, String version) {
        logger.debug((Object)Language.getFormatResIntegra("TP006", new Object[]{serviceName}));
        Properties res = TransformersProperties.getMethodTransformersProperties(serviceName, method, version, "request");
        return res;
    }

    public static Properties getMethodResponseTransformersProperties(String serviceName, String method, String version) {
        logger.debug((Object)Language.getFormatResIntegra("TP007", new Object[]{serviceName}));
        Properties res = TransformersProperties.getMethodTransformersProperties(serviceName, method, version, "response");
        return res;
    }

    public static Properties getMethodParseTransformersProperties(String serviceName, String method, String version) {
        logger.debug((Object)Language.getFormatResIntegra("TP008", new Object[]{serviceName}));
        Properties res = TransformersProperties.getMethodTransformersProperties(serviceName, method, version, "parser");
        return res;
    }

    public static Properties getMethodTransformersProperties(String serviceName, String method, String version) {
        logger.debug((Object)Language.getFormatResIntegra("TP008", new Object[]{serviceName}));
        Properties res = TransformersProperties.getMethodTransformersProperties(serviceName, method, version, null);
        return res;
    }

    private static Properties getMethodTransformersProperties(String serviceName, String method, String version, String type) {
        Properties res = new Properties();
        String header = serviceName + "." + method + "." + version + "." + (type == null ? "" : type);
        Enumeration enumeration = TransformersProperties.getTransformersProperties().propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            if (!key.startsWith(header)) continue;
            res.put(key, properties.getProperty(key));
        }
        logger.debug((Object)Language.getFormatResIntegra("TP002", new Object[]{res}));
        return res;
    }

    
}

