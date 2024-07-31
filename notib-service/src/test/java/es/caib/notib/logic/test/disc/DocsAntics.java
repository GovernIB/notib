package es.caib.notib.logic.test.disc;

import org.apache.commons.lang3.time.DateUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

public class DocsAntics {

//    public static void main(String[] args) {
//
//        var llindar = -10;
//        var ara = Calendar.getInstance().getTime();
//        var dataLlindar = DateUtils.addDays(ara, llindar);
//        var directori = "";
//        try (var filePathStream = Files.walk(Paths.get(directori), 2)) {
//            filePathStream.forEach(filePath -> {
//                var file = filePath.toFile();
//                if (file.isDirectory()) {
////                    System.out.println("Saltant directori " + filePath);
//                    return;
//                }
//                try {
//                    var lastModified = new Date(file.lastModified());
//                    var proc = Runtime.getRuntime().exec("file " + filePath);
//                    var stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//                    String s = null;
//                    String output = "";
//                    while ((s = stdInput.readLine()) != null) {
//                        output = s;
//                    }
//                    if (lastModified.after(dataLlindar) || output.contains("Zip")) {
////                        System.out.println("Fitxer " + file.getName() + " NO ES COMPRIMIRA");
//                        return;
//                    }
////                    String [] comanda = {"zip -j " + filePath + ".zip " + filePath, "rm " + filePath, "mv " + filePath + ".zip " + filePath };
//				    var comanda = "zip -j " + filePath + ".zip " + filePath;
//                    proc = Runtime.getRuntime().exec(comanda);
//                    proc.waitFor();
//				    comanda = "rm " + filePath;
//                    proc = Runtime.getRuntime().exec(comanda);
//                    proc.waitFor();
//                    comanda = "mv " + filePath + ".zip " + filePath;
//                    proc = Runtime.getRuntime().exec(comanda);
//                    proc.waitFor();
//                } catch (Exception e) {
//                    System.out.println("Error executant la commanda" + e);
//                }
//            });
//        } catch (Exception ex) {
////                log.error("Error comprimint els documents antics", ex);
//        }
////            log.info("Els documents antics s'han comprimit correctament");
//    }
}
