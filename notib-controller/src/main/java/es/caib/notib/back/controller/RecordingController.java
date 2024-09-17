package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/recording")
public class RecordingController {

    @Autowired
    private AplicacioService aplicacioService;

    @GetMapping
    public String get(HttpServletRequest request, Model model) {
        return "recording";
    }

    @GetMapping(value = "/start")
    @ResponseBody
    public String startJFR() {
        aplicacioService.startRecording();
        return "Started";
    }

    @GetMapping(value = "/stop")
    @ResponseBody
    public String stopJFR() {
        try {
            aplicacioService.stopRecording();
            return "Stopped";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error stopping JFR recording: " + e.getMessage();
        }
    }

    @GetMapping(value = "/info")
    @ResponseBody
    public String analyzeJFR() {
        try {
            return aplicacioService.analyzeRecording();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error analyzing JFR recording: " + e.getMessage();
        }
    }
}