package es.caib.notib.core.helper;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

public class AuxTest {
    @Test
    public void deleteFilesOlderThanOneDay() {

        String baseDir = "/home/siona/Escriptori/tmp";

        try {
            String command = SystemUtils.IS_OS_LINUX ?
                    "find " + baseDir + " -mindepth 1 -type f -mtime +1 -delete" :
                    "forfiles /p \"" + baseDir + "\" /s /d -1 /c \"cmd /c del /q @file\"";
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            process.destroy();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
