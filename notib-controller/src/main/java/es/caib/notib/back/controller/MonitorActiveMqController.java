/**
 * 
 */
package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.service.ActiveMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/monitor/activemq")
public class MonitorActiveMqController extends BaseController {

	@Autowired
	private ActiveMqService activeMqService;

	@GetMapping
	public String get(HttpServletRequest request, @PathVariable Long entitatId, Model model) {

		return "monitorActiveMq";
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
