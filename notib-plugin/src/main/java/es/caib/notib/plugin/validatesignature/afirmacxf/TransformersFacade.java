package es.caib.notib.plugin.validatesignature.afirmacxf;

/*
 * Decompiled with CFR 0_118.
 *
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */

import es.gob.afirma.i18n.Language;
import es.gob.afirma.transformers.TransformersException;
import es.gob.afirma.utils.GenericUtils;
import es.gob.afirma.utils.UtilsXML;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

public final class TransformersFacade {
    private static final Logger LOGGER = Logger.getLogger(TransformersFacade.class);

    private static TransformersFacade instance;
    private final Properties transformersProperties;
    private final Properties parserParamsProp;
    private static final int PARAM_NUMBERS = 4;
    
    
    public static TransformersFacade init(Properties transformersProperties, Properties parserParamsProp) {
      instance = new TransformersFacade(transformersProperties, parserParamsProp);
      TransformersProperties.init(transformersProperties);
      ParserParameterProperties.init(parserParamsProp);
      return instance;
    }


    public static TransformersFacade getInstance() {
        return instance;
    }

    private TransformersFacade(Properties transformersProperties, Properties parserParamsProp) {
      this.transformersProperties = transformersProperties;
      this.parserParamsProp = parserParamsProp;
    }

    public Document getXmlRequestFileByRequestType(String serviceReq, String method, String type, String version) throws TransformersException {
        Document res = null;
        if (!(GenericUtils.assertStringValue(serviceReq) && GenericUtils.assertStringValue(type) && GenericUtils.assertStringValue(version) && GenericUtils.assertStringValue(method))) {
            String errorMsg = Language.getResIntegra("TF003");
            LOGGER.error((Object)errorMsg);
            throw new TransformersException(errorMsg);
        }
        LOGGER.debug((Object)Language.getFormatResIntegra("TF004", new Object[]{serviceReq, method, type, version}));
        try {
            StringBuffer templateName = new StringBuffer(serviceReq).append(".");
            templateName.append(method).append(".");
            templateName.append(version).append(".");
            templateName.append(type).append(".");
            templateName.append("template");
            String fileName = this.transformersProperties.getProperty(templateName.toString());
            LOGGER.debug((Object)Language.getFormatResIntegra("TF005", new Object[]{fileName}));
            String xmlTemplateFolder = this.transformersProperties.getProperty("TransformersTemplatesPath") + "/xmlTemplates";
            LOGGER.debug((Object)Language.getFormatResIntegra("TF007", new Object[]{xmlTemplateFolder}));
            LOGGER.debug((Object)Language.getResIntegra("TF008"));
            File xmlFile = new File(xmlTemplateFolder, fileName);
            res = UtilsXML.parseDocument(new FileReader(xmlFile));
            LOGGER.debug((Object)Language.getFormatResIntegra("TF006", new Object[]{res.getDocumentElement().getTagName()}));
        }
        catch (IOException e) {
            String errorMsg = Language.getResIntegra("TF002");
            LOGGER.error((Object)errorMsg, (Throwable)e);
            throw new TransformersException(errorMsg, e);
        }
        catch (Exception e) {
            String errorMsg = Language.getResIntegra("TF003");
            LOGGER.error((Object)errorMsg, (Throwable)e);
            throw new TransformersException(errorMsg, e);
        }
        return res;
    }

    public Document getParserTemplateByRequestType(String serviceReq, String method, String version) throws TransformersException {
        Document res = null;
        LOGGER.debug((Object)Language.getFormatResIntegra("TF009", new Object[]{serviceReq, method, "parser", version}));
        try {
            String fileName = this.transformersProperties.getProperty(serviceReq + "." + method + "." + version + "." + "parser" + "." + "template");
            LOGGER.debug((Object)Language.getFormatResIntegra("TF005", new Object[]{fileName}));
            String xmlTemplateFolder = this.transformersProperties.getProperty("TransformersTemplatesPath") + "/parserTemplates";
            LOGGER.debug((Object)Language.getFormatResIntegra("TF010", new Object[]{xmlTemplateFolder}));
            LOGGER.debug((Object)Language.getResIntegra("TF008"));
            File xmlFile = new File(xmlTemplateFolder, fileName);
            res = UtilsXML.parseDocument(new FileReader(xmlFile));
            LOGGER.debug((Object)Language.getFormatResIntegra("TF006", new Object[]{res.getDocumentElement().getTagName()}));
        }
        catch (IOException e) {
            String errorMsg = Language.getResIntegra("TF002");
            LOGGER.error((Object)errorMsg, (Throwable)e);
            throw new TransformersException(errorMsg, e);
        }
        catch (Exception e) {
            String errorMsg = Language.getResIntegra("TF003");
            LOGGER.error((Object)errorMsg, (Throwable)e);
            throw new TransformersException(errorMsg, e);
        }
        return res;
    }

    public String generateXml(Map<String, Object> parameters, String service, String version) throws TransformersException {
        return this.generateXml(parameters, service, this.getMethodName(service), version);
    }

    private String getMethodName(String service) {
        if ("DSSAfirmaSign".equals(service)) {
            return "sign";
        }
        if ("DSSAfirmaVerifyCertificate".equals(service)) {
            return "verify";
        }
        if ("DSSAfirmaVerify".equals(service)) {
            return "verify";
        }
        if ("DSSBatchVerifySignature".equals(service)) {
            return "verifySignatures";
        }
        if ("DSSBatchVerifyCertificate".equals(service)) {
            return "verifyCertificates";
        }
        if ("DSSAsyncRequestStatus".equals(service)) {
            return "getProcessResponse";
        }
        return service;
    }

    public String generateXml(Map<String, Object> parameters, String service, String method, String version) throws TransformersException {
        if (!(parameters != null && GenericUtils.assertStringValue(service) && GenericUtils.assertStringValue(version) && GenericUtils.assertStringValue(method))) {
            String errorMsg = Language.getResIntegra("TF003");
            LOGGER.error((Object)errorMsg);
            throw new TransformersException(errorMsg);
        }
        String res;
        try {
            Class<?> transformerClass = XmlTransformersFactory.getXmlTransformer(service, method, "request", version);
            res = (String)this.invokeCommonXmlTransf(transformerClass, parameters, service, method, "request", version);
        }
        catch (Exception e) {
            LOGGER.error((Object)e);
            throw new TransformersException(e);
        }
        return res;
    }

    public Map<String, Object> parseResponse(String response, String service, String version) throws TransformersException {
        return this.parseResponse(response, service, this.getMethodName(service), version);
    }

    public Map<String, Object> parseResponse(String response, String service, String method, String version) throws TransformersException {
        Map res = null;
        if (!(GenericUtils.assertStringValue(response) && GenericUtils.assertStringValue(service) && GenericUtils.assertStringValue(version) && GenericUtils.assertStringValue(method))) {
            String errorMsg = Language.getResIntegra("TF003");
            LOGGER.error((Object)errorMsg);
            throw new TransformersException(errorMsg);
        }
        try {
            Class<Object> transformerClass = ParseTransformersFactory.getParseTransformer(service, method, version);
            res = (Map)this.invokeParseTransf(response, transformerClass, service, method, version);
        }
        catch (Exception e) {
            LOGGER.error((Object)e);
            throw new TransformersException(e);
        }
        return res;
    }

    private Object invokeCommonXmlTransf(Class<?> transformerClass, Map<String, Object> parameters, String service, String methodWS, String type, String version) throws TransformersException {
        String res = null;
        try {
            Class[] constrParamClasses = new Class[]{Class.forName(String.class.getName()), Class.forName(String.class.getName()), Class.forName(String.class.getName()), Class.forName(String.class.getName())};
            Constructor constructor = transformerClass.getConstructor(constrParamClasses);
            Object[] constrParamObjects = new Object[]{service, methodWS, type, version};
            Object object = constructor.newInstance(constrParamObjects);
            Class[] methodParamClasses = new Class[]{Class.forName("java.lang.Object")};
            Method method = transformerClass.getMethod("transform", methodParamClasses);
            Object[] methodParamObjects = new Object[]{parameters};
            res = (String)method.invoke(object, methodParamObjects);
        }
        catch (Exception e) {
            String errorMsg = Language.getResIntegra("TF001");
            LOGGER.error((Object)errorMsg, (Throwable)e);
            throw new TransformersException(errorMsg, e);
        }
        return res;
    }

    private Object invokeParseTransf(String response, Class<Object> transformerClass, String service, String methodParam, String version) throws TransformersException {
        Map res = null;
        try {
            Class stringClass = Class.forName("java.lang.String");
            Class[] constrParamClasses = new Class[]{stringClass, stringClass, stringClass};
            Constructor<Object> constructor = transformerClass.getConstructor(constrParamClasses);
            Object[] constrParamObjects = new Object[]{service, methodParam, version};
            Object object = constructor.newInstance(constrParamObjects);
            Class[] methodParamClasses = new Class[]{stringClass};
            Method method = transformerClass.getMethod("transform", methodParamClasses);
            Object[] methodParamObjects = new Object[]{response};
            res = (Map)method.invoke(object, methodParamObjects);
        }
        catch (Exception e) {
            String errorMsg = Language.getResIntegra("TF001");
            LOGGER.error((Object)errorMsg, (Throwable)e);
            throw new TransformersException(errorMsg, e);
        }
        return res;
    }

    public String getParserParameterValue(String parameterName) {
        String result = null;
        if (GenericUtils.assertStringValue(parameterName)) {
            Object tmp = this.parserParamsProp.get(parameterName);
            result = tmp == null ? null : tmp.toString();
        }
        return result;
    }
}


