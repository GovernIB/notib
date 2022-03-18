package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.entity.DocumentEntity;
import es.caib.plugins.arxiu.api.DocumentContingut;
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
        if (document == null)
            return null;
        if(document.getArxiuGestdocId() != null) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            pluginHelper.gestioDocumentalGet(
                    document.getArxiuGestdocId(),
                    PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
                    output);
            return new ArxiuDto(
                    document.getArxiuNom() != null ? document.getArxiuNom() : nomDocumetnDefault,
                    null,
                    output.toByteArray(),
                    output.size());
        }else if(document.getUuid() != null){
            DocumentContingut dc = pluginHelper.arxiuGetImprimible(document.getUuid(), true);
            return new ArxiuDto(
                    document.getArxiuNom() != null ? document.getArxiuNom() : nomDocumetnDefault,
                    dc.getTipusMime(),
                    dc.getContingut(),
                    dc.getTamany());
        }else if(document.getCsv() != null){
            DocumentContingut dc = pluginHelper.arxiuGetImprimible(document.getCsv(), false);
            return new ArxiuDto(
                    document.getArxiuNom() != null ? document.getArxiuNom() : nomDocumetnDefault,
                    dc.getTipusMime(),
                    dc.getContingut(),
                    dc.getTamany());
        }else if(document.getUrl() != null){
            try {
                byte[] contingut = downloadUsingStream(document.getUrl(), "document");
                return new ArxiuDto(
                        document.getArxiuNom() != null ? document.getArxiuNom() : nomDocumetnDefault,
                        "PDF",
                        contingut,
                        contingut.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private byte[] downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
        return buffer;
    }

}
