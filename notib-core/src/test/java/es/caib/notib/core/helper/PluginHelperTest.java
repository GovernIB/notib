package es.caib.notib.core.helper;

import es.caib.notib.plugin.conversio.ConversioPlugin;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.notib.plugin.gesconadm.GestorContingutsAdministratiuPlugin;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.RegistrePlugin;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import es.caib.notib.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PluginHelperTest {
    @Mock
    private DadesUsuariPlugin dadesUsuariPlugin;
    @Mock
    private GestioDocumentalPlugin gestioDocumentalPlugin;
    @Mock
    private RegistrePlugin registrePlugin;
    @Mock
    private IArxiuPlugin arxiuPlugin;
    @Mock
    private UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin;
    @Mock
    private GestorContingutsAdministratiuPlugin gestorDocumentalAdministratiuPlugin;
    @Mock
    private ConversioPlugin conversioPlugin;
    @Mock
    private FirmaServidorPlugin firmaServidorPlugin;
    @Mock
    private IntegracioHelper integracioHelper;
    @Mock
    private ConversioTipusHelper conversioTipusHelper;
    @Mock
    private ConfigHelper configHelper;

    @InjectMocks
    private PluginHelper pluginHelper;

    @Before
    public void setUp() throws Exception {
        Mockito.when(configHelper.getAsInt(Mockito.eq("es.caib.notib.plugin.registre.segons.entre.peticions"))).thenReturn(secondsBetweenCalls);
    }

    static int secondsBetweenCalls = 2;

    @Test
    public void whenObtenirJustificant_thenWaitForNextCall() {

        RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
        resposta.setErrorCodi(null);
        resposta.setErrorDescripcio("respostaMock");
        Mockito.when(registrePlugin.obtenerJustificante(Mockito.anyString(),
                Mockito.anyString(), Mockito.anyLong())).thenReturn(resposta);
        // Given
        String codiDir3Entitat = "A00000000";
        String numeroRegistreFormatat = "9874";

        // When
        pluginHelper.obtenirJustificant(codiDir3Entitat, numeroRegistreFormatat);

        // Then

        long iniTime = System.nanoTime();
        long lastTime;
        double timeSpend;
        RespostaJustificantRecepcio resposta2;
        do {
            resposta2 = pluginHelper.obtenirJustificant(codiDir3Entitat, numeroRegistreFormatat);
            lastTime = System.nanoTime();
            timeSpend = (lastTime - iniTime) / 1e9;
        }while (resposta2.getErrorDescripcio() == null && timeSpend < secondsBetweenCalls + 2);

        Assert.assertTrue(timeSpend >= secondsBetweenCalls);
    }
}