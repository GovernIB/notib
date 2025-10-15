package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.dto.logs.FitxerContingut;
import es.caib.notib.logic.intf.dto.logs.FitxerInfo;
import es.caib.notib.logic.intf.service.LogService;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private ConfigHelper configHelper;

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
                    double sizeMB = file.length() / 1048576.0; //(1024.0 * 1024.0)
                    var fitxer = FitxerInfo.builder().nom(file.getName()).mida(sizeMB + "MB").dataCreacio(dataCreacio).dataModificacio(dataModificacio).build();
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
            var directoriPath = configHelper.getConfig("es.caib.notib.plugin.fitxer.logs.path"); // Change this to your directory path
            if (Strings.isNullOrEmpty(directoriPath)) {
                return FitxerContingut.builder().build();
            }
            var filePath = Paths.get(directoriPath, nom);
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return FitxerContingut.builder().build();
            }
            var contingut = Files.readAllBytes(filePath);
            return FitxerContingut.builder().continugt(contingut).mida(contingut.length + "").build();
        } catch (IOException ex) {
            log.error("Error reading file content for " + nom, ex);
            return FitxerContingut.builder().build();
        }
    }
}
