package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.ConfigHelper;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.comanda.ms.log.helper.LogHelper;
import es.caib.comanda.model.v1.log.FitxerContingut;
import es.caib.notib.logic.intf.service.LogService;
import es.caib.comanda.model.v1.log.FitxerInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
        return LogHelper.llistarFitxers(directoriPath, "notib");
    }

    @Override
    public es.caib.comanda.model.v1.log.FitxerContingut getFitxerByNom(String nom) {


        try {
            var directoriPath = configHelper.getConfig("es.caib.notib.plugin.fitxer.logs.path");
            if (Strings.isNullOrEmpty(directoriPath)) {
                return es.caib.comanda.model.v1.log.FitxerContingut.builder().build();
            }
            return LogHelper.getFitxerByNom(directoriPath, nom);
        } catch (Exception ex) {
            log.error("[LogService.getFitxerByNom] Error llegint el fitxer " + nom, ex);
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
            return LogHelper.readLastNLines(directoriPath, nomFitxer, nLinies);
        } catch (Exception ex) {
            log.error("[LogService.readLastNLines] Error no controlat", ex);
            return new ArrayList<>();
        }
    }
}