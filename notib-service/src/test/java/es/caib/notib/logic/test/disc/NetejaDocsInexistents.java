package es.caib.notib.logic.test.disc;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class NetejaDocsInexistents {

    static Map<String, String> idsArrel = new HashMap<>();
    static Map<String, Map<String, String>> directoris = new HashMap<>();
    static String directori;
    static List<String> rutaFitxers = new ArrayList<>();
    static List<String> subDirectoris = new ArrayList<>();
    static Date dataLlindar;
    static List<Path> perEsborrar = new ArrayList<>();
    static String dirDesti;
    static String rutaFitxersDuplicatsArxiu;

    public static void main(String [] args) throws IOException, ParseException {

        if (args == null || args.length < 4) {
            System.out.println("Nombre de parametres incorrecte");
            return;
        }
        var sdf = new SimpleDateFormat("dd/MM/yyyy");
        dataLlindar = sdf.parse(args[0]);
        directori = args[1];
        var argNum = 2;
        while (argNum < args.length-1) {
            if ("duplicatsArxiu".equals(args[argNum+1])) {
                rutaFitxersDuplicatsArxiu = args[argNum];
                argNum += 2;
                continue;
            }
            rutaFitxers.add(args[argNum]);
            subDirectoris.add(args[argNum+1]);
            argNum += 2;
        }
        if (rutaFitxers.size() != subDirectoris.size()) {
            System.out.println("Els parametres de les rutes dels fitxers i els subdirectoris no son iguals.");
            return;
        }
        sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        dirDesti = directori + "perEsborrar" + sdf.format(new Date());
        Runtime.getRuntime().exec("mkdir " + dirDesti);
        for (var i = 0; i < rutaFitxers.size(); i++) {
            llegirFitxerDocumentsExistents(rutaFitxers.get(i));
            detectarDocumentsInexistents(subDirectoris.get(i));
            moureFitxersNoExistents();
        }
        moureFitxersExistentsArxiu();
    }

    private static void moureFitxersExistentsArxiu() throws IOException {

        var inputStream = new FileInputStream(rutaFitxersDuplicatsArxiu);
        var sc = new Scanner(inputStream, "UTF-8");
        String linia;
        try {
            while (sc.hasNextLine()) {
                linia = sc.nextLine();
                idsArrel.put(directori + linia, directori + linia);
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
        try (var filePathStream = Files.walk(Paths.get(directori), 1)) {
            filePathStream.forEach(filePath -> {
                var file = filePath.toFile();
                if (file.isDirectory()) {
//                    System.out.println("Saltant directori " + filePath);
                    return;
                }
                if (!idsArrel.containsKey(filePath.toString())) {
                    return;
                }
                var comanda = "mv " + filePath + " " + dirDesti + "/";
                try {
                    Runtime.getRuntime().exec(comanda);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void moureFitxersNoExistents() throws IOException {

        String comanda;
        String desti = "";
        for (var fitxer : perEsborrar) {
            comanda = "mv " + fitxer + " " + dirDesti;
            for (var subDirectori : subDirectoris) {
                if (fitxer.toString().contains(subDirectori)) {
                    desti = "/" + subDirectori + "/";
                    break;
                }
            }
            desti += fitxer.getParent().getFileName() + "/";
            Runtime.getRuntime().exec("mkdir -p " + dirDesti + desti);
            comanda += "/" + desti;
            Runtime.getRuntime().exec(comanda);
        }
    }

    private static void detectarDocumentsInexistents(String ruta) throws IOException {

        var path = directori + ruta;
        System.out.println("Detectant els fitxers NO existents a Notib");
        perEsborrar = new ArrayList<>();
        List<Path> total = new ArrayList<>();
        try (var filePathStream = Files.walk(Paths.get(path))) {
            filePathStream.forEach(filePath -> {
                var file = filePath.toFile();
                if (file.isDirectory()) {
//                    System.out.println("Saltant directori " + filePath);
                    return;
                }
//                    System.out.println(filePath);
                var dirNom = filePath.getParent().getFileName().toString();
                var fitxerNom = filePath.getFileName().toString();
                var fitxers = directoris.get(dirNom);
                total.add(filePath);
                if (fitxers == null || fitxers.containsKey(fitxerNom) || file.lastModified() > dataLlindar.getTime()) {
                    return;
                }
//                    System.out.println("Fitxer per esborrar: " + filePath);
                perEsborrar.add(filePath);
            });
        }
        System.out.println("num fitxers per esborrar: " + perEsborrar.size());
        System.out.println("num total de fitxers: " + total.size());
    }

    private static void llegirFitxerDocumentsExistents(String path) throws IOException {

        System.out.println("Llegint el fitxer " + path);
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(path);
            sc = new Scanner(inputStream, "UTF-8");
            String line;

            Map<String, String> map;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
//                line = line.replace("\"", "");
//                System.out.println(line);
                var split = line.split("/");
                if (split.length < 2) {
//                    idsArrel.put(line, line);
                    continue;
                }
                if (directoris.containsKey(split[0])) {
                    directoris.get(split[0]).put(split[1], split[1]);
                    continue;
                }
                map = new HashMap<>();
                map.put(split[1], split[1]);
                directoris.put(split[0], map);
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
}
