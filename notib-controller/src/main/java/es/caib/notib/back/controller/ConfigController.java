package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.ConfigCommand;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.logic.intf.dto.config.ConfigGroupDto;
import es.caib.notib.logic.intf.service.ConfigService;
import es.caib.notib.logic.intf.service.EntitatService;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador per a la gestió de la configuració de l'aplicació.
 * Només accessible amb el rol de superusuari.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/config")
public class ConfigController extends BaseUserController{

    @Autowired
    private ConfigService configService;
    @Autowired
    private EntitatService entitatService;


    @GetMapping
    public String get(HttpServletRequest request, Model model) {

        List<EntitatDto> entitats = new ArrayList<>();
        if (RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
            entitats = entitatService.findAll();
        }
        var configGroups = configService.findAll();
        var pluginGroup = configGroups.stream().filter(x -> "PLUGINS".equals(x.getKey())).collect(Collectors.toList());
        configGroups.addAll(pluginGroup.get(0).getInnerConfigs());
        model.addAttribute("config_groups", configGroups);
        for (var cGroup: configGroups) {
            fillFormsModel(cGroup, model, entitats);
        }
        return "config";
    }

    @ResponseBody
    @GetMapping(value = "/entitat/{key}")
    public List<ConfigDto> getEntitatConfigByKey(HttpServletRequest request, @PathVariable String key, Model model) {

        try {
            return configService.findEntitatsConfigByKey(key.replace("-", "."));
        } catch (Exception ex) {
            log.error("Error obtinguent les configuracions d'entitat per la key " + key, ex);
            return new ArrayList<>();
        }
    }

    @ResponseBody
    @PostMapping(value="/update")
    public SimpleResponse updateConfig(HttpServletRequest request, Model model, @Valid ConfigCommand configCommand, BindingResult bindingResult) {

        var errorMsg = "config.controller.edit.error";
        if (bindingResult.hasErrors()) {
            return SimpleResponse.builder().status(0).message(getMessage(request, errorMsg)).build();
        }
        var msg = "config.controller.edit.ok";
        var status = 1;
        var isErrorDesc = false;
        try {
            var c = configService.updateProperty(configCommand.asDto());
            msg = c == null ? errorMsg : msg;
            status = c == null ? 0 : status;
            if (c != null && "error".equals(c.getKey())) {
                isErrorDesc = true;
                msg = c.getDescription();
                status = 0;
            }
        } catch (Exception ex) {
            status = 0;
            log.error(errorMsg, ex);
        }
        return SimpleResponse.builder().status(status).message(isErrorDesc ? msg : getMessage(request, msg)).build();
    }

    @ResponseBody
    @GetMapping(value="/sync")
    public SyncResponse sync(HttpServletRequest request, Model model) {

        try {
            var editedProperties = configService.syncFromJBossProperties();
            return SyncResponse.builder().status(true).editedProperties(editedProperties).build();
        } catch (Exception e) {
            return SyncResponse.builder().status(false).build();
        }
    }

    private void fillFormsModel(ConfigGroupDto cGroup, Model model, List<EntitatDto> entitats){

        List<ConfigDto> confs = new ArrayList<>();
        for (var config: cGroup.getConfigs()) {
            if (!Strings.isNullOrEmpty(config.getEntitatCodi())) {
                continue;
            }
            model.addAttribute("config_" + config.getKey().replace('.', '_'), ConfigCommand.builder().key(config.getKey()).value(config.getValue()).build());
            for (var entitat : entitats) {
                config.addEntitatKey(entitat);
            }
            confs.add(config);
        }
        cGroup.setConfigs(confs);
        if (cGroup.getInnerConfigs() == null || cGroup.getInnerConfigs().isEmpty()){
            return;
        }
        for (var child : cGroup.getInnerConfigs()){
            fillFormsModel(child, model, entitats);
        }
    }

    @Builder @Getter
    public static class SyncResponse {
        private boolean status;
        private List<String> editedProperties;
    }

    @Builder @Getter
    public static class SimpleResponse {
        private int status;
        private String message;
    }
}
