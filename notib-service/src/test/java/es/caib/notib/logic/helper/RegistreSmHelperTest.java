package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.statemachine.mappers.EnviamentRegistreMapper;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.UsuariEntity;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;


//@RunWith(MockitoJUnitRunner.class)
@RunWith(MockitoJUnitRunner.Silent.class)
public class RegistreSmHelperTest {

    @Mock
    private ConfigHelper configHelper;
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private NotificacioEventHelper notificacioEventHelper;
    @Mock
    private AuditHelper auditHelper;
    @Mock
    private EnviamentTableHelper enviamentTableHelper;
    @Mock
    private CallbackHelper callbackHelper;
    @Mock
    private IntegracioHelper integracioHelper;
    @Mock
    private EnviamentRegistreMapper enviamentRegistreMapper;
    @InjectMocks
    private RegistreSmHelper registreSmHelper;

    private NotificacioEntity notificacio;
    private ProcedimentEntity procediment;
    private OrganGestorEntity organ;
    private NotificacioEnviamentEntity enviament;
    private EntitatEntity entitat;
    private RegistreSmHelper.ReqAssentamentRegistral request;
    private RespostaConsultaRegistre arbResposta;
    private AsientoRegistralBeanDto asientoRegistral;
    private Date dataResposta;
    private String organCodi;
    private String versio;
    private String concepte;
    private Long tipusRegistre;
    private EnviamentTipus enviamentTipus;

    @Before
    public void setUp() throws Exception {

        organCodi = "A04026953";
        versio = "3.1";
        tipusRegistre = 2L;
        concepte = "Test";
        entitat = initEntitat();
        request = Mockito.mock(RegistreSmHelper.ReqAssentamentRegistral.class);
        Mockito.when(request.getTipusOperacio()).thenReturn(2L);
        initProcOrgan();
        initNotificacio();
        initEnviament();
        initAsientoRegistral();
        initReposta();
//        Mockito.when(enviament.getTitular()).thenReturn(titular);
        Mockito.when(pluginHelper.getRegistreReintentsMaxProperty()).thenReturn(3);
//        Mockito.when(
//                pluginHelper.crearAsientoRegistral(
//                request.getDir3CodiRegistre(),
//                enviamentRegistreMapper.toAsientoRegistral(enviament),
//                request.getTipusOperacio(),
//                enviament.getNotificacio().getId(),
//                enviament.getId().toString(),
//                true)
////               pluginHelper.crearAsientoRegistral(Mockito.anyString(), Mockito.any(AsientoRegistralBeanDto.class), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.eq(true))
//        ).thenReturn(arbResposta);
//
        Mockito.when(pluginHelper.crearAsientoRegistral(
                        Mockito.anyString(),
                        Mockito.any(AsientoRegistralBeanDto.class),
                        Mockito.nullable(long.class),
                        Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(arbResposta);

    }

    private void initAsientoRegistral() {

        asientoRegistral = Mockito.mock(AsientoRegistralBeanDto.class);
        Mockito.when(asientoRegistral.getAplicacion()).thenReturn("RWE");
        Mockito.when(asientoRegistral.getAplicacionTelematica()).thenReturn("NOTIB v.2.0.1");
//        Mockito.when(asientoRegistral.getCodigoSia()).thenReturn();
        Mockito.when(asientoRegistral.getUnidadTramitacionDestinoCodigo()).thenReturn(organCodi);
        Mockito.when(asientoRegistral.getUnidadTramitacionDestinoDenominacion()).thenReturn(organCodi);
        Mockito.when(asientoRegistral.getUnidadTramitacionOrigenCodigo()).thenReturn(organCodi);
        Mockito.when(asientoRegistral.getUnidadTramitacionOrigenDenominacion()).thenReturn(organCodi);
        Mockito.when(asientoRegistral.getResumen()).thenReturn(enviamentTipus + " - " + concepte);
        Mockito.when(asientoRegistral.getVersion()).thenReturn(versio);
        Mockito.when(asientoRegistral.getTipoRegistro()).thenReturn(tipusRegistre);
        Mockito.when(enviamentRegistreMapper.toAsientoRegistral(enviament)).thenReturn(asientoRegistral);
    }

    private void initProcOrgan() {

        organ =  Mockito.mock(OrganGestorEntity.class);
        Mockito.when(organ.getCodi()).thenReturn(organCodi);
        procediment =  Mockito.mock(ProcedimentEntity.class);
        Mockito.when(procediment.getOrganGestor()).thenReturn(organ);
    }

    private void initNotificacio() {

        notificacio =  Mockito.mock(NotificacioEntity.class);
        enviamentTipus = EnviamentTipus.NOTIFICACIO;
        Mockito.when(notificacio.getId()).thenReturn(1L);
        Mockito.when(notificacio.getEntitat()).thenReturn(entitat);
        Mockito.when(notificacio.getUsuariCodi()).thenReturn("admin");
        Mockito.when(notificacio.getProcediment()).thenReturn(procediment);
        Mockito.when(notificacio.getConcepte()).thenReturn(concepte);
        var usuari = Mockito.mock((UsuariEntity.class));
        notificacio.setCreatedBy(usuari);
        Mockito.when(notificacio.getCreatedBy()).thenReturn(Optional.of(usuari));
        Mockito.when(usuari.getCodi()).thenReturn("1");
    }

    private void initEnviament() {

        enviament = Mockito.mock(NotificacioEnviamentEntity.class);
        Mockito.when(enviament.getId()).thenReturn(1L);
        Mockito.when(enviament.getNotificacio()).thenReturn(notificacio);
        Mockito.when(enviament.getNotificacio()).thenReturn(notificacio);
    }

    private void initReposta() {

        dataResposta = new Date();
        arbResposta = Mockito.mock(RespostaConsultaRegistre.class);
        Mockito.when(arbResposta.getErrorCodi()).thenReturn(null);
        Mockito.when(arbResposta.getEstat()).thenReturn(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT);
        Mockito.when(arbResposta.getRegistreNumeroFormatat()).thenReturn("170097/2023");
        Mockito.when(arbResposta.getRegistreNumero()).thenReturn("170097");
        Mockito.when(arbResposta.getRegistreData()).thenReturn(dataResposta);
        Mockito.when(arbResposta.getSirRegistreDestiData()).thenReturn(dataResposta);
    }

    @Test
    public void check_parametres_entrada_sortida_notificacio() throws Exception {

        // Given
        enviamentTipus = EnviamentTipus.NOTIFICACIO;
        Mockito.when(notificacio.getEnviamentTipus()).thenReturn(enviamentTipus);
        Mockito.when(configHelper.getConfigAsBoolean("es.caib.notib.emprar.sir")).thenReturn(false);

        // When
        var resposta = registreSmHelper.registrarEnviament(enviament, 1);

        // Then
        assertTrue(resposta);
        assertEquals("170097",arbResposta.getRegistreNumero());
        assertEquals("170097/2023",arbResposta.getRegistreNumeroFormatat());
        assertEquals(dataResposta,arbResposta.getRegistreData());
        assertEquals(dataResposta,arbResposta.getSirRegistreDestiData());
        assertEquals(asientoRegistral.getUnidadTramitacionDestinoCodigo(), organ.getCodi());
        assertEquals(asientoRegistral.getUnidadTramitacionDestinoDenominacion(), organ.getCodi());
        assertEquals(asientoRegistral.getUnidadTramitacionOrigenCodigo(), organ.getCodi());
        assertEquals(asientoRegistral.getUnidadTramitacionOrigenDenominacion(), organ.getCodi());
        assertEquals(asientoRegistral.getResumen(), enviamentTipus + " - " + notificacio.getConcepte());
        assertEquals(asientoRegistral.getVersion(), "3.1");
        assertEquals(asientoRegistral.getTipoRegistro(), tipusRegistre);

    }

    @Test
    public void check_parametres_entrada_sir() throws Exception {

        // Given
        Mockito.when(notificacio.getEnviamentTipus()).thenReturn(enviamentTipus);
        Mockito.when(configHelper.getConfigAsBoolean("es.caib.notib.emprar.sir")).thenReturn(true);

        // When
        var resposta = registreSmHelper.registrarEnviament(enviament, 1);

        // Then
        assertTrue(resposta);
        assertEquals("170097", arbResposta.getRegistreNumero());
        assertEquals("170097/2023", arbResposta.getRegistreNumeroFormatat());
        assertEquals(dataResposta, arbResposta.getRegistreData());
        assertEquals(dataResposta,arbResposta.getSirRegistreDestiData());
        assertEquals(asientoRegistral.getUnidadTramitacionDestinoCodigo(), organ.getCodi());
        assertEquals(asientoRegistral.getUnidadTramitacionDestinoDenominacion(), organ.getCodi());
        assertEquals(asientoRegistral.getUnidadTramitacionOrigenCodigo(), organ.getCodi());
        assertEquals(asientoRegistral.getUnidadTramitacionOrigenDenominacion(), organ.getCodi());
        assertEquals(asientoRegistral.getResumen(), enviamentTipus + " - " + notificacio.getConcepte());
        assertEquals(asientoRegistral.getVersion(), "3.1");
        assertEquals(asientoRegistral.getTipoRegistro(), tipusRegistre);
    }

    private EntitatEntity initEntitat() {

        return EntitatEntity.getBuilder("codi",
                        "nom",
                        null,
                        "dir3Codi",
                        "dir3CodiReg",
                        "apiKey",
                        false,
                        //				false,
                        null,
                        null,
                        "colorFons",
                        "colorLletra",
                        null,
                        "oficina",
                        "nomOficinaVirtual",
                        false,
                        "llibre",
                        "llibreNom",
                        false)
                .build();
    }
}
