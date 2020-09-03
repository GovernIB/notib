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
import es.caib.notib.core.api.dto.PagadorCieFormatSobreDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.TipusDocumentDto;
import es.caib.notib.core.api.dto.TipusDocumentEnumDto;
import es.caib.notib.core.api.dto.TipusEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.helper.PermisosHelper;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class PagadorCieFormatSobreServiceTest extends BaseServiceTest{

	private EntitatDto entitatCreate;
	private PermisDto permisAdmin;
	private PagadorCieDto createPagadorCie;
	private PagadorCieFormatSobreDto createPagadorCieFormatSobre;
	private PagadorCieFormatSobreDto updatePagadorCieFormatSobre;

	
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
		
		createPagadorCieFormatSobre= new PagadorCieFormatSobreDto();
		createPagadorCieFormatSobre.setCodi("12345");
		
		updatePagadorCieFormatSobre= new PagadorCieFormatSobreDto();
		updatePagadorCieFormatSobre.setCodi("23456");
	}
	
	
	@Test
	public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats){
					PagadorCieDto pagadorCieCreada = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatSobreDto formatSobreCreada = (PagadorCieFormatSobreDto)elementsCreats.get(2);
					
					assertNotNull(formatSobreCreada);
					assertNotNull(formatSobreCreada.getId());
					comprobarPagadorCieFormatSobre(
							createPagadorCieFormatSobre,
							formatSobreCreada);
					assertEquals(pagadorCieCreada.getId(), formatSobreCreada.getPagadorCieId());
				}
			}, 
			"Create FORMAT SOBRE",
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatSobre);
	}
	
	
	@Test
	public void update() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					PagadorCieDto pagadorCieCreat = (PagadorCieDto)elementsCreats.get(1);
					PagadorCieFormatSobreDto formatCreat = (PagadorCieFormatSobreDto)elementsCreats.get(2);
					autenticarUsuari("admin");
					
					updatePagadorCieFormatSobre.setId(formatCreat.getId());
					PagadorCieFormatSobreDto formatModificat = pagadorCieFormatSobreService.update(
							updatePagadorCieFormatSobre);
					
					assertNotNull(formatModificat);
					assertNotNull(formatModificat.getId());
					assertEquals(
							formatCreat.getId(), 
							formatModificat.getId());
					
					comprobarPagadorCieFormatSobre(
							updatePagadorCieFormatSobre,
							formatModificat);
					assertEquals(pagadorCieCreat.getId(), formatModificat.getPagadorCieId());
				}
			},
			"Update FORMAT SOBRE",
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatSobre);
	}


	@Test
	public void delete() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats) throws NotFoundException{
					PagadorCieFormatSobreDto formatCreat = (PagadorCieFormatSobreDto)elementsCreats.get(2);
					autenticarUsuari("admin");

					PagadorCieFormatSobreDto formatBorrat = pagadorCieFormatSobreService.delete(
							formatCreat.getId());
					comprobarPagadorCieFormatSobre(
							createPagadorCieFormatSobre,
							formatBorrat);
					try {						
						pagadorCieFormatSobreService.findById(formatCreat.getId());
						fail("El format esborrat no s'hauria d'haver trobat");												
					}catch(NotFoundException expected) {
					}
					elementsCreats.remove(formatCreat);
				}
			},
			"Delete FORMAT SOBRE",
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatSobre);
	}
				
	
	@Test
	public void findById() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats)throws NotFoundException{
					autenticarUsuari("admin");
					PagadorCieFormatSobreDto formatCreat = (PagadorCieFormatSobreDto)elementsCreats.get(2);
					
					PagadorCieFormatSobreDto formatTrobat = pagadorCieFormatSobreService.findById(
							formatCreat.getId());
					
					assertNotNull(formatTrobat);
					assertNotNull(formatTrobat.getId());
					comprobarPagadorCieFormatSobre(
							createPagadorCieFormatSobre,
							formatTrobat);
				}
			},
			"FindById FORMAT SOBRE",
			entitatCreate,
			createPagadorCie,
			createPagadorCieFormatSobre);
	}
	
	private void comprobarPagadorCieFormatSobre(
			PagadorCieFormatSobreDto original,
			PagadorCieFormatSobreDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
	}
	
}
	

