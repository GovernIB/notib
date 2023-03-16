package es.caib.notib.core.helper;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.core.api.dto.ServeiTipusEnumDto;
import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.UsuariEntity;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class NotificaV2HelperTest {
    @Mock
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private ConversioTipusHelper conversioTipusHelper;
    @Mock
    private ProcedimentRepository procedimentRepository;
    @Mock
    private IntegracioHelper integracioHelper;
    @Mock
    private NotificacioEventHelper notificacioEventHelper;
    @Mock
    protected ConfigHelper configHelper;
    @Mock
    private EmailNotificacioHelper emailNotificacioHelper;
    @Mock
    private NotificacioRepository notificacioRepository;
    @Mock
    private NotificacioHelper notificacioHelper;
    @Mock
    private NotificacioTableHelper notificacioTableHelper;
    @Mock
    private EnviamentHelper enviamentHelper;
    @Mock
    private EnviamentTableHelper enviamentTableHelper;
    @Mock
    private CallbackHelper callbackHelper;
    @InjectMocks
    private NotificaV2Helper notificaV2Helper;

    private NotificacioEntity notificacioMock;
    private EntitatEntity entitatMock;
    private NotificacioEnviamentEntity enviamentMock;

    private final String MAX_INTENTS_CALLBACK = "10";

    private static final String NOTIFICA_WS_INFOENVIOV2_RESPONSE =
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "   <soap:Body>\n" +
            "      <ns2:resultadoInfoEnvioV2 xmlns=\"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/altaRemesaEnvios\" " +
            "                                xmlns:ns2=\"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/infoEnvioV2\" " +
            "                                xmlns:ns3=\"https://administracionelectronica.gob.es/notifica/ws/notificaws_v2/1.0/getCies\">\n" +
            "         <ns2:identificador>5af994cdbef3f</ns2:identificador>\n" +
            "         <ns2:estado>enviado_deh</ns2:estado>\n" +
            "         <ns2:concepto>PRUEBA DE NOTIFICACION CON NOTIFICA 2.1</ns2:concepto>\n" +
            "         <ns2:descripcion>PRUEBA</ns2:descripcion>\n" +
            "         <ns2:codigoOrganismoEmisor>\n" +
            "            <ns2:codigo>A05003638</ns2:codigo>\n" +
            "            <ns2:descripcionCodigoDIR>Comunidad Autónoma de Canarias</ns2:descripcionCodigoDIR>\n" +
            "         </ns2:codigoOrganismoEmisor>\n" +
            "         <ns2:tipoEnvio>Notificacion</ns2:tipoEnvio>\n" +
            "         <ns2:fechaCreacion>2018-05-14T15:53:17</ns2:fechaCreacion>\n" +
            "         <ns2:fechaPuestaDisposicion>2018-05-14T15:53:17</ns2:fechaPuestaDisposicion>\n" +
            "         <ns2:retardo>0</ns2:retardo>\n" +
            "         <ns2:procedimiento>\n" +
            "            <ns2:codigoSia>200620</ns2:codigoSia>\n" +
            "            <ns2:descripcionSia>Consulta Bibliotecas Públicas</ns2:descripcionSia>\n" +
            "         </ns2:procedimiento>\n" +
            "         <ns2:documento>  " +
            "            <ns2:contenido>JVBERi0xLjQKJcOkw7zDtsOfCjIgMCBvYmoKPDwvTGVuZ3RoIDMgMCBSL0ZpbHRlci9GbGF0ZURlY29kZT4+CnN0cmVhbQp4nDPQM1Qo5ypUMFAwALJMLU31jBQsTAz1LBSKUrnCtRTyIHJAWJTO5RTCZWoGlDI3NwEqDklR0HczVDA0UghJi7YxMLTTNbQxMDIwNjCxAzJMDczsYkO8uFxDuAK5AhUAeIcWEwplbmRzdHJlYW0KZW5kb2JqCgozIDAgb2JqCjEwMQplbmRvYmoKCjUgMCBvYmoKPDwvTGVuZ3RoIDYgMFt9fLiHmq/0AZ7INzDhGu6ccoa/MVQbEpxRuwx3HdTNGR3Kb4wdjgsheJhNnZYocbLdj4cYT4rY6GBUWlg+HhovxaIymDmdPX+m8xIIbtqBgdQMdYbaUiwi2EUtCCBBhCQujrwrhjqjbgsWHBOZYPb1UFDxA5r0hiGAwNTI0NiAwMDAwMCBuIAowMDAwMDA1NDQxIDAwMDAwIG4gCjAwMDAwMDU3NTcgDAwMDAgbiAKMDAwMDAwNjM4OCAwMDAwMCBuIAp0cmFpbGVyCjw8L1NpemUgMTQvUm9vdCAxMiAwIFIKL0luZm8gMTMgMCBSCi9JRCBbIDw3RDc0NjkzQkI2RUYyMjlBNUUwRUJDRDBCMERENTI4OT4KPDdENzQ2OTNCQjZFRjIyOUE1RTBFQkNEMEIwREQ1Mjg5PiBdCi9Eb2NDaGVja3N1bSAvOUY0OTIzNTgwRUQyMTUzODNERkE0QURGNUI5MjUxRjMKPj4Kc3RhcnR4cmVmCjY1NTgKJSVFT0YK</ns2:contenido>\n" +
            "            <ns2:hash>ZDc4N2MzNmZmYWQ2OWJjYTgwYTRkZDliZjk3YjdkZTUwYWQ2ZmQwMzkzNTM3MDkzMGQ0NDdhODBjMmYyYTJiMQ==</ns2:hash>\n" +
            "            <ns2:mime>application/pdf</ns2:mime>\n" +
            "            <ns2:size>7041</ns2:size>\n" +
            "            <ns2:opcionesDocumento>\n" +
            "               <ns2:opcion tipo=\"normalizado\">no</ns2:opcion>\n" +
            "            </ns2:opcionesDocumento>\n" +
            "         </ns2:documento>\n" +
            "         <ns2:referenciaEmisor>5af994cdbef3f</ns2:referenciaEmisor>\n" +
            "         <ns2:titular>\n" +
            "            <ns2:nif>11111111H</ns2:nif>\n" +
            "            <ns2:nombre>SAID</ns2:nombre>\n" +
            "            <ns2:apellidos>FILALI YACHOU</ns2:apellidos>\n" +
            "            <ns2:email>saidfy.platino@gmail.com</ns2:email>\n" +
            "            <ns2:telefono>922100100</ns2:telefono>\n" +
            "         </ns2:titular>\n" +
            "         <ns2:destinatarios>\n" +
            "            <ns2:destinatario>\n" +
            "               <ns2:nif>11111111H</ns2:nif>\n" +
            "               <ns2:nombre>SAID</ns2:nombre>\n" +
            "               <ns2:apellidos>FILALI YACHOU</ns2:apellidos>\n" +
            "               <ns2:email>email@email.com</ns2:email>\n" +
            "               <ns2:telefono>922100100</ns2:telefono>\n" +
            "            </ns2:destinatario>\n" +
            "         </ns2:destinatarios>\n" +
            "         <ns2:entregaDEH>\n" +
            "            <ns2:obligado>true</ns2:obligado>\n" +
            "            <ns2:codigoProcedimiento>200620</ns2:codigoProcedimiento>\n" +
            "         </ns2:entregaDEH>\n" +
            "         <ns2:datados>\n" +
            "            <ns2:datado>\n" +
            "               <ns2:fecha>2018-05-14T15:53:19</ns2:fecha>\n" +
            "               <ns2:resultado>pendiente_deh</ns2:resultado>\n" +
            "               <ns2:origen>electronico</ns2:origen>\n" +
            "               <ns2:nombreReceptor>SAID</ns2:nombreReceptor>\n" +
            "               <ns2:nifReceptor>11111111H</ns2:nifReceptor>\n" +
            "            </ns2:datado>\n" +
            "            <ns2:datado>\n" +
            "               <ns2:fecha>2018-05-14T15:53:29</ns2:fecha>\n" +
            "               <ns2:resultado>enviado_deh</ns2:resultado>\n" +
            "               <ns2:origen>electronico</ns2:origen>\n" +
            "               <ns2:nombreReceptor>SAID</ns2:nombreReceptor>\n" +
            "               <ns2:nifReceptor>11111111H</ns2:nifReceptor>\n" +
            "            </ns2:datado>\n" +
            "         </ns2:datados>\n" +
            "         <ns2:certificacion>\n" +
            "           <ns2:contenidoCertificacion>\nJVBERi0xLjQKJcOkw7zDtsOfCjIgMCBvYmoKPDwvTGVuZ3RoIDMgMCBSL0ZpbHRlci9GbGF0ZURlY29kZT4+CnN0cmVhbQp4nDPQM1Qo5ypUMFAwALJMLU31jBQsTAz1LBSKUrnCtRTyIHJAWJTO5RTCZWoGlDI3NwEqDklR0HczVDA0UghJi7YxMLTTNbQxMDIwNjCxAzJMDczsYkO8uFxDuAK5AhUAeIcWEwplbmRzdHJlYW0KZW5kb2JqCgozIDAgb2JqCjEwMQplbmRvYmoKCjUgMCBvYmoKPDwvTGVuZ3RoIDYgMFt9fLiHmq/0AZ7INzDhGu6ccoa/MVQbEpxRuwx3HdTNGR3Kb4wdjgsheJhNnZYocbLdj4cYT4rY6GBUWlg+HhovxaIymDmdPX+m8xIIbtqBgdQMdYbaUiwi2EUtCCBBhCQujrwrhjqjbgsWHBOZYPb1UFDxA5r0hiGAwNTI0NiAwMDAwMCBuIAowMDAwMDA1NDQxIDAwMDAwIG4gCjAwMDAwMDU3NTcgDAwMDAgbiAKMDAwMDAwNjM4OCAwMDAwMCBuIAp0cmFpbGVyCjw8L1NpemUgMTQvUm9vdCAxMiAwIFIKL0luZm8gMTMgMCBSCi9JRCBbIDw3RDc0NjkzQkI2RUYyMjlBNUUwRUJDRDBCMERENTI4OT4KPDdENzQ2OTNCQjZFRjIyOUE1RTBFQkNEMEIwREQ1Mjg5PiBdCi9Eb2NDaGVja3N1bSAvOUY0OTIzNTgwRUQyMTUzODNERkE0QURGNUI5MjUxRjMKPj4Kc3RhcnR4cmVmCjY1NTgKJSVFT0YK</ns2:contenidoCertificacion>\n" +
            "           <ns2:hash>ZDc4N2MzNmZmYWQ2OWJjYTgwYTRkZDliZjk3YjdkZTUwYWQ2ZmQwMzkzNTM3MDkzMGQ0NDdhODBjMmYyYTJiMQ==</ns2:hash>\n" +
            "           <ns2:fechaCertificacion>2021-09-15T16:00:00</ns2:fechaCertificacion>\n" +
            "           <ns2:origen>electronico</ns2:origen>\n" +
            "           <ns2:csv>dasd-dsadad-asdasd-asda-sda-das</ns2:csv>\n" +
            "           <ns2:mime>application/pdf</ns2:mime>\n" +
            "           <ns2:size>10081</ns2:size>\n" +
            "           <ns2:metadatos>datos_metadatos</ns2:metadatos>\n" +
            "           <ns2:opcionesCertificacion></ns2:opcionesCertificacion>\n" +
            "         </ns2:certificacion>\n" +
            "      </ns2:resultadoInfoEnvioV2>\n" +
            "    </soap:Body>\n" +
            "</soap:Envelope>\n" +
            "\n";

//    private static WireMockServer wireMockServer;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().port(8181),
            false);

    @Before
    public void setUp() throws Exception {
        Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.notifica.url"))).thenReturn("http://localhost:8181/notifica");
        Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.notifica.username"))).thenReturn("");
        Mockito.when(configHelper.getConfig(Mockito.eq("es.caib.notib.notifica.password"))).thenReturn("");

        entitatMock = Mockito.mock(EntitatEntity.class);
        Mockito.when(entitatMock.getApiKey()).thenReturn("APIKEY");

        notificacioMock =  Mockito.mock(NotificacioEntity.class);
        Mockito.when(notificacioMock.getEntitat()).thenReturn(entitatMock);
        Mockito.when(notificacioMock.getTipusUsuari()).thenReturn(TipusUsuariEnumDto.INTERFICIE_WEB);

        UsuariEntity mockUser = Mockito.mock(UsuariEntity.class);

        enviamentMock = NotificacioEnviamentEntity.builder()
                .serveiTipus(ServeiTipusEnumDto.NORMAL)
                .notificacio(notificacioMock)
                .notificaDataCreacio(new Date())
                .notificaEstat(EnviamentEstat.EXPIRADA)
                .notificaIdentificador("identificador")
                .build();
        ReflectionTestUtils.setField(enviamentMock, "id", 2L);
        ReflectionTestUtils.setField(enviamentMock, "createdBy", mockUser);

        Mockito.when(notificacioEnviamentRepository.findOne(Mockito.eq(2L))).thenReturn(enviamentMock);
        Mockito.when(pluginHelper.getConsultaReintentsPeriodeProperty()).thenReturn(3);

    }

    @Test
    public void givenEnviamentSenseCertificacio_whenEnviamentRefrescarEstat_ThenCallGestioDocumentalCreate() throws Exception {
        // Given
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/notifica"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_XML_VALUE)
                        .withBody(NOTIFICA_WS_INFOENVIOV2_RESPONSE)));

        Mockito.when(pluginHelper.gestioDocumentalCreate(Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS),
                Mockito.any(byte[].class))).thenReturn("ArxiuId");
//        Mockito.doNothing().when(notificacioHelper).auditaNotificacio(Mockito.any(NotificacioEntity.class), Mockito.<AuditService.TipusOperacio>any(), Mockito.anyString());
//        Mockito.doNothing().when(notificacioTableHelper).actualitzarRegistre(Mockito.any(NotificacioEntity.class));
//        Mockito.doNothing().when(enviamentHelper).auditaEnviament(Mockito.any(NotificacioEnviamentEntity.class), Mockito.<AuditService.TipusOperacio>any(), Mockito.anyString());
//        Mockito.doNothing().when(enviamentTableHelper).actualitzarRegistre(Mockito.any(NotificacioEnviamentEntity.class));
//        Mockito.doNothing().when(callbackHelper).crearCallback(Mockito.any(NotificacioEntity.class), Mockito.any(NotificacioEnviamentEntity.class), Mockito.anyBoolean(), Mockito.anyString());

        Assert.assertNull(enviamentMock.getNotificaCertificacioData());
        Assert.assertNull(enviamentMock.getNotificaCertificacioArxiuId());
        Assert.assertNull(enviamentMock.getNotificaCertificacioHash());
        Assert.assertNull(enviamentMock.getNotificaCertificacioOrigen());
        Assert.assertNull(enviamentMock.getNotificaCertificacioMetadades());
        Assert.assertNull(enviamentMock.getNotificaCertificacioCsv());
        Assert.assertNull(enviamentMock.getNotificaCertificacioMime());
        Assert.assertNull(enviamentMock.getNotificaCertificacioTamany());

        // When
        notificaV2Helper.enviamentRefrescarEstat(enviamentMock.getId(), true);

        // Then
        Mockito.verify(pluginHelper).gestioDocumentalCreate(
                Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS),
                Mockito.any(byte[].class)
        );

        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-09-15 16:00:00"),
                enviamentMock.getNotificaCertificacioData());
        Assert.assertEquals("ArxiuId", enviamentMock.getNotificaCertificacioArxiuId());
        Assert.assertEquals("ZDc4N2MzNmZmYWQ2OWJjYTgwYTRkZDliZjk3YjdkZTUwYWQ2ZmQwMzkzNTM3MDkzMGQ0NDdhODBjMmYyYTJiMQ==",
                enviamentMock.getNotificaCertificacioHash());
        Assert.assertEquals("electronico", enviamentMock.getNotificaCertificacioOrigen());
        Assert.assertEquals("datos_metadatos", enviamentMock.getNotificaCertificacioMetadades());
        Assert.assertEquals("dasd-dsadad-asdasd-asda-sda-das", enviamentMock.getNotificaCertificacioCsv());
        Assert.assertEquals("application/pdf", enviamentMock.getNotificaCertificacioMime());
        Assert.assertEquals((Integer) 10081, enviamentMock.getNotificaCertificacioTamany());
    }

    @Test
    public void givenEnviamentAmbCertificacioMesActual_whenEnviamentRefrescarEstat_ThenCallGestioDocumentalCreate() throws Exception {
        // Given
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/notifica"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_XML_VALUE)
                        .withBody(NOTIFICA_WS_INFOENVIOV2_RESPONSE)));

        Mockito.when(pluginHelper.gestioDocumentalCreate(Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS),
                Mockito.any(byte[].class))).thenReturn("ArxiuId");

        Assert.assertNull(enviamentMock.getNotificaCertificacioData());
        Assert.assertNull(enviamentMock.getNotificaCertificacioArxiuId());
        Assert.assertNull(enviamentMock.getNotificaCertificacioHash());
        Assert.assertNull(enviamentMock.getNotificaCertificacioOrigen());
        Assert.assertNull(enviamentMock.getNotificaCertificacioMetadades());
        Assert.assertNull(enviamentMock.getNotificaCertificacioCsv());
        Assert.assertNull(enviamentMock.getNotificaCertificacioMime());
        Assert.assertNull(enviamentMock.getNotificaCertificacioTamany());

        // L'enviament tenia un certificat anterior al actual
        enviamentMock.updateNotificaCertificacio(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-01-01 16:00:00"),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // When
        notificaV2Helper.enviamentRefrescarEstat(enviamentMock.getId(), true);

        // Then
        Mockito.verify(pluginHelper).gestioDocumentalCreate(
                Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS),
                Mockito.any(byte[].class)
        );

        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-09-15 16:00:00"),
                enviamentMock.getNotificaCertificacioData());
        Assert.assertEquals("ArxiuId", enviamentMock.getNotificaCertificacioArxiuId());
        Assert.assertEquals("ZDc4N2MzNmZmYWQ2OWJjYTgwYTRkZDliZjk3YjdkZTUwYWQ2ZmQwMzkzNTM3MDkzMGQ0NDdhODBjMmYyYTJiMQ==",
                enviamentMock.getNotificaCertificacioHash());
        Assert.assertEquals("electronico", enviamentMock.getNotificaCertificacioOrigen());
        Assert.assertEquals("datos_metadatos", enviamentMock.getNotificaCertificacioMetadades());
        Assert.assertEquals("dasd-dsadad-asdasd-asda-sda-das", enviamentMock.getNotificaCertificacioCsv());
        Assert.assertEquals("application/pdf", enviamentMock.getNotificaCertificacioMime());
        Assert.assertEquals((Integer) 10081, enviamentMock.getNotificaCertificacioTamany());
    }

    @Test
    public void givenEnviamentAmbCertificacioActual_whenEnviamentRefrescarEstat_ThenDoNothing() throws Exception {
        // Given
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/notifica"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_XML_VALUE)
                        .withBody(NOTIFICA_WS_INFOENVIOV2_RESPONSE)));

//        Mockito.when(pluginHelper.gestioDocumentalCreate(Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS),
//                Mockito.any(byte[].class))).thenReturn("ArxiuId");

        Assert.assertNull(enviamentMock.getNotificaCertificacioData());
        Assert.assertNull(enviamentMock.getNotificaCertificacioArxiuId());
        Assert.assertNull(enviamentMock.getNotificaCertificacioHash());
        Assert.assertNull(enviamentMock.getNotificaCertificacioOrigen());
        Assert.assertNull(enviamentMock.getNotificaCertificacioMetadades());
        Assert.assertNull(enviamentMock.getNotificaCertificacioCsv());
        Assert.assertNull(enviamentMock.getNotificaCertificacioMime());
        Assert.assertNull(enviamentMock.getNotificaCertificacioTamany());

        // L'enviament tenia un certificat anterior al actual
        enviamentMock.updateNotificaCertificacio(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-09-15 16:00:00"),
                "ArxiuId",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // When
        notificaV2Helper.enviamentRefrescarEstat(enviamentMock.getId(), true);

        // Then
        Mockito.verify(pluginHelper, Mockito.times(0)).gestioDocumentalCreate(
                Mockito.eq(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS),
                Mockito.any(byte[].class)
        );

        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-09-15 16:00:00"),
                enviamentMock.getNotificaCertificacioData());
//        Assert.assertNull(enviamentMock.getNotificaCertificacioArxiuId());
        Assert.assertNull(enviamentMock.getNotificaCertificacioHash());
        Assert.assertNull(enviamentMock.getNotificaCertificacioOrigen());
        Assert.assertNull(enviamentMock.getNotificaCertificacioMetadades());
        Assert.assertNull(enviamentMock.getNotificaCertificacioCsv());
        Assert.assertNull(enviamentMock.getNotificaCertificacioMime());
        Assert.assertNull(enviamentMock.getNotificaCertificacioTamany());
    }

}