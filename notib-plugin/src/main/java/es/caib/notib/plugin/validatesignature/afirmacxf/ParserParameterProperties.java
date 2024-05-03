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
import java.util.Properties;
import org.apache.log4j.Logger;

public final class ParserParameterProperties {
    private static final Logger LOGGER = Logger.getLogger(ParserParameterProperties.class);
    private static long propsFileLastUpdate = -1;
    private static Properties properties = new Properties();

    private ParserParameterProperties() {
    }

    public static Properties getParserParametersProperties() {
        //ParserParameterProperties.init();
        return properties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static synchronized void init(Properties prop) {
      /*  
      FileInputStream fis = null;
        try {
            File file = ParserParameterProperties.getPropertiesResource();
            if (propsFileLastUpdate != file.lastModified()) {
                LOGGER.debug((Object)Language.getResIntegra("PPP001"));
                properties = new Properties();
                fis = new FileInputStream(file);
                properties.load(fis);
                propsFileLastUpdate = file.lastModified();
                LOGGER.debug((Object)Language.getFormatResIntegra("PPP002", new Object[]{properties}));
                LOGGER.debug((Object)Language.getFormatResIntegra("PPP003", new Object[]{new Date(propsFileLastUpdate)}));
            }
        }
        catch (URISyntaxException e) {
            LOGGER.error((Object)Language.getFormatResIntegra("PPP004", new Object[]{"parserParameters.properties"}), (Throwable)e);
            properties = new Properties();
        }
        catch (IOException e) {
            LOGGER.error((Object)Language.getFormatResIntegra("PPP004", new Object[]{"parserParameters.properties"}), (Throwable)e);
            properties = new Properties();
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException e) {}
            }
        }
        */
      properties = prop;
    }

    private static File getPropertiesResource() throws URISyntaxException {
        URL url = ParserParameterProperties.class.getClassLoader().getResource("parserParameters.properties");
        if (url == null) {
            throw new URISyntaxException("Error", Language.getFormatResIntegra("PPP005", new Object[]{"parserParameters.properties"}));
        }
        URI uri = new URI(url.toString());
        File res = new File(uri);
        return res;
    }

    
}

