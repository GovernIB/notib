package es.caib.notib.plugin.usuari;

import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class DadesUsuariPluginJdbcTest {

    @Test
    public void testGetEstatPluginUp() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.dades.usuari.jdbc.query", "select usu_codi, usu_nom, usu_nif, usu_codi||'@limit.es' from sc_wl_usuari where usu_codi=:codi");

        // Configuració del DataSource
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@localhost:49161:xe"); // Reemplaça amb la teva URL
        dataSource.setUsername("seycon"); // Reemplaça amb el teu usuari
        dataSource.setPassword("seycon"); // Reemplaça amb la teva contrasenya
        dataSource.setMinIdle(1);
        dataSource.setMaxTotal(5);

        DadesUsuariPluginJdbc dadesUsuariPluginJdbc = new DadesUsuariPluginJdbc(properties, false) {
            @Override
            protected DataSource getDataSource() {
                return dataSource;
            }
        };

        EstatSalut estatSalut = dadesUsuariPluginJdbc.getEstatPlugin();

        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.UP, estatSalut.getEstat());
        assertTrue(estatSalut.getLatencia() >= 0);
    }

    @Test
    public void testGetEstatPluginDown() {
        Properties properties = new Properties();
        properties.setProperty("es.caib.notib.plugin.dades.usuari.jdbc.query", "SELECT * FROM Users WHERE codi=?");

        DadesUsuariPluginJdbc dadesUsuariPluginJdbc = new DadesUsuariPluginJdbc(properties, false);

        EstatSalut estatSalut = dadesUsuariPluginJdbc.getEstatPlugin();

        assertNotNull(estatSalut);
        assertEquals(EstatSalutEnum.DOWN, estatSalut.getEstat());
    }
}