package es.caib.notib.logic.test;

import lombok.Builder;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;

public class NotificacioMassivaTests {

    public static TestMassiusFiles getTest1Files() {

        return TestMassiusFiles.builder().csvContent(getFileContent("/es/caib/notib/logic/massiu/test1.csv"))
                .zipContent(getFileContent("/es/caib/notib/logic/massiu/test1.zip")).build();
    }
    public static TestMassiusFiles getTest2Files() {

        return TestMassiusFiles.builder().csvContent(getFileContent("/es/caib/notib/logic/massiu/test2.csv"))
                .zipContent(getFileContent("/es/caib/notib/logic/massiu/test1.zip")).build();
    }

    public static TestMassiusFiles getTest3Files() {

        return TestMassiusFiles.builder().csvContent(getFileContent("/es/caib/notib/logic/massiu/test3.csv"))
                .zipContent(getFileContent("/es/caib/notib/logic/massiu/test1.zip")).build();
    }

    public static TestMassiusFiles getTestInteressatSenseNif() {

        return TestMassiusFiles.builder().csvContent(getFileContent("/es/caib/notib/logic/massiu/test_interessat_sense_nif.csv"))
                .zipContent(getFileContent("/es/caib/notib/logic/massiu/test_interessat_sense_nif.zip")).build();
    }

    private static byte[] getFileContent(String filename) {

        InputStream is =  NotificacioMassivaTests.class.getResourceAsStream(filename);
        byte[] targetArray = new byte[0];
        try {
            targetArray = new byte[is.available()];
            is.read(targetArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetArray;
    }

    @Builder
    @Getter
    public static class TestMassiusFiles{
        byte[] csvContent;
        byte[] zipContent;
    }
}
