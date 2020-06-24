package es.caib.notib.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.notib.core.api.service.AplicacioService;

@Controller
@RequestMapping("/metrics")
public class MetricController {

	@Autowired
	private AplicacioService aplicacioService;
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String metrics(
			HttpServletRequest request) {
		return aplicacioService.getMetrics();
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String metricsView(
			HttpServletRequest request,
			Model model) {
		model.addAttribute("metriques", aplicacioService.getMetrics());
		return "util/metrics";
	}
}
