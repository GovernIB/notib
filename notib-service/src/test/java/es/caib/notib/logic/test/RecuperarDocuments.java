package es.caib.notib.logic.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class RecuperarDocuments {

    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    public static void main(String [] args) throws IOException {


        var path = "";
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(path);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line);
                var split = line.split("/");
                if (split.length != 2) {
                    continue;
                }
                toDate(split[1]);
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }
    }

    public static void toDate(String timestamp) {

        var date = new Date(Long.parseLong(timestamp));
        System.out.println(sf.format(date));
    }
}
