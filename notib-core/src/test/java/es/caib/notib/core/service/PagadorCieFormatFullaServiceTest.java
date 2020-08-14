package es.caib.notib.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.EntitatTipusEnumDto;
import es.caib.notib.core.api.dto.PagadorCieDto;
import es.caib.notib.core.api.dto.PagadorCieFormatFullaDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.helper.PermisosHelper;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional

public class PagadorCieFormatFullaServiceTest extends BaseServiceTest{

	private EntitatDto entitatCreate;
	private PermisDto permisAdmin;
	private PagadorCieDto createPagadorCie;
	private PagadorCieFormatFullaDto createPagadorCieFormatFulla;
	private PagadorCieFormatFullaDto updatePagadorCieFormatFulla;
	

	@Autowired
	PermisosHelper permisosHelper;
	
	@Before
	public void setUp() {
		entitatCreate = new EntitatDto();
		entitatCreate.setCodi("LIMIT");
		entitatCreate.setNom("Limit Tecnologies");
		entitatCreate.setDescripcio("Descripció de Limit Tecnologies");
		entitatCreate.setTipus(EntitatTipusEnumDto.GOVERN);
		entitatCreate.setDir3Codi("23599770E");
		entitatCreate.setApiKey("123abc");
		entitatCreate.setAmbEntregaDeh(true);
		entitatCreate.setAmbEntregaCie(true);
		TipusDocumentDto tipusDocDefault = new TipusDocumentDto();
		tipusDocDefault.setTipusDocEnum(TipusDocumentEnumDto.UUID);
		entitatCreate.setTipusDocDefault(tipusDocDefault);
		
		permisAdmin = new PermisDto();
		permisAdmin.setAdministration(true);
		permisAdmin.setAdministradorEntitat(true);
		permisAdmin.setTipus(TipusEnumDto.USUARI);
		permisAdmin.setPrincipal("admin");
		entitatCreate.setPermisos(Arrays.asList(permisAdmin));
		
		createPagadorCie=new PagadorCieDto();
		createPagadorCie.setDir3codi("A04027005");
		createPagadorCie.setContracteDataVig(new Date());
		
		createPagadorCieFormatFulla=new PagadorCieFormatFullaDto();
		createPagadorCieFormatFulla.setCodi("122");
		
		updatePagadorCieFormatFulla=new PagadorCieFormatFullaDto();
		updatePagadorCieFormatFulla.setCodi("12333");
	}
	
	
	@Test
	public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws Exception {
					PagadorCieDto pagadorCieCreat = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatFullaDto formatFullaCreada = (PagadorCieFormatFullaDto)elementsCreats.get(2);
					
					assertNotNull(formatFullaCreada);
					assertNotNull(formatFullaCreada.getId());
					comprobarPagadorCieFormatFulla(
							createPagadorCieFormatFulla,
							formatFullaCreada);
					assertEquals(pagadorCieCreat.getId(), formatFullaCreada.getPagadorCieId());
				}
			}, 
			"Create FORMAT FULLA", 
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatFulla);
	}
	
	
	
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					PagadorCieDto pagadorCieCreat = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatFullaDto formatCreat = (PagadorCieFormatFullaDto)elementsCreats.get(2);
					autenticarUsuari("admin");

					updatePagadorCieFormatFulla.setId(formatCreat.getId());
					PagadorCieFormatFullaDto formatModificat = pagadorCieFormatFullaService.update(
							updatePagadorCieFormatFulla);	
					
					assertNotNull(formatModificat);
					assertNotNull(formatModificat.getId());
					assertEquals(
							formatCreat.getId(), 
							formatModificat.getId());
					
					comprobarPagadorCieFormatFulla(
							updatePagadorCieFormatFulla,
							formatModificat);
					assertEquals(pagadorCieCreat.getId(), formatModificat.getPagadorCieId());
				}
			},
			"Update FORMAT FULLA",
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatFulla);
	}
	
	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					PagadorCieFormatFullaDto formatCreat = (PagadorCieFormatFullaDto)elementsCreats.get(2);
					autenticarUsuari("admin");

					PagadorCieFormatFullaDto formatBorrat = pagadorCieFormatFullaService.delete(
							formatCreat.getId());
					comprobarPagadorCieFormatFulla(
							createPagadorCieFormatFulla,
							formatBorrat);
					try {						
						pagadorCieFormatFullaService.findById(formatCreat.getId());
						fail("El format esborrat no s'hauria d'haver trobat");												
					}catch(NotFoundException expected) {
					}
					elementsCreats.remove(formatCreat);
				}
			},
			"Delete FORMAT FULLA",
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatFulla);
	}
				
	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					autenticarUsuari("admin");
					PagadorCieFormatFullaDto formatCreat = (PagadorCieFormatFullaDto)elementsCreats.get(2);
					
					PagadorCieFormatFullaDto formatTrobat = pagadorCieFormatFullaService.findById(
							formatCreat.getId());
					
					assertNotNull(formatTrobat);
					assertNotNull(formatTrobat.getId());
					comprobarPagadorCieFormatFulla(
							createPagadorCieFormatFulla,
							formatTrobat);
				}
			},
			"FindById FORMAT FULLA",
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatFulla);
	}

	private void comprobarPagadorCieFormatFulla(
			PagadorCieFormatFullaDto original,
			PagadorCieFormatFullaDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
	}

}

