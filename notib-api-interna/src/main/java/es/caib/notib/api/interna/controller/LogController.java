package es.caib.notib.api.interna.controller;

import es.caib.notib.logic.intf.dto.logs.FitxerContingut;
import es.caib.notib.logic.intf.dto.logs.FitxerInfo;
import es.caib.notib.logic.intf.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping
    public List<FitxerInfo> llistarFitxers() {
        return logService.llistarFitxers();
    }

    @GetMapping("/{nom}")
    public FitxerContingut getFitxerByNom(@PathVariable("nom") String nom) {
        return logService.getFitxerByNom(nom);
    }

    @GetMapping("/{nomFitxer}/linies/{nLinies}")
    public List<String> llegitUltimesLinies(@PathVariable("nLinies") Long nLinies, @PathVariable("nomFitxer") String nomFitxer) {
        return logService.readLastNLines(nomFitxer, nLinies);
    }

    @GetMapping(value = "/stream/{filename}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogFile(@PathVariable String filename, HttpServletResponse response) throws IOException {

        logService.tailLogFile(filename);

        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                while (true) {
                    String line = logService.getQueue().take(); // Block until a line is available

                    // Send the line to the client
                    emitter.send(line);
                }
            } catch (Exception e) {
                log.error("Error sending data to client: " + e.getMessage(), e);
                emitter.completeWithError(e);  // Complete emitter on error
            } finally {
                emitter.complete();  // Ensure completion happens if exiting
            }
        }).start();

        // Ensure proper cleanup on completion/timing out
        emitter.onCompletion(() -> log.info("Emitter completed."));
        emitter.onTimeout(() -> {
            log.info("Emitter timed out. Attempting to notify client to reconnect.");
            // Notify client (you could send a periodic message or keep this simple)
            try {
                emitter.send("timeout"); // Optional: Signal the client to reconnect
//                emitter.complete(); // Clean up old emitter
//                logService.getQueue().take();
            } catch (IOException e) {
                log.error("Error notifying client of timeout: " + e.getMessage());
            }
        });

//        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//        executor.scheduleAtFixedRate(() -> {
//            try {
//                emitter.send("heartbeat");
//            } catch (IOException e) {
//                log.error("Error sending heartbeat: " + e.getMessage());
//                emitter.completeWithError(e);
//            }
//        }, 0, 10, TimeUnit.SECONDS);

        return emitter; // Returns the emitter and allows the client to receive updates
    }

//    const eventSource = new EventSource('http://localhost:8082/notibback/logs/logs/stream/server.log');
//
//    eventSource.onmessage = function(event) {
//        if (event.data === "timeout") {
//            console.warn("The emitter timed out. Please reconnect.");
//            // Handle reconnection logic if necessary
//        } else {
//            console.log("", event.data);
//        }
//    };
//
//    eventSource.onerror = function(event) {
//        console.error("Error occurred, likely due to disconnection.");
//        // Reconnect logic
//        if (event.target.readyState === EventSource.CLOSED) {
//            setTimeout(() => {
//                    // Attempt to reconnect after timeout
//                    eventSource = new EventSource('notibback/logs/logs/stream/server.log');
//        }, 5000);
//        }
//    };

}
