package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.DocumentValidDto;
import es.caib.notib.logic.intf.dto.SignatureInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.Document;
import es.caib.notib.logic.utils.MimeUtils;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.plugins.arxiu.api.DocumentContingut;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Slf4j
@Component
public class DocumentHelper {

    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private ConfigHelper configHelper;

    private static final OrigenEnum ORIGEN = OrigenEnum.ADMINISTRACIO;
    private static final ValidesaEnum VALIDESA = ValidesaEnum.ORIGINAL;
    private static final TipusDocumentalEnum TIPUS_DOCUMENTAL = TipusDocumentalEnum.NOTIFICACIO;
    private static final boolean MODE_FIRMA = false;

    public ArxiuDto documentToArxiuDto(String nomDocumetnDefault, DocumentEntity document) {

        if (document == null) {
            return null;
        }
        var nom = document.getArxiuNom() != null ? document.getArxiuNom() : nomDocumetnDefault;
        if (document.getArxiuGestdocId() != null) {
            var output = new ByteArrayOutputStream();
            pluginHelper.gestioDocumentalGet(document.getArxiuGestdocId(), PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, output);
            return new ArxiuDto(nom, null, output.toByteArray(), output.size());
        }
        if (document.getUuid() != null){
            var dc = pluginHelper.arxiuGetImprimible(document.getUuid(), true);
            return new ArxiuDto(nom, dc.getTipusMime(), dc.getContingut(), dc.getTamany());
        }
        if (document.getCsv() != null){
            var dc = pluginHelper.arxiuGetImprimible(document.getCsv(), false);
            return new ArxiuDto(nom, dc.getTipusMime(), dc.getContingut(), dc.getTamany());
        }
        return null;
    }

    public DocumentValidDto getDocument(Document document) {

        DocumentValidDto documentDto = null;
        if (document == null || document.isEmpty() || !document.hasOnlyOneSource()) {
            return documentDto;
        }

        var dto = new DocumentValidDto();
        boolean utilizarMetadadesPerDefecte = getUtilizarValoresPorDefecto();

        if (!Strings.isNullOrEmpty(document.getUuid())) {
            dto = getDocumentByUUID(document, utilizarMetadadesPerDefecte);
        }
        if (!Strings.isNullOrEmpty(document.getCsv())) {
            dto = getDocumentByCSV(document, utilizarMetadadesPerDefecte);
        }
        if (!Strings.isNullOrEmpty(document.getContingutBase64())) {
            dto = getDocumentByContingut(document, utilizarMetadadesPerDefecte);
        }
        return dto;
    }

    private DocumentValidDto getDocumentByUUID(Document document, boolean utilizarMetadadesPerDefecte) {

        var dto = new DocumentValidDto();
        dto.setArxiuNom(document.getArxiuNom());
        dto.setUuid(document.getUuid());
        dto.setNormalitzat(document.isNormalitzat());

        DocumentContingut contingut = null;
        try {
            contingut = pluginHelper.arxiuGetImprimible(document.getUuid(), true);
        } catch (Exception ex) {
            dto.setErrorFitxer(true);
            return dto;
        }

        dto.setMediaType(contingut.getTipusMime());
        dto.setMida(contingut.getTamany());

        es.caib.plugins.arxiu.api.Document doc = null;
        try {
            doc = pluginHelper.arxiuDocumentConsultar(document.getUuid(), null, true, true);
            if (doc.getMetadades() == null && !utilizarMetadadesPerDefecte) {
                dto.setErrorMetadades(true);
            }
        } catch (Exception ex) {
            dto.setErrorMetadades(true);
        }

        if (doc != null && doc.getMetadades() != null) {
            dto.setOrigen(OrigenEnum.valorAsEnum(doc.getMetadades().getOrigen() != null ? doc.getMetadades().getOrigen().ordinal() : null));
            dto.setValidesa(ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(doc.getMetadades().getEstatElaboracio())));
            dto.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(doc.getMetadades().getTipusDocumental() != null ? doc.getMetadades().getTipusDocumental().toString() : null));
            dto.setModoFirma(pluginHelper.getModeFirma(doc, doc.getContingut().getArxiuNom()) == 1 ? Boolean.TRUE : Boolean.FALSE);
            // Recuperar csv
            Map<String, Object> metadadesAddicionals = doc.getMetadades().getMetadadesAddicionals();
            if (metadadesAddicionals != null) {
                if (metadadesAddicionals.containsKey("csv"))
                    dto.setCsv((String) metadadesAddicionals.get("csv"));
                else if (metadadesAddicionals.containsKey("eni:csv"))
                    dto.setCsv((String) metadadesAddicionals.get("eni:csv"));
            }
        } else if (utilizarMetadadesPerDefecte) {
            dto.setOrigen(ORIGEN);
            dto.setValidesa(VALIDESA);
            dto.setTipoDocumental(TIPUS_DOCUMENTAL);
            dto.setModoFirma(MODE_FIRMA);
        }

        return dto;
    }

    private DocumentValidDto getDocumentByCSV(Document document, boolean utilizarMetadadesPerDefecte) {
        var dto = new DocumentValidDto();

        dto.setArxiuNom(document.getArxiuNom());
        dto.setCsv(document.getCsv());
        dto.setNormalitzat(document.isNormalitzat());

        DocumentContingut contingut = null;
        try {
            contingut = pluginHelper.arxiuGetImprimible(document.getCsv(), false);
        } catch (Exception ex) {
            dto.setErrorFitxer(true);
            return dto;
        }

        dto.setMediaType(contingut.getTipusMime());
        dto.setMida(contingut.getTamany());

        es.caib.plugins.arxiu.api.Document doc = null;
        try {
            doc = pluginHelper.arxiuDocumentConsultar(document.getCsv(), null, true, false);
            if (doc.getMetadades() == null && !utilizarMetadadesPerDefecte) {
                dto.setErrorMetadades(true);
            }
        } catch (Exception ex) {
            dto.setErrorMetadades(true);
        }

        if (doc != null && doc.getMetadades() != null) {
            dto.setOrigen(OrigenEnum.valorAsEnum(doc.getMetadades().getOrigen() != null ? doc.getMetadades().getOrigen().ordinal() : null));
            dto.setValidesa(ValidesaEnum.valorAsEnum(pluginHelper.estatElaboracioToValidesa(doc.getMetadades().getEstatElaboracio())));
            dto.setTipoDocumental(TipusDocumentalEnum.valorAsEnum(doc.getMetadades().getTipusDocumental() != null ? doc.getMetadades().getTipusDocumental().toString() : null));
            dto.setModoFirma(pluginHelper.getModeFirma(doc, doc.getContingut().getArxiuNom()) == 1 ? Boolean.TRUE : Boolean.FALSE);
        } else if (utilizarMetadadesPerDefecte){
            dto.setOrigen(ORIGEN);
            dto.setValidesa(VALIDESA);
            dto.setTipoDocumental(TIPUS_DOCUMENTAL);
            dto.setModoFirma(MODE_FIRMA);
        }

        return dto;
    }

    private DocumentValidDto getDocumentByContingut(Document document, boolean utilizarMetadadesPerDefecte) {
        var dto = new DocumentValidDto();

        byte[] contingut = Base64.decodeBase64(document.getContingutBase64());
        String mediaType = MimeUtils.getMimeTypeFromContingut(document.getArxiuNom(), contingut);
        boolean isPdf = MediaType.APPLICATION_PDF_VALUE.equals(mediaType);
        if (isPdf && isValidaFirmaRestEnabled()) {
            SignatureInfoDto signatureInfo = pluginHelper.detectSignedAttachedUsingValidateSignaturePlugin(contingut, document.getArxiuNom(), mediaType);
            if (signatureInfo.isError()) {
                dto.setErrorFirma(true);
                dto.setErrorFirmaMsg(signatureInfo.getErrorMsg());
            }
        }
        String documentGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, contingut);

        dto.setArxiuNom(document.getArxiuNom());
        dto.setMediaType(mediaType);
        dto.setArxiuGestdocId(documentGesdocId);
        dto.setMida(Long.valueOf(contingut.length));
        dto.setNormalitzat(document.isNormalitzat());
        dto.setOrigen(document.getOrigen() == null && utilizarMetadadesPerDefecte ? ORIGEN : document.getOrigen());
        dto.setValidesa(document.getValidesa() == null && utilizarMetadadesPerDefecte ? VALIDESA : document.getValidesa());
        dto.setTipoDocumental(document.getTipoDocumental() == null && utilizarMetadadesPerDefecte ? TIPUS_DOCUMENTAL : document.getTipoDocumental());
        if (document.getModoFirma() != null || utilizarMetadadesPerDefecte) {
            dto.setModoFirma(document.getModoFirma() != null ? document.getModoFirma() : MODE_FIRMA);
        }

        return dto;
    }

    private boolean isValidaFirmaRestEnabled() {
        return configHelper.getConfigAsBoolean("es.caib.notib.plugins.validatesignature.enable.rest");
    }

    // Indica si usar valores por defecto cuando ni el documento ni documentV2 tienen metadades
    private boolean getUtilizarValoresPorDefecto() {
        return configHelper.getConfigAsBoolean("es.caib.notib.document.metadades.por.defecto");
    }

}
