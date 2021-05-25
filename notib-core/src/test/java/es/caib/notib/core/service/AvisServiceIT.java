package es.caib.notib.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.AvisDto;
import es.caib.notib.core.api.dto.AvisNivellEnumDto;
import es.caib.notib.core.api.dto.PaginaDto;
import es.caib.notib.core.api.dto.PaginacioParamsDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.service.AvisService;
import es.caib.notib.core.repository.AvisRepository;
import es.caib.notib.core.test.AuthenticationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class AvisServiceIT {
	
	@Autowired
	AuthenticationTest authenticationTest;
	
	@Autowired
	AvisService avisService;
	
	@Autowired
	AvisRepository avisRepository;
	
	
//	private AvisDto avisDto;
	
	@Before
	public void setUp() {
	}

	@Test
	public void whenCreateAvis_thenCreateTableItem() {
		
		authenticationTest.autenticarUsuari("super");
		
		// Given
		AvisDto avisDto = new AvisDto();
		avisDto.setAssumpte("Aviso nivel Información");
		avisDto.setMissatge("Se ha desplegado una nueva versión de NOTIB");
		avisDto.setDataInici(new Date());
		SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			avisDto.setDataFinal(formatoFecha.parse("2021-06-25 00:00:00"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		avisDto.setActiu(null);
		avisDto.setAvisNivell(AvisNivellEnumDto.INFO);
		
		// When
		AvisDto avisCreated = avisService.create(avisDto);
		
		// Then
		try {
			assertNotNull(avisCreated);
			assertNotNull(avisCreated.getId());
			comprobarAvisCoincide(avisDto, avisCreated);
			assertEquals(avisCreated.getActiu(), true);
		} finally {
			// Borrado de los elementos creados
			avisService.delete(avisCreated.getId());
		}
	
	}
	
	private void comprobarAvisCoincide(AvisDto esperado, AvisDto actual) {
		assertEquals(esperado.getAssumpte(), actual.getAssumpte());
		assertEquals(esperado.getMissatge(), actual.getMissatge());
		assertEquals(esperado.getDataInici(), actual.getDataInici());
		assertEquals(esperado.getDataFinal(), actual.getDataFinal());
		assertEquals(esperado.getAvisNivell(), actual.getAvisNivell());			
	}
	
}
