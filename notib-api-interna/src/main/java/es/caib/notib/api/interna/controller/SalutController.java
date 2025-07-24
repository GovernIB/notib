package es.caib.notib.api.interna.controller;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.notib.logic.intf.service.SalutService;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@RequiredArgsConstructor
@RestController
public class SalutController {

    private final ServletContext servletContext;
    private final SalutService salutService;

    private ManifestInfo manifestInfo;

    @GetMapping("/appInfo")
    public AppInfo appInfo(HttpServletRequest request) throws IOException {

        var manifestInfo = getManifestInfo();
        return AppInfo.builder()
                .codi("NOT")
                .nom("Notib")
                .data(manifestInfo.getBuildDate())
                .versio(manifestInfo.getVersion())
                .integracions(salutService.getIntegracions())
                .subsistemes(salutService.getSubsistemes())
                .contexts(salutService.getContexts(getBaseUrl(request)))
                .build();
    }

    public String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null) // elimina el context path "/comandaapi/..."
                .build()
                .toUriString();
    }

    @GetMapping("/salut")
    public SalutInfo health(HttpServletRequest request) throws IOException {

        var manifestInfo = getManifestInfo();
        return salutService.checkSalut(manifestInfo.getVersion(), request.getRequestURL().toString() + "Performance");
    }

    @GetMapping("/salutPerformance")
    public String healthCheck() {
        return "OK";
    }

    private ManifestInfo getManifestInfo() throws IOException {

        if (manifestInfo == null) {
            manifestInfo = buildManifestInfo();
        }

        return manifestInfo;
    }

    private ManifestInfo buildManifestInfo() throws IOException {

        ManifestInfo manifestInfo = ManifestInfo.builder().build();
        var manifest = new Manifest(servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME));
        var manifestAtributs = manifest.getMainAttributes();
        Map<String, Object>manifestAtributsMap = new HashMap<>();
        for (var key: new HashMap<>(manifestAtributs).keySet()) {
            manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
        }
        if (!manifestAtributsMap.isEmpty()) {
            var version = manifestAtributsMap.get("Implementation-Version");
            var buildDate = manifestAtributsMap.get("Build-Timestamp");
            var buildJDK = manifestAtributsMap.get("Build-Jdk-Spec");
            var buildScmBranch = manifestAtributsMap.get("Implementation-SCM-Branch");
            var buildScmRevision = manifestAtributsMap.get("Implementation-SCM-Revision");
            manifestInfo = ManifestInfo.builder()
                    .version(version != null ? version.toString() : null)
                    .buildDate(buildDate != null ? getDate(buildDate.toString()) : null)
                    .buildJDK(buildJDK != null ? buildJDK.toString() : null)
                    .buildScmBranch(buildScmBranch != null ? buildScmBranch.toString() : null)
                    .buildScmRevision(buildScmRevision != null ? buildScmRevision.toString() : null)
                    .build();
        }
        return manifestInfo;
    }

    public static Date getDate(String isoDate) {

        try {
            Instant instant = Instant.parse(isoDate);
            return Date.from(instant);
        } catch (DateTimeParseException e) {
            System.out.println("El format de la data és incorrecte: " + e.getMessage());
            return null;
        }
    }

    @Builder
    @Getter
    public static class ManifestInfo {

        private final String version;
        private final Date buildDate;
        private final String buildJDK;
        private final String buildScmBranch;
        private final String buildScmRevision;
    }
}
