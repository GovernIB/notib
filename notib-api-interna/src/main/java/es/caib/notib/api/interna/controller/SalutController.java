package es.caib.notib.api.interna.controller;

import es.caib.comanda.model.server.monitoring.AppInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.SalutService;
import es.caib.notib.logic.intf.util.DatesUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/salut")
public class SalutController {

    private final ServletContext servletContext;
    private final SalutService salutService;
    private final AplicacioService aplicacioService;
    private final MeterRegistry meterRegistry;

    private ManifestInfo manifestInfo;
    private final AtomicReference<LatenciaSnapshot> darreraLatenciaSnapshot = new AtomicReference<>(new LatenciaSnapshot(0, 0));

	@PreAuthorize("hasRole('NOT_COM')")
    @GetMapping("/info")
    public AppInfo appInfo(HttpServletRequest request) throws IOException {

        var manifestInfo = getManifestInfo();
        return new AppInfo()
                .codi("NOT")
                .nom("Notib")
                .data(DatesUtils.toOffsetDateTime(manifestInfo.getBuildDate()))
                .versio(manifestInfo.getVersion())
                .revisio(manifestInfo.getBuildScmRevision())
                .jdkVersion(manifestInfo.getBuildJDK())
                .integracions(salutService.getIntegracions())
                .subsistemes(salutService.getSubsistemes())
                .contexts(salutService.getContexts(getBaseUrl(request)))
                .versioJboss(MonitorHelper.getApplicationServerInfo());
    }

    public String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null) // elimina el context path "/comandaapi/..."
                .build()
                .toUriString();
    }

    @GetMapping
    public SalutInfo health(HttpServletRequest request) throws IOException {

        var manifestInfo = getManifestInfo();
        return salutService.checkSalut(manifestInfo.getVersion(), calcularLatenciaHttpMs());
    }

    /**
     * Latència mitjana de les peticions HTTP ateses per aquesta aplicació (mètrica
     * "http.server.requests" de Micrometer) des de l'anterior consulta de salut, enlloc
     * de fer una crida HTTP a un endpoint propi només per mesurar-ne el temps de resposta.
     * Els comptadors de Micrometer són acumulatius des de l'arrencada, així que es
     * guarda l'últim valor llegit per calcular només la diferència ("delta") del període.
     */
    private Long calcularLatenciaHttpMs() {

        long totalCount = 0;
        long totalTimeNanos = 0;
        for (Timer timer : meterRegistry.find("http.server.requests").timers()) {
            totalCount += timer.count();
            totalTimeNanos += (long) timer.totalTime(TimeUnit.NANOSECONDS);
        }

        var anterior = darreraLatenciaSnapshot.getAndSet(new LatenciaSnapshot(totalCount, totalTimeNanos));
        long deltaCount = totalCount - anterior.count;
        long deltaTimeNanos = totalTimeNanos - anterior.totalTimeNanos;

        return deltaCount > 0 ? TimeUnit.NANOSECONDS.toMillis(deltaTimeNanos) / deltaCount : null;
    }

    private static final class LatenciaSnapshot {

        private final long count;
        private final long totalTimeNanos;

        private LatenciaSnapshot(long count, long totalTimeNanos) {
            this.count = count;
            this.totalTimeNanos = totalTimeNanos;
        }
    }

    private ManifestInfo getManifestInfo() throws IOException {

        if (manifestInfo == null) {
            manifestInfo = buildManifestInfo();
        }

        return manifestInfo;
    }

    @GetMapping("/metriques")
    public String metriques(HttpServletRequest request) throws Exception {

        return aplicacioService.getMetriques();
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
