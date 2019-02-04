package es.caib.notib.core.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import es.caib.notib.core.api.dto.RegistreAnotacioDto;
import es.caib.notib.core.api.exception.RegistrePluginException;
import es.caib.notib.core.api.service.RegistreService;
import es.caib.notib.core.helper.PluginHelper;

@Service
public class RegistreServiceImpl implements RegistreService{

	@Resource
	private PluginHelper pluginHelper;
	
	@Override
	public void registrarSortida(RegistreAnotacioDto registreAnotacio) {
		try {
			pluginHelper.registrarSortida(registreAnotacio, "notib", "0.1");
		} catch (RegistrePluginException e) {
			e.printStackTrace();
		}
	}
}
