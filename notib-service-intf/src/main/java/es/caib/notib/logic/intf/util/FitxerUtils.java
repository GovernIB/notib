package es.caib.notib.logic.intf.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class FitxerUtils {

    public static void esborrar(File f) {

        try {
            Files.delete(f.toPath());
        } catch (IOException ex) {
            log.error("Error esborrant el fitxer " + f.getName(), ex);
        }
    }

}
