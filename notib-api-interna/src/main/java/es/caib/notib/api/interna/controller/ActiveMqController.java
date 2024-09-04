package es.caib.notib.api.interna.controller;

import es.caib.notib.logic.intf.service.ActiveMqService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ActiveMqController {

    private final ActiveMqService activeMqService;

    @GetMapping("/queues")
    public List<String> getQueues() {
        try {
            return activeMqService.getQueues();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting queue information!");
        }
    }

//    @PostMapping("/queues")
//    public String createQueue(@RequestParam String name) {
//        try {
//            activeMqService.createQueue(name);
//            return "Queue created: " + name;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error creating queue!");
//        }
//    }
//
//    @DeleteMapping("/queues")
//    public String deleteQueue(@RequestParam String name) {
//        try {
//            activeMqService.deleteQueue(name);
//            return "Queue deleted: " + name;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error deleting queue!");
//        }
//    }

    @GetMapping("/queues/{name}")
    public String getQueueDetails(@PathVariable String name) {
        try {
            return activeMqService.getQueueDetails(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting queue details!");
        }
    }

    @PostMapping("/kahadb/compact")
    public String compactKahaDB() {
        try {
            activeMqService.compactKahaDB();
            return "KahaDB compacted successfully";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error compacting KahaDB!");
        }
    }

}
