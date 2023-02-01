package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.persist.entity.DocumentEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

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
        if (document.getUrl() == null) {
            return null;
        }
        try {
            byte[] contingut = downloadUsingStream(document.getUrl(), "document");
            return new ArxiuDto(nom, "PDF", contingut, contingut.length);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] downloadUsingStream(String urlStr, String file) throws IOException {

        var url = new URL(urlStr);
        var bis = new BufferedInputStream(url.openStream());
        var fis = new FileOutputStream(file);
        var buffer = new byte[1024];
        var count = 0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
        return buffer;
    }

}
