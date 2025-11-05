package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.dto.logs.FitxerContingut;
import es.caib.notib.logic.intf.dto.logs.FitxerInfo;
import es.caib.notib.logic.intf.service.LogService;
import es.caib.notib.logic.intf.util.MimeUtils;
import es.caib.notib.logic.utils.ZipFileUtils;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private ConfigHelper configHelper;

    private static final Long maxNLinies = 10000L;
    private static final Long minNLinies = 100L;

    @Override
    public List<FitxerInfo> llistarFitxers() {

        var directoriPath = configHelper.getConfig("es.caib.notib.plugin.fitxer.logs.path");
        if (Strings.isNullOrEmpty(directoriPath)) {
            return new ArrayList<>();
        }
        List<FitxerInfo> fitxers = new ArrayList<>();
        var sdf = new SimpleDateFormat("dd/MM/yyyy");
        try (Stream<Path> paths = Files.list(Paths.get(directoriPath))) {
            paths.filter(Files::isRegularFile).forEach(f -> {
                var file = f.toFile();
                try {
                    var attr = Files.readAttributes(f, BasicFileAttributes.class);
                    var dataCreacio = sdf.format(new Date(attr.creationTime().toMillis()));
                    var dataModificacio = sdf.format(new Date(attr.lastModifiedTime().toMillis()));
                    var mida = file.length();
                    var fitxer = FitxerInfo.builder().nom(file.getName())
                                                     .mida(mida)
                                                     .dataCreacio(dataCreacio)
                                                     .dataModificacio(dataModificacio).build();
                    fitxers.add(fitxer);
                } catch (Exception ex) {
                    log.error("Errror obtenint la info del fitxer " + f.getFileName(), ex);
                }
            });
        } catch (Exception ex) {
            log.error("Error generant la info dels fitxers pel directori " + directoriPath, ex);
        }
        return fitxers;
    }

    @Override
    public FitxerContingut getFitxerByNom(String nom) {

        try {
            var directoriPath = configHelper.getConfig("es.caib.notib.plugin.fitxer.logs.path");
            if (Strings.isNullOrEmpty(directoriPath)) {
                return FitxerContingut.builder().build();
            }
            var filePath = Paths.get(directoriPath, nom);
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return FitxerContingut.builder().build();
            }
            var file = filePath.toFile();
            var attr = Files.readAttributes(filePath, BasicFileAttributes.class);
            var sdf = new SimpleDateFormat("dd/MM/yyyy");
            var dataCreacio = sdf.format(new Date(attr.creationTime().toMillis()));
            var dataModificacio = sdf.format(new Date(attr.lastModifiedTime().toMillis()));
            var contingut = Files.readAllBytes(filePath);
            var mime = MimeUtils.getMimeTypeFromContingut(file.getName(), contingut);
            if (MimeUtils.isTextPlain(mime)) {
                contingut = ZipFileUtils.compressFile(contingut, nom + ".zip");
                mime = MimeUtils.getMimeTypeFromContingut(file.getName(), contingut);
            }
            return FitxerContingut.builder().contingut(contingut)
                                            .mimeType(mime)
                                            .nom(file.getName())
                                            .dataCreacio(dataCreacio)
                                            .dataModificacio(dataModificacio)
                                            .mida(contingut.length).build();
        } catch (IOException ex) {
            log.error("Error reading file content for " + nom, ex);
            return FitxerContingut.builder().build();
        }
    }


    private long lastPosition = 0;
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

    @Override
    public void tailLogFile(String filePath) {

        var directoriPath = configHelper.getConfig("es.caib.notib.plugin.fitxer.logs.path");
        if (Strings.isNullOrEmpty(directoriPath)) {
            log.error("[LogService.tailLogFile] No s'ha especificat valor a la propietat \"es.caib.notib.plugin.fitxer.logs.path\"");
            return;
        }
        var path = Paths.get(directoriPath, filePath);
        new Thread(() -> {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                reader.skip(Files.size(path));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        queue.put(line);
                    } else {
                        // Sleep for a short time to avoid busy waiting
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                }

//                reader.skip(lastPosition);
//                String line;
//                while (true) {
//                    line = reader.readLine();
//                    if (line == null) {
//                        Thread.sleep(1000);
//                        continue;
//                    }
//                    queue.put(line);
//                    lastPosition += line.length() + System.lineSeparator().length();
//                }
            } catch (IOException e) {
                log.error("[LogService.tailLogFile] IOException llegint el fitxer de log: " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("[LogService.tailLogFile] Thread interrupted: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public BlockingQueue<String> getQueue() {
        return queue;
    }

    @Override
    public List<String> readLastNLines(String nomFitxer, Long nLinies) {

        try {
            if (Strings.isNullOrEmpty(nomFitxer) || nLinies == null) {
                log.error("[LogService.readLastNLines] Parametres incorrectes, nomFitxer " + nomFitxer + " nLinies" + nLinies);
                return new ArrayList<>();
            }
            var directoriPath = configHelper.getConfig("es.caib.notib.plugin.fitxer.logs.path");
            if (Strings.isNullOrEmpty(directoriPath)) {
                log.error("[LogService.nomFitxer] No s'ha especificat valor a la propietat \"es.caib.notib.plugin.fitxer.logs.path\"");
                return new ArrayList<>();
            }
            if (nLinies > maxNLinies) {
                nLinies = maxNLinies;
            } else if (nLinies < minNLinies) {
                nLinies = minNLinies;
            }
            var path = Paths.get(directoriPath, nomFitxer);
            try (var file = new RandomAccessFile(path.toFile(), "r")) {
                var fileLength = file.length();
                LinkedList<String> lines = new LinkedList<>();
                var pointer = fileLength - 1;
                var currentLine = new StringBuilder();
                char ch;
                while (pointer >= 0 && lines.size() < nLinies) {
                    file.seek(pointer);
                    ch = (char) file.readByte();
                    if (ch == '\n') {
                        if (currentLine.length() > 0) {
                            lines.addFirst(currentLine.reverse().toString());
                            currentLine.setLength(0);
                        }
                    } else {
                        currentLine.append(ch);
                    }
                    pointer--;
                }
                // Add the last line if present
                if (currentLine.length() > 0) {
                    lines.addFirst(currentLine.reverse().toString());
                }
                return lines;
            }
        } catch (Exception ex) {
            log.error("[LogService.readLastNLines] Error no controlat", ex);
            return new ArrayList<>();
        }
    }
}
