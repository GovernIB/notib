package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.persist.entity.DocumentEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Slf4j
@Component
public class DocumentHelper {

    @Autowired
    private PluginHelper pluginHelper;

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

}
