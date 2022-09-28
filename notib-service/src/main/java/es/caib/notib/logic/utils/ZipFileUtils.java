package es.caib.notib.logic.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class ZipFileUtils {

    public static List<String> readZipFileNames (byte [] fitxer) {
        List<String> names = new ArrayList<String>();
        try {
            ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(fitxer));
            ZipEntry entrada;
            try {
            	entrada = zip.getNextEntry();
            } catch (IllegalArgumentException iae) {
            	zip = new ZipInputStream(new ByteArrayInputStream(fitxer), Charset.forName("Cp437")); // Si falla ho provam de llegir amb la codificació Cp437
            	entrada = zip.getNextEntry();
            }
            while (null != (entrada) ){
                names.add(entrada.getName());
                zip.closeEntry();
                entrada = zip.getNextEntry();
            }
            zip.close();
        } catch (Exception e) {
            log.debug("S'ha produït un error a l'llegir el fitxer ZIP per obtenir els noms dels fitxers.", e);
            return null;
        }
        return names;
    }

    public static byte [] readZipFile (byte [] fitxer, String fileName) {
        ByteArrayOutputStream baos;
        byte arxiuBytes[] = null;
        try {
        	 ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(fitxer));
             ZipEntry entrada;
             try {
             	entrada = zip.getNextEntry();
             } catch (IllegalArgumentException iae) {
             	zip = new ZipInputStream(new ByteArrayInputStream(fitxer), Charset.forName("Cp437")); // Si falla ho provam de llegir amb la codificació Cp437
             	entrada = zip.getNextEntry();
             }
            while (null != (entrada) ){
                if (fileName.equalsIgnoreCase(entrada.getName())) {
                    baos = new ByteArrayOutputStream();
                    int leido;
                    byte [] buffer = new byte[1024];
                    while ( 0 < (leido=zip.read(buffer))){
                        baos.write(buffer,0,leido);

                    }
                    arxiuBytes = baos.toByteArray();
                    baos.close();
                    zip.closeEntry();
                }
                entrada = zip.getNextEntry();
            }
            zip.close();
        } catch (Exception e) {
            log.debug("S'ha produït un error a l'llegir el fitxer ZIP per extreure un fitxer.", e);
            return null;
        }
        return arxiuBytes;
    }

}
