package es.caib.notib.logic.service;

import es.caib.comanda.salut.model.AppInfo;
import es.caib.comanda.salut.model.DetallSalut;
import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.comanda.salut.model.IntegracioApp;
import es.caib.comanda.salut.model.IntegracioInfo;
import es.caib.comanda.salut.model.IntegracioSalut;
import es.caib.comanda.salut.model.MissatgeSalut;
import es.caib.comanda.salut.model.SalutInfo;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.service.SalutService;
import es.caib.notib.logic.mapper.MissatgeSalutMapper;
import es.caib.notib.logic.utils.NotibBenchmark;
import es.caib.notib.persist.repository.AvisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalutServiceImpl implements SalutService {

    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;
    private final PluginHelper pluginHelper;
    private final MissatgeSalutMapper missatgeSalutMapper;
    private final AvisRepository avisRepository;

    @Override
    public List<IntegracioInfo> getIntegracions() {
        return List.of(
                IntegracioInfo.builder().integracioApp(IntegracioApp.CAR).build(),
                IntegracioInfo.builder().integracioApp(IntegracioApp.AFI).build(),
                IntegracioInfo.builder().integracioApp(IntegracioApp.PFI).build(),
                IntegracioInfo.builder().integracioApp(IntegracioApp.RSC).build(),
                IntegracioInfo.builder().integracioApp(IntegracioApp.DIR).build(),
                IntegracioInfo.builder().integracioApp(IntegracioApp.ARX).build(),
                IntegracioInfo.builder().integracioApp(IntegracioApp.REG).build(),
                IntegracioInfo.builder().integracioApp(IntegracioApp.NTF).build(),
                IntegracioInfo.builder().integracioApp(IntegracioApp.USR).build(),
                IntegracioInfo.builder().integracioApp(IntegracioApp.EML).build()
        );
    }

    @Override
    public List<AppInfo> getSubsistemes() {
        return List.of(
                AppInfo.builder().codi("AWE").nom("Alta web").build(),
                AppInfo.builder().codi("ARE").nom("Alta REST").build(),
                AppInfo.builder().codi("MAS").nom("Alta massiva").build(),
                AppInfo.builder().codi("REG").nom("Registre").build(),
                AppInfo.builder().codi("SIR").nom("SIR").build(),
                AppInfo.builder().codi("NOT").nom("Notificació").build(),
                AppInfo.builder().codi("CBK").nom("Callback de client").build(),
                AppInfo.builder().codi("CIE").nom("CIE").build()
        );
    }

    @Override
    public SalutInfo checkSalut(String versio, String performanceUrl) {

        var estatSalut = checkEstatSalut(performanceUrl);   // Estat
        var salutDatabase = checkDatabase();                // Base de dades
        var integracions = checkIntegracions();             // Integracions
        var subsistemes = checkSubsistemes();               // Subsistemes
        var altres = checkAltres();                         // Altres
        var missatges = checkMissatges();                   // Missatges

        return SalutInfo.builder()
                .codi("NOT")
                .versio(versio)
                .data(new Date())
                .estat(estatSalut)
                .bd(salutDatabase)
                .integracions(integracions)
                .subsistemes(subsistemes)
                .altres(altres)
                .missatges(missatges)
                .build();
    }

    @Override
    public EstatSalut executePerformanceTest() {

        Options opt = new OptionsBuilder()
                .include(NotibBenchmark.class.getSimpleName())
                .timeUnit(TimeUnit.MILLISECONDS)
                .forks(1)
                .build();

        try {
            var runResults = new Runner(opt).run();

            // Processar els resultats
            DoubleSummaryStatistics stats = runResults.stream()
                    .mapToDouble(result -> result.getPrimaryResult().getScore())
                    .summaryStatistics();

            return EstatSalut.builder()
                    .estat(EstatSalutEnum.UP)
                    .latencia(Math.round(stats.getAverage()))
                    .build();
        } catch (RunnerException e) {
            throw new RuntimeException(e);
        }

    }

    private EstatSalut checkEstatSalut(String performanceUrl) {

        try {
            Instant start = Instant.now();
            var response = restTemplate.getForObject(performanceUrl, String.class);
            Instant end = Instant.now();
            long latency = Duration.between(start, end).toMillis();

            return EstatSalut.builder()
                    .estat(EstatSalutEnum.UP)
                    .latencia(latency)
                    .build();
        } catch (Exception e) {
            return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
        }
    }

    private EstatSalut checkDatabase() {

        try {
            Instant start = Instant.now();
            jdbcTemplate.execute("SELECT ID FROM NOT_ENTITAT WHERE ID = 1");
            Instant end = Instant.now();

            return EstatSalut.builder()
                    .estat(EstatSalutEnum.UP)
                    .latencia(Duration.between(start, end).toMillis())
                    .build();
        } catch (Exception e) {
            return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
        }
    }

    private List<IntegracioSalut> checkIntegracions() {

        try {
            return pluginHelper.getPeticionsPluginsAndReset();
        } catch (Exception e) {
            return null;
        }
    }

    public List<SalutInfo> checkSubsistemes() {

        try {

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public List<DetallSalut> checkAltres() {

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        // Nombre de cores (CPU)
        var availableProcessors = osBean.getAvailableProcessors();
        var os = osBean.getName() + " " + osBean.getVersion() + " (" + osBean.getArch() + ")";

        try {

            var systemCpuLoad = "No disponible";
            var processCpuLoad = "No disponible";

            loadSigarNativeLibs();
            Sigar sigar = new Sigar();

            // Informació sobre CPU
            CpuPerc cpu = sigar.getCpuPerc();
            systemCpuLoad = CpuPerc.format(cpu.getCombined());
            processCpuLoad = CpuPerc.format(sigar.getProcCpu(sigar.getPid()).getPercent());
            // Informació sobre Memòria
            Mem memory = sigar.getMem();
            // Informació sobre Disc
            var totalSpace = 0L;
            var freeSpace = 0L;
            FileSystem[] fileSystems = sigar.getFileSystemList();
            for (FileSystem fs : fileSystems) {
                if (fs.getDirName().equals("/")) {
                    FileSystemUsage usage = sigar.getFileSystemUsage(fs.getDirName());
                    totalSpace = usage.getTotal();
                    freeSpace = usage.getFree();
                    break;
                }
            }

            System.out.println("CPU User Time: " + CpuPerc.format(cpu.getUser()));
            System.out.println("CPU System Time: " + CpuPerc.format(cpu.getSys()));
            System.out.println("CPU Idle Time: " + CpuPerc.format(cpu.getIdle()));
            System.out.println("CPU Combined: " + CpuPerc.format(cpu.getCombined()));

            return List.of(
                    DetallSalut.builder().codi("PRC").nom("Processadors").valor(String.valueOf(Runtime.getRuntime().availableProcessors())).build(),
                    DetallSalut.builder().codi("CPU").nom("Càrrega del sistema").valor(systemCpuLoad).build(),
                    DetallSalut.builder().codi("CPU").nom("Càrrega del procés").valor(processCpuLoad).build(),
                    DetallSalut.builder().codi("MED").nom("Memòria disponible").valor(humanReadableByteCount(memory.getFree())).build(),
                    DetallSalut.builder().codi("MET").nom("Memòria total").valor(humanReadableByteCount(memory.getTotal())).build(),
                    DetallSalut.builder().codi("EDT").nom("Espai de disc total").valor(humanReadableByteCount(totalSpace)).build(),
                    DetallSalut.builder().codi("EDL").nom("Espai de disc lliure").valor(humanReadableByteCount(freeSpace)).build(),
                    DetallSalut.builder().codi("SO").nom("Sistema operatiu").valor(os).build());

        } catch (Exception e) {
            log.error("No s'ha pogut obtenir informació del sistema utilitzant la llibreria Sigar", e);
            try {
                // Càrrega de la CPU (només per la implementació de Sun)
                if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                    com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
                    var systemCpuLoad = sunOsBean.getSystemCpuLoad() * 100 + "%";
                    var processCpuLoad = sunOsBean.getProcessCpuLoad() * 100 + "%";

                    var totalSpace = 0L;
                    var freeSpace = 0L;
                    for (var root : File.listRoots()) {
                        if (root.getTotalSpace() > totalSpace) {
                            totalSpace = root.getTotalSpace();
                            freeSpace = root.getFreeSpace();
                        }
                    }

                    return List.of(
                            DetallSalut.builder().codi("PRC").nom("Processadors").valor(String.valueOf(Runtime.getRuntime().availableProcessors())).build(),
                            DetallSalut.builder().codi("CPU").nom("Càrrega del sistema").valor(systemCpuLoad).build(),
                            DetallSalut.builder().codi("CPU").nom("Càrrega del procés").valor(processCpuLoad).build(),
                            DetallSalut.builder().codi("MED").nom("Memòria disponible").valor((Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Ilimitada" : humanReadableByteCount(Runtime.getRuntime().maxMemory()))).build(),
                            DetallSalut.builder().codi("MET").nom("Memòria total").valor(humanReadableByteCount(Runtime.getRuntime().totalMemory())).build(),
                            DetallSalut.builder().codi("EDT").nom("Espai de disc total").valor(humanReadableByteCount(totalSpace)).build(),
                            DetallSalut.builder().codi("EDL").nom("Espai de disc lliure").valor(humanReadableByteCount(freeSpace)).build(),
                            DetallSalut.builder().codi("SO").nom("Sistema operatiu").valor(os).build()
                    );
                }
            } catch (Exception e2) {
                log.error("Salut: No s'ha pogut obtenir informació del sistema amb la implementació de Sun", e2);
            }
            return null;
        }
    }

    public List<MissatgeSalut> checkMissatges() {

        List<MissatgeSalut> missatges = new ArrayList<>();
        try {
            var avisos = avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE));
            if (avisos != null && !avisos.isEmpty()) {
                avisos.forEach(avis -> {
                    missatges.add(missatgeSalutMapper.toMissatgeSalut(avis));
                });
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }


    // Helpers

    public static String humanReadableByteCount(long bytes) {

        var unit = 1000;
        if (bytes < unit) {
            return bytes + " B";
        }
        var exp = (int) (Math.log(bytes) / Math.log(unit));
        var pre = "kMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void loadSigarNativeLibs() throws Exception {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        var osName = osBean.getName().toLowerCase();
        var osArch64 = osBean.getArch().contains("64");

        String nativeLibPath = "es/caib/notib/logic/sigar/natives/";

        if (osName.contains("win")) {
            nativeLibPath += osArch64 ? "windows/sigar-amd64-winnt.dll" : "windows/sigar-x86-winnt.dll";
        } else if (osName.contains("linux")) {
            nativeLibPath += osArch64 ? "linux/libsigar-amd64-linux.so" : "linux/libsigar-x86-linux.so";
        } else if (osName.contains("mac")) {
            nativeLibPath += osArch64 ? "macos/libsigar-universal64-macosx.dylib" : "macos/libsigar-universal-macosx.dylib";
        } else {
            throw new UnsupportedOperationException("OS not supported for Sigar natives: " + osName);
        }

        // Copia la llibreria nativa a un directori temporal
        InputStream in = SalutServiceImpl.class.getClassLoader().getResourceAsStream(nativeLibPath);
        if (in == null) {
            throw new RuntimeException("Failed to load native library from path: " + nativeLibPath);
        }

        File tempLib = File.createTempFile("sigar", nativeLibPath.substring(nativeLibPath.lastIndexOf('.')));
        Files.copy(in, tempLib.toPath(), StandardCopyOption.REPLACE_EXISTING);
        in.close();

        // Afegir el directori temporal al java.library.path
        System.load(tempLib.getAbsolutePath());
    }
}
