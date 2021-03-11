package es.caib.notib.plugin.gesdoc;

import es.caib.notib.plugin.SistemaExternException;
import es.caib.notib.plugin.utils.PropertiesHelper;
import junit.framework.TestCase;
import org.junit.Before;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Random;

public class GestioDocumentalPluginFilesystemTest extends TestCase {

    private GestioDocumentalPluginFilesystem plugin;

    // Set the path where to store the files
    private final String BASE_DIR = "/home/bgalmes/dades/notib-fs/";

    @Before
    public void setUp() throws Exception {
        PropertiesHelper.getProperties().setLlegirSystem(false);
        PropertiesHelper.getProperties().setProperty(
                "es.caib.notib.plugin.gesdoc.filesystem.base.dir",
                BASE_DIR);
        plugin = new GestioDocumentalPluginFilesystem();
    }

    public void testCreate() {
        Random r = new Random();
        String[] agrupacions = new String[]{"certificacions", "notificacions", "tmp"};
        long nFiles = GestioDocumentalPluginFilesystem.getMAX_FILES_IN_FOLDER() * 5 * agrupacions.length;
        for (int i =0; i < nFiles; i++) {
            String agr = agrupacions[r.nextInt(agrupacions.length)];
            byte[] contingut = new byte[]{4, 5, 6};
            try {
                plugin.create(agr, new ByteArrayInputStream(contingut));
            } catch (SistemaExternException e) {
                fail();
            }
        }
        for (String agrupacio : agrupacions){
            File file = new File(getBaseDir(agrupacio));
            File[] files = file.listFiles();
            for (File f : files){
                File[] files2 = f.listFiles();
                if (files2 != null){
                    assertFalse(files2.length > GestioDocumentalPluginFilesystem.getMAX_FILES_IN_FOLDER());
                }
            }
        }

        // Esborram tots els fitxers creats
        for (String agrupacio : agrupacions){
            File file = new File(getBaseDir(agrupacio));
            file.delete();
        }
    }

    public void testUpdate() {
    }

    public void testDelete() {
    }

    public void testGet() {
    }

    private String getBaseDir(String agrupacio) {
        String baseDir = PropertiesHelper.getProperties().getProperty(
                "es.caib.notib.plugin.gesdoc.filesystem.base.dir");
        if (baseDir != null) {
            if (baseDir.endsWith("/")) {
                return baseDir + agrupacio;
            } else {
                return baseDir + "/" + agrupacio;
            }
        }
        return baseDir;
    }
}