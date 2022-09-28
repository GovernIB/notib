package es.caib.notib.logic.utils;

import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CSVReader {

    public static List<String[]> readFile(byte[] fitxer) {
        List<String[]> linies = new ArrayList<>();
        ICsvListReader listReader = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(fitxer);
            String detectedCharset = UniversalDetector.detectCharset(bais);
            if(detectedCharset == null) {
                detectedCharset = "UTF-8";
            }
            Reader reader = new InputStreamReader( new ByteArrayInputStream(fitxer), detectedCharset);
            listReader = new CsvListReader(reader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
            List<String> linia;
            int index = 0;
            while( (linia = listReader.read()) != null ) {
                if (index > 0) {
                    linies.add(linia.toArray(new String[]{}));
                }
                index++;
            }
            if( listReader != null ) {
                listReader.close();
            }
        } catch (IOException e) {
            log.debug("S'ha produït un error a l'llegir el fitxer CSV.", e);
            return null;
        }
        return linies;
    }
    public static List<String> readHeader(byte[] fitxer) {
        ICsvListReader listReader = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(fitxer);
            String detectedCharset = UniversalDetector.detectCharset(bais);
            if(detectedCharset == null) {
                detectedCharset = "UTF-8";
            }
            Reader reader = new InputStreamReader( new ByteArrayInputStream(fitxer), detectedCharset);
            listReader = new CsvListReader(reader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
            List<String> res = listReader.read();
            listReader.close();
            return  res;
        } catch (IOException e) {
            log.debug("S'ha produït un error a l'llegir el fitxer CSV.", e);
            return null;
        }
    }
}
