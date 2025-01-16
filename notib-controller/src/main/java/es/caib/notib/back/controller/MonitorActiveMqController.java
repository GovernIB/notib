/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.logic.intf.dto.ActiveMqInfo;
import es.caib.notib.logic.intf.dto.AplicacioDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.service.ActiveMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/monitor/activemq")
public class MonitorActiveMqController extends BaseController {

	@Autowired
	private ActiveMqService activeMqService;

	@GetMapping
	public String get(HttpServletRequest request, Model model) {

		return "monitorActiveMQ";
	}

	@GetMapping(value = "/datatable")
	@ResponseBody
	public DatatablesHelper.DatatablesResponse datatable(HttpServletRequest request) {

		var params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		PaginaDto<ActiveMqInfo> queues = activeMqService.getInfoQueues(params);
		return DatatablesHelper.getDatatableResponse(request, queues);
	}

	@GetMapping(value = "/missatges/{queueNom}")
	public String missatges(HttpServletRequest request, @PathVariable String queueNom, Model model) {

		model.addAttribute("queueNom", queueNom);
		return "monitorActiveMqMissatges";
	}

	@GetMapping(value = "/missatges/{queueNom}/datatable")
	@ResponseBody
	public DatatablesHelper.DatatablesResponse missatgesDatatable(HttpServletRequest request, @PathVariable String queueNom) {

		var params = DatatablesHelper.getPaginacioDtoFromRequest(request);
		PaginaDto<ActiveMqInfo> apps = activeMqService.getInfoQueues(params);
		activeMqService.getMessages(queueNom);
		return DatatablesHelper.getDatatableResponse(request, apps);
	}

	@GetMapping("/queues")
	public List<String> getQueues() {
		try {
			return activeMqService.getQueues();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error getting queue information!");
		}
	}


	@GetMapping("/queues/{name}")
	public String getQueueDetails(@PathVariable String name) {
		try {
			return activeMqService.getQueueDetails(name);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error getting queue details!");
		}
	}

}
