package es.caib.notib.war.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class ApiController extends BaseUserController {

	
//	@RequestMapping(value = "/rest")
//	public String altaForm(
//			HttpServletRequest request) {
//		return "redirect:apidoc";
//	}
}