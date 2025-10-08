package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.service.AplicacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/metrics")
public class MetricController {

	@Autowired
	private AplicacioService aplicacioService;
	
	@GetMapping
	@ResponseBody
	public String metrics(HttpServletRequest request) {
		return aplicacioService.getMetrics();
	}
	
	@GetMapping(value = "/list")
	public String metricsView(HttpServletRequest request, Model model) {

		model.addAttribute("metriques", aplicacioService.getMetrics());
		return "util/metrics";
	}
}
