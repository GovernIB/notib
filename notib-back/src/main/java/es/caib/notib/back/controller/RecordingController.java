package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/recording")
public class RecordingController extends BaseController {

    @Autowired
    private AplicacioService aplicacioService;

    @GetMapping
    public String get(HttpServletRequest request, Model model) {

        model.addAttribute("isRecording", aplicacioService.isRecording());
        return "recording";
    }

    @GetMapping("/download")
    @ResponseBody
    public void downloadJFR(HttpServletResponse response) throws Exception {

        var arxiu = aplicacioService.getRecordingFile();
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(arxiu.getNom(), arxiu.getContingut(), response);
    }

    @GetMapping(value = "/start")
    @ResponseBody
    public String startJFR(Model model) {

        var recording = aplicacioService.startRecording();
        model.addAttribute("isRecording", recording);
        return "Started";
    }

    @GetMapping(value = "/stop")
    @ResponseBody
    public String stopJFR(Model model) {
        try {
            var recording = aplicacioService.stopRecording();
            model.addAttribute("isRecording", recording);
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