package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.cie.CieDto;
import es.caib.notib.core.api.dto.cie.CieFormatSobreDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.helper.PermisosHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class PagadorCieFormatSobreServiceTest extends BaseServiceTest{

	private EntitatDto entitatCreate;
	private PermisDto permisAdmin;
	private CieDto createPagadorCie;
	private CieFormatSobreDto createPagadorCieFormatSobre;
	private CieFormatSobreDto updatePagadorCieFormatSobre;

	
	@Autowired
	PermisosHelper permisosHelper;
	
	@Before
	public void setUp() {
		addConfig("es.caib.notib.metriques.generar", "false");
		entitatCreate = new EntitatDto();
		entitatCreate.setCodi("LIMIT");
		entitatCreate.setNom("Limit Tecnologies");
		entitatCreate.setDescripcio("Descripció de Limit Tecnologies");
		entitatCreate.setTipus(EntitatTipusEnumDto.GOVERN);
		entitatCreate.setDir3Codi("23599770E");
		entitatCreate.setApiKey("123abc");
		entitatCreate.setAmbEntregaDeh(true);
//		entitatCreate.setAmbEntregaCie(true);
		TipusDocumentDto tipusDocDefault = new TipusDocumentDto();
		tipusDocDefault.setTipusDocEnum(TipusDocumentEnumDto.UUID);
		entitatCreate.setTipusDocDefault(tipusDocDefault);

		permisAdmin = new PermisDto();
		permisAdmin.setAdministration(true);
		permisAdmin.setAdministradorEntitat(true);
		permisAdmin.setTipus(TipusEnumDto.USUARI);
		permisAdmin.setPrincipal("admin");
		entitatCreate.setPermisos(Arrays.asList(permisAdmin));
		
		createPagadorCie=new CieDto();
		createPagadorCie.setOrganismePagadorCodi("A04027005");
		createPagadorCie.setContracteDataVig(new Date());
		
		createPagadorCieFormatSobre= new CieFormatSobreDto();
		createPagadorCieFormatSobre.setCodi("12345");
		
		updatePagadorCieFormatSobre= new CieFormatSobreDto();
		updatePagadorCieFormatSobre.setCodi("23456");
	}
	
	
	@Test
	public void create() {
		testCreantElements(
			new TestAmbElementsCreats() {
				@Override
				public void executar(List<Object> elementsCreats){
					CieDto pagadorCieCreada = (CieDto)elementsCreats.get(1);
					CieFormatSobreDto formatSobreCreada = (CieFormatSobreDto)elementsCreats.get(2);
					
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
					CieDto pagadorCieCreat = (CieDto)elementsCreats.get(1);
					CieFormatSobreDto formatCreat = (CieFormatSobreDto)elementsCreats.get(2);
					autenticarUsuari("admin");
					
					updatePagadorCieFormatSobre.setId(formatCreat.getId());
					CieFormatSobreDto formatModificat = pagadorCieFormatSobreService.update(
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
					CieFormatSobreDto formatCreat = (CieFormatSobreDto)elementsCreats.get(2);
					autenticarUsuari("admin");

					CieFormatSobreDto formatBorrat = pagadorCieFormatSobreService.delete(
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
					CieFormatSobreDto formatCreat = (CieFormatSobreDto)elementsCreats.get(2);
					
					CieFormatSobreDto formatTrobat = pagadorCieFormatSobreService.findById(
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
			CieFormatSobreDto original,
			CieFormatSobreDto perComprovar) {
		assertEquals(
				original.getCodi(),
				perComprovar.getCodi());
	}
	
}
	

