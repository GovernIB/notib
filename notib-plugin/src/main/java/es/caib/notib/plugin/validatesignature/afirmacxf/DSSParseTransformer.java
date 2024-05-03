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
import es.gob.afirma.utils.UtilsXML;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DSSParseTransformer
implements IParseTransformer {
    private static final Logger LOGGER = Logger.getLogger(DSSParseTransformer.class);
    private String request = null;
    private String messageVersion = null;
    private String method = null;

    public DSSParseTransformer(String req, String methodParam, String msgVersion) {
        this.request = req;
        this.messageVersion = msgVersion;
        this.method = methodParam;
    }

    @Override
    public final String getRequest() {
        return this.request;
    }

    @Override
    public final String getMessageVersion() {
        return this.messageVersion;
    }

    @Override
    public final Object transform(String xmlResponse) throws TransformersException {
        Document docResp;
        Map<String, Object> result = null;
        Map<String, Object> nodesToParser = null;
        try {
            docResp = UtilsXML.parseDocument(new StringReader(xmlResponse));
        }
        catch (Exception e) {
            throw new TransformersException(Language.getResIntegra("DPT001"), e);
        }
        Document docTemplate = TransformersFacade.getInstance().getParserTemplateByRequestType(this.request, this.method, this.messageVersion);
        if (docTemplate != null && docResp != null) {
            nodesToParser = this.getDSSNodesToParser(UtilsXML.searchChildElements(docTemplate.getDocumentElement()));
            result = this.parseDSSNodes(nodesToParser, docResp.getDocumentElement());
        }
        return result;
    }

    private Map<String, Object> getDSSNodesToParser(List<Element> childNodes) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        for (Element element : childNodes) {
            if (element.getNodeType() == 1 && element.getAttributeNode("afirmaNodeType") != null) {
                String nodeType = element.getAttribute("afirmaNodeType");
                if ("attribute".equals(nodeType) || "attributeText".equals(nodeType)) {
                    result.put(UtilsXML.getNodeXPath(element), nodeType + "#" + element.getAttribute("attributesToInclude"));
                } else {
                    if ("mapFields".equals(nodeType)) {
                        this.getMapFields(element, result);
                        continue;
                    }
                    if ("severalOcurrences".equals(nodeType)) {
                        if (element.getAttributeNode("ocurrenceNames") != null) {
                            this.getOcurrenceNodes(element, result);
                            continue;
                        }
                    } else {
                        result.put(UtilsXML.getNodeXPath(element), nodeType);
                    }
                }
            }
            if (UtilsXML.searchChildElements(element).isEmpty()) continue;
            result.putAll(this.getDSSNodesToParser(UtilsXML.searchChildElements(element)));
        }
        return result;
    }

    private void getOcurrenceNodes(Element element, Map<String, Object> result) {
        String severalNodeNames = element.getAttribute("ocurrenceNames");
        List<String> ocurrenceNames = Arrays.asList(severalNodeNames.split(","));
        if (!UtilsXML.searchChildElements(element).isEmpty()) {
            List<Element> childs = UtilsXML.searchChildElements(element);
            for (Element child : childs) {
                ArrayList<Element> tmpList = new ArrayList<Element>();
                if (ocurrenceNames.contains(child.getNodeName())) {
                    Element occurrenceNode = UtilsXML.searchChild(element, child.getNodeName());
                    tmpList.add(occurrenceNode);
                    result.put(UtilsXML.getNodeXPath(child), this.getDSSNodesToParser(tmpList));
                    continue;
                }
                tmpList.add(child);
                result.putAll(this.getDSSNodesToParser(tmpList));
            }
        }
    }

    private void getMapFields(Element element, Map<String, Object> result) {
        NodeList nl = UtilsXML.getFirstElementNode(element).getChildNodes();
        String key = null;
        String value = null;
        for (int i = 0; nl != null && i < nl.getLength(); ++i) {
            Node tmpNode = nl.item(i);
            if (tmpNode == null || tmpNode.getNodeType() != 1) continue;
            String attrTmp = ((Element)tmpNode).getAttribute("afirmaNodeType");
            if (attrTmp != null && attrTmp.equals("mapFieldKey")) {
                key = tmpNode.getNodeName();
                continue;
            }
            if (attrTmp == null || !attrTmp.equals("mapFieldValue")) continue;
            value = tmpNode.getNodeName();
        }
        if (!GenericUtils.checkNullValues(key, value)) {
            result.put(UtilsXML.getNodeXPath(element), "mapFields#" + key + "," + value);
        }
    }

    private Map<String, Object> parseDSSNodes(Map<String, Object> nodesToParser, Element elResponse) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (nodesToParser != null && elResponse != null) {
            String attribPath = UtilsXML.getNodeXPath(elResponse);
            if (nodesToParser.containsKey(attribPath)) {
                this.setAttributesValuesToMap(elResponse, nodesToParser.get(attribPath).toString(), result);
            }
            List<Element> childs = UtilsXML.searchChildElements(elResponse);
            for (Element element : childs) {
                String nodeXPath = UtilsXML.getNodeXPath(element);
                if (nodesToParser.containsKey(nodeXPath)) {
                    Object nodeType = nodesToParser.get(nodeXPath);
                    if (nodeType instanceof Map) {
                        this.readMapNode((Map)nodeType, nodeXPath, element, result);
                    } else if (nodeType instanceof String) {
                        this.readValueOfNode(nodeType.toString(), nodeXPath, element, result);
                    }
                }
                if (UtilsXML.searchChildElements(element).isEmpty()) continue;
                result.putAll(this.parseDSSNodes(nodesToParser, element));
            }
        }
        return result;
    }

    private void readMapNode(Map<String, Object> nodeType, String nodeXPath, Element element, Map<String, Object> result) {
        List<Object> childrens = UtilsXML.searchListChilds((Element)element.getParentNode(), element.getNodeName());
        if (childrens != null && childrens.size() > 0) {
            HashMap[] multipleNodes = new HashMap[childrens.size()];
            for (int i = 0; childrens.size() > i; ++i) {
                multipleNodes[i] = (HashMap) this.parseDSSNodes(nodeType, (Element)childrens.get(i));
            }
            result.put(nodeXPath, multipleNodes);
        }
    }

    private void readValueOfNode(String nodeType, String nodeXPath, Element element, Map<String, Object> result) {
        if (nodeType.startsWith("text")) {
            String textValue = UtilsXML.getElementValue(element);
            result.put(nodeXPath, textValue);
        } else if (nodeType.startsWith("attribute") || nodeType.startsWith("attributeText")) {
            this.setAttributesValuesToMap(element, nodeType, result);
            if (nodeType.startsWith("attributeText")) {
                String textValue = UtilsXML.getElementValue(element);
                result.put(nodeXPath, textValue);
            }
        } else if (nodeType.startsWith("mapFields")) {
            String[] data;
            int index = nodeType.indexOf("#");
            if (index >= 0 && (data = nodeType.substring(index + 1).split(",")).length == 2) {
                String keyTagName = data[0];
                String valueTagName = data[1];
                List<Element> elementFields = UtilsXML.searchChildElements(element);
                HashMap<String, String> mapFields = new HashMap<String, String>();
                for (Element tmpField : elementFields) {
                    mapFields.put(UtilsXML.getElementValue(tmpField, keyTagName), UtilsXML.getElementValue(tmpField, valueTagName));
                }
                result.put(nodeXPath, mapFields);
            }
        } else if (nodeType.startsWith("xml")) {
            result.put(nodeXPath, this.readXmlContent(nodeXPath, element));
        }
    }

    private String readXmlContent(String nodeXPath, Element element) {
        try {
            return UtilsXML.transformDOMtoString(element, true);
        }
        catch (TransformersException e) {
            LOGGER.warn((Object)Language.getFormatResIntegra("DPT002", new Object[]{nodeXPath}), (Throwable)e);
            return UtilsXML.getElementValue(element);
        }
    }

    private void setAttributesValuesToMap(Element element, String attrKeys, Map<String, Object> nodesValues) {
        int index;
        if (attrKeys != null && nodesValues != null && (index = attrKeys.indexOf("#")) >= 0) {
            String[] data;
            String attributeNames = attrKeys.substring(index + 1);
            for (String attName : data = attributeNames.split(",")) {
                String attributeValue = UtilsXML.getAttributeValue(element, attName);
                if (attributeValue == null || attributeValue.trim().length() <= 0) continue;
                nodesValues.put(UtilsXML.getNodeXPath(element) + "@" + attName, attributeValue);
            }
        }
    }

    @Override
    public final String getMethod() {
        return this.method;
    }
}

