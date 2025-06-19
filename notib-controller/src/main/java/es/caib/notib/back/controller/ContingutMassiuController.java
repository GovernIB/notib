package es.caib.notib.back.controller;

import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@Slf4j
@Controller
@RequestMapping("/accions/massives")
public class ContingutMassiuController {


    @GetMapping
    public String get(HttpServletRequest request, Model model) {

        return "consultaAccionsMassives";
    }

    @GetMapping(value = "/datatable")
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request ) {

        return DatatablesHelper.getDatatableResponse(request, new ArrayList<>());
    }


}
