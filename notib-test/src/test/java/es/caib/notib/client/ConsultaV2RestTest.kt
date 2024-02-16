package es.caib.notib.client

import es.caib.notib.client.domini.Idioma
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class ConsultaV2RestTest {

    private var client: NotificacioRestClientV2? = null
    private val keystorePath: String = ConsultaV2RestTest::class.java.getResource("/es/caib/notib/client/truststore.jks").path

    @BeforeEach
    fun setUp() {
        System.setProperty("javax.net.ssl.trustStore", keystorePath)
        System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies")

        client = NotificacioRestClientFactory.getRestClientV2(
            URL,
            USERNAME,
            PASSWORD,
            false,
        )
    }

    companion object {
        private const val URL = "https://se.caib.es/notibapi"
        private const val USERNAME = "\$carpeta_notib"
        private const val PASSWORD = "carpeta_notib"
    }

    /**
     * Test checks the comunicacionsByTitular method of NotificacioRestClientV2 for a specific titular
     */
    @Test
    fun comunicacionsByTitularTestCorrectValues() {
        val result = client?.comunicacionsByTitular("18225486x", aYearAgo(), Date(), true, Idioma.CA, 1, 5)

        println(">>> Resultat: ${result}")
        assertNotNull(result)
    }

    @Test
    fun notificacionsByTitularTestCorrectValues() {
        val result = client?.notificacionsByTitular("18225486x", aYearAgo(), Date(), true, Idioma.CA, 1, 5)

        println(">>> Resultat: ${result}")
        assertNotNull(result)
    }

    @Test
    fun comunicacionsPendentsByTitularTestCorrectValues() {
        val result = client?.comunicacionsPendentsByTitular("18225486x", aYearAgo(), Date(), true, Idioma.CA, 1, 5)

        println(">>> Resultat: ${result}")
        assertNotNull(result)
    }

    @Test
    fun notificacionsPendentsByTitularTestCorrectValues() {
        val result = client?.notificacionsPendentsByTitular("18225486x", aYearAgo(), Date(), true, Idioma.CA, 1, 5)

        println(">>> Resultat: ${result}")
        assertNotNull(result)
    }

    @Test
    fun comunicacionsLlegidesByTitularTestCorrectValues() {
        val result = client?.comunicacionsLlegidesByTitular("18225486x", aYearAgo(), Date(), true, Idioma.CA, 1, 5)

        println(">>> Resultat: ${result}")
        assertNotNull(result)
    }

    @Test
    fun notificacionsLlegidesByTitularTestCorrectValues() {
        val result = client?.notificacionsLlegidesByTitular("18225486x", aYearAgo(), Date(), true, Idioma.CA, 1, 5)

        println(">>> Resultat: ${result}")
        assertNotNull(result)
    }

    @Test
    fun getDocument() {
        val result = client?.getDocument("3620821")

        println(">>> Resultat: ${result}")
        assertNotNull(result)
    }

    @Test
    fun getJustificant() {
        val result = client?.getJustificant("3620824")

        println(">>> Resultat: ${result}")
        assertNotNull(result)
    }

    @Test
    fun getCertificacio() {
        val result = client?.getCertificacio("3620824")

        println(">>> Resultat: ${result}")
        assertNotNull(result)
    }

    fun aYearAgo(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -1)
        return calendar.time
    }

}