/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package es.caib.notib.plugin.validatesignature.afirmacxf;

import es.gob.afirma.i18n.Language;
import es.gob.afirma.transformers.TransformersConstants;
import es.gob.afirma.transformers.TransformersException;
import es.gob.afirma.transformers.xmlTransformers.IXmlTransformer;
import es.gob.afirma.utils.UtilsXML;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DSSXmlTransformer
implements IXmlTransformer {
    private String service = null;
    private String type = null;
    private String messageVersion = null;
    private String method;
    private static Logger logger = Logger.getLogger(DSSXmlTransformer.class);

    public DSSXmlTransformer(String svc, String methodParam, String typ, String msgVersion) {
        this.service = svc;
        this.method = methodParam;
        this.type = typ;
        this.messageVersion = msgVersion;
    }

    @Override
    public final String getService() {
        return this.service;
    }

    @Override
    public final String getType() {
        return this.type;
    }

    @Override
    public final String getMessageVersion() {
        return this.messageVersion;
    }

    @Override
    public final Object transform(Object params) throws TransformersException {
        if (!(params instanceof Map) || ((Map)params).size() == 0) {
            throw new TransformersException(Language.getResIntegra("DXT001"));
        }
        if (!this.messageVersion.equals("1_0")) {
            String errorMsg = Language.getFormatResIntegra("DXT003", new Object[]{this.messageVersion, this.service});
            logger.error((Object)errorMsg);
            throw new TransformersException(errorMsg);
        }
        Document doc = TransformersFacade.getInstance().getXmlRequestFileByRequestType(this.service, this.method, this.type, this.messageVersion);
        logger.debug((Object)Language.getFormatResIntegra("DXT002", new Object[]{this.service, this.messageVersion}));
        String result = this.transformXmlVersion1((Map)params, doc);
        return result;
    }

    private String transformXmlVersion1(Map<String, Object> parameters, Document doc) throws TransformersException {
        logger.debug((Object)Language.getResIntegra("DXT004"));
        String result = null;
        this.createXmlNodes(parameters, doc.getDocumentElement());
        UtilsXML.deleteNodesNotUsed(doc.getDocumentElement(), TransformersConstants.OPTIONAL_ANODE_TYPES);
        try {
            result = UtilsXML.transformDOMtoString(doc);
        }
        catch (Exception e) {
            throw new TransformersException(e.getMessage(), e);
        }
        logger.debug((Object)Language.getResIntegra("DXT005"));
        return result;
    }

    private void createXmlNodes(Map<String, Object> parameters, Element element) throws TransformersException {
        for (String key : parameters.keySet()) {
            Object value = parameters.get(key);
            if (value instanceof Map[]) {
                this.addValueIntoNodeMultiple(element, key, (Map[])value);
                continue;
            }
            if (value instanceof String) {
                this.addValueIntoNode(element, key, (String)value);
                continue;
            }
            Object valueType = value;
            if (value != null && value.getClass() != null) {
                valueType = value.getClass().getName();
            }
            throw new TransformersException(Language.getFormatResIntegra("DXT006", new Object[]{key, valueType}));
        }
    }

    private void addValueIntoNodeMultiple(Element xmlElement, String basePath, Map<String, Object>[] valuesNodes) throws TransformersException {
        Element parent = (Element)UtilsXML.searchChild(xmlElement, basePath).getParentNode();
        Element templateToRepeat = UtilsXML.removeElement(xmlElement, basePath);
        for (Map<String, Object> parametersNode : valuesNodes) {
            if (parametersNode.size() <= 0) continue;
            Element instance = (Element)templateToRepeat.cloneNode(true);
            this.createXmlNodes(parametersNode, instance);
            parent.appendChild(instance);
        }
        UtilsXML.removeAfirmaAttribute(parent);
    }

    private void addValueIntoNode(Element xmlElement, String path, String value) throws TransformersException {
        Element result = null;
        result = path.contains("@") ? UtilsXML.insertAttributeValue(xmlElement, path, value) : UtilsXML.insertValueElement(xmlElement, path, value);
        if (result == null) {
            throw new TransformersException(Language.getFormatResIntegra("DXT007", new Object[]{path}));
        }
    }

    @Override
    public final String getMethod() {
        return this.method;
    }
}

