package es.caib.notib.war.helper;

public class FileHelper {

    public static boolean isPdf(String docBase64) {

        String formatsPdf = "JVBERi0"; //PDF
        return docBase64.startsWith(formatsPdf);
    }

}
