package es.caib.notib.logic.utils;

import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Slf4j
public class CSVReader {

    public static ICsvListReader setupCSVListReader(byte[] file) throws IOException {

        var bais = new ByteArrayInputStream(file);
        var detectedCharset = UniversalDetector.detectCharset(bais);
        if (detectedCharset == null) {
            detectedCharset = "UTF-8";
        }
        var fitxer = new ByteArrayInputStream(file);
        var reader = new InputStreamReader(fitxer, detectedCharset);
        var cvList = new CsvListReader(reader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
        bais.close();
        fitxer.close();
//        reader.close();
//        cvList.close();
        return cvList;
    }

    public static <T> T readLinesFromCSVFile(byte[] file, Function<ICsvListReader, T> fn) {
        T result;
        try (var listReader = setupCSVListReader(file)) {
            result = fn.apply(listReader);
        } catch (IOException e) {
            log.debug("S'ha produït un error a l'llegir el fitxer CSV.", e);
            return null;
        }
        return result;
    }

    public static List<List<String>> readFile(byte[] file) {
        return readLinesFromCSVFile(file, listReader -> {
            var lines = new ArrayList<List<String>>();
            List<String> line;
            try {
                while ((line = listReader.read()) != null) {
                    lines.add(line);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return lines;
        });
    }

    public static List<List<String>> readBody(byte[] file) {
        return readLinesFromCSVFile(file, listReader -> {
            var lines = new ArrayList<List<String>>();
            var index = new AtomicInteger(0);
            List<String> line;
            try {
                while ((line = listReader.read()) != null) {
                    if (index.getAndIncrement() > 0) {
                        lines.add(line);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return lines;
        });
    }

    public static List<String> readHeader(byte[] file) {
        return readLinesFromCSVFile(file, listReader -> {
            try {
                return listReader.read();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
