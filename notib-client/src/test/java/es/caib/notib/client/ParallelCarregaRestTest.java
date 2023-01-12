package es.caib.notib.client;

import es.caib.notib.client.domini.DadesConsulta;
import es.caib.notib.client.domini.DocumentV2;
import es.caib.notib.client.domini.EntregaDeh;
import es.caib.notib.client.domini.Enviament;
import es.caib.notib.client.domini.EnviamentReferenciaV2;
import es.caib.notib.client.domini.EnviamentTipusEnum;
import es.caib.notib.client.domini.InteressatTipusEnumDto;
import es.caib.notib.client.domini.NotificaServeiTipusEnumDto;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.Persona;
import es.caib.notib.client.domini.RespostaAltaV2;
import es.caib.notib.client.domini.RespostaConsultaDadesRegistreV2;
import es.caib.notib.client.domini.RespostaConsultaEstatEnviamentV2;
import es.caib.notib.client.domini.RespostaConsultaEstatNotificacioV2;
import es.caib.notib.client.domini.RespostaConsultaJustificantEnviament;
import lombok.Builder;
import lombok.Data;
import lombok.Synchronized;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ParallelCarregaRestTest {

//    private static final String URL = "https://dev.caib.es/notib";
    private static final String URL = "http://localhost:8280/notib";
//    private static final String USERNAME = "$ripea_notib";
    private static final String USERNAME = "admin";
//    private static final String PASSWORD = "ripea_notib";
    private static final String PASSWORD = "admin";

    private static Map<Integer, String> notificacions = new HashMap<>();
    private static Map<Integer, String> enviaments = new HashMap<>();
    private static int notificacionsCount = 0;
    private static int enviamentsCount = 0;
    private static final Random random = new Random();

    public static String getNotificacio() {
//        return notificacionsCount < 50 ? enviaments.get(notificacionsCount) : enviaments.get(random.nextInt(50));
        return notificacionsCount < 50 ? notificacions.get(notificacionsCount) : notificacions.get(random.nextInt(50));
    }
    public static void setNotificacio(String referencia) {
        notificacions.put(notificacionsCount < 50 ? notificacionsCount++ : random.nextInt(50), referencia);
    }

    public static String getEnviament() {
        return enviamentsCount < 50 ? enviaments.get(enviamentsCount) : enviaments.get(random.nextInt(50));
    }
    public static void setEnviament(String referencia) {
        enviaments.put(enviamentsCount < 50 ? enviamentsCount++ : random.nextInt(50), referencia);
    }


    @Test
    public void rullAllTests() {
        Class<?>[] classes = {
                ParallelCreacioTest.class,
                ParallelConsultaNotificacioTest.class,
                ParallelConsultaEnviamentTest.class,
                ParallelConsultaRegistreTest.class,
                ParallelConsultaCertificacioTest.class};

        JUnitCore.runClasses(new ParallelComputer(true, true), classes);
    }

    public static class ParallelCreacioTest {

        private static final String uuid = "8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9";
        private static final String csv = "f0cf70121eaa28506dc3f3981546a997991e568d07efff7775f2ab7ce3ec0977";
        private static Long counter = 0L;
        private static String arxiuContingut;
        private NotificacioRestClientV2 client;

        @Synchronized
        public Long getCounter() {
            return counter++;
        }

        @BeforeClass
        public static void classSetUp() throws Exception {
            arxiuContingut = Base64.encodeBase64String(IOUtils.toByteArray(ParallelCreacioTest.class.getResourceAsStream("/es/caib/notib/client/notificacio_adjunt.pdf")));
        }

        @Before
        public void setUp() throws Exception {

            String keystorePath = ClientRestTest.class.getResource("/es/caib/notib/client/truststore.jks").toURI().getPath();
            System.setProperty("javax.net.ssl.trustStore", keystorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");
            client = NotificacioRestClientFactory.getRestClientV2(URL, USERNAME, PASSWORD, false);
        }

        @Test
        public void testCarga0() {

            InfoNot infoNot = InfoNot.builder()
                    .identificador(String.format("%1$10s", getCounter().toString()).replace(' ', '0'))
                    .entitatDir3Codi("A04003003")
                    .organDir3Codi("A04035965")
                    .procedimentCodi("874510")
                    .enviamentTipus(EnviamentTipusEnum.NOTIFICACIO)
                    .usuariCodi("e18225486x")
//                    .docUuid("8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9")
                    .destinataris(Arrays.asList(new Persona[][]{
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Andreu").llinatge1("Adrover").llinatge2("Amoros").nif("11111111H").telefon("611111111").email("usuari1@limit.es").build(),
                                Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Bernat").llinatge1("Berga").llinatge2("Balcells").nif("22222222J").telefon("622222222").email("usuari2@limit.es").build() },
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Colau").llinatge1("Cladera").llinatge2("Cerda").nif("33333333P").telefon("633333333").email("usuari3@limit.es").build() }}))
                    .build();

            String id;
            int index;
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 0");
                id = infoNot.getIdentificador();
                index = id.lastIndexOf("_");
                id = (index != -1 ? id.substring(0, index) : id) + "_" + i;
                infoNot.setIdentificador(id);
                notifica(infoNot);
            }
        }

        @Test
        public void testCarga1() {

            InfoNot infoNot = InfoNot.builder()
                    .identificador(String.format("%1$10s", getCounter().toString()).replace(' ', '0'))
                    .entitatDir3Codi("A04003003")
                    .organDir3Codi("A04035965")
                    .procedimentCodi("874510")
                    .enviamentTipus(EnviamentTipusEnum.NOTIFICACIO)
                    .usuariCodi("e18225486x")
//                    .docUuid("8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9")
                    .destinataris(Arrays.asList(new Persona[][]{
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Andreu").llinatge1("Adrover").llinatge2("Amoros").nif("11111111H").telefon("611111111").email("usuari1@limit.es").build() }}))
                    .build();

            String id;
            int index;
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                id = infoNot.getIdentificador();
                index = id.lastIndexOf("_");
                id = (index != -1 ? id.substring(0, index) : id) + "_" + i;
                notifica(infoNot);
            }
        }

        @Test
        public void testCarga2() {

            InfoNot infoNot = InfoNot.builder()
                    .identificador(String.format("%1$10s", getCounter().toString()).replace(' ', '0'))
                    .entitatDir3Codi("A04003003")
                    .organDir3Codi("A04035965")
                    .procedimentCodi("874510")
                    .enviamentTipus(EnviamentTipusEnum.NOTIFICACIO)
                    .usuariCodi("e18225486x")
//                    .docUuid("8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9")
                    .destinataris(Arrays.asList(new Persona[][]{
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Andreu").llinatge1("Adrover").llinatge2("Amoros").nif("11111111H").telefon("611111111").email("usuari1@limit.es").build() },
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Colau").llinatge1("Cladera").llinatge2("Cerda").nif("33333333P").telefon("633333333").email("usuari3@limit.es").build() }}))
                    .build();

            String id;
            int index;
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 2");
                id = infoNot.getIdentificador();
                index = id.lastIndexOf("_");
                id = (index != -1 ? id.substring(0, index) : id) + "_" + i;
                notifica(infoNot);
            }
        }

        @Test
        public void testCarga3() {

            InfoNot infoNot = InfoNot.builder()
                    .identificador(String.format("%1$10s", getCounter().toString()).replace(' ', '0'))
                    .entitatDir3Codi("A04003003")
                    .organDir3Codi("A04035965")
                    .procedimentCodi("874510")
                    .enviamentTipus(EnviamentTipusEnum.NOTIFICACIO)
                    .usuariCodi("e18225486x")
//                    .docUuid("8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9")
                    .destinataris(Arrays.asList(new Persona[][]{
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Andreu").llinatge1("Adrover").llinatge2("Amoros").nif("11111111H").telefon("611111111").email("usuari1@limit.es").build()},
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Bernat").llinatge1("Berga").llinatge2("Balcells").nif("22222222J").telefon("622222222").email("usuari2@limit.es").build()},
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Colau").llinatge1("Cladera").llinatge2("Cerda").nif("33333333P").telefon("633333333").email("usuari3@limit.es").build()}}))
                    .build();

            String id;
            int index;
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 3");
                id = infoNot.getIdentificador();
                index = id.lastIndexOf("_");
                id = (index != -1 ? id.substring(0, index) : id) + "_" + i;
                notifica(infoNot);
            }
        }

        @Test
        public void testCarga4() {

            InfoNot infoNot = InfoNot.builder()
                    .identificador(String.format("%1$10s", getCounter().toString()).replace(' ', '0'))
                    .entitatDir3Codi("A04003003")
                    .organDir3Codi("A04035965")
                    .procedimentCodi("874510")
                    .enviamentTipus(EnviamentTipusEnum.COMUNICACIO)
                    .usuariCodi("e18225486x")
//                    .docUuid("8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9")
                    .destinataris(Arrays.asList(new Persona[][]{
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Andreu").llinatge1("Adrover").llinatge2("Amoros").nif("11111111H").telefon("611111111").email("usuari1@limit.es").build(),
                                Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Bernat").llinatge1("Berga").llinatge2("Balcells").nif("22222222J").telefon("622222222").email("usuari2@limit.es").build() },
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Colau").llinatge1("Cladera").llinatge2("Cerda").nif("33333333P").telefon("633333333").email("usuari3@limit.es").build() }}))
                    .build();

            String id;
            int index;
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 4");
                id = infoNot.getIdentificador();
                index = id.lastIndexOf("_");
                id = (index != -1 ? id.substring(0, index) : id) + "_" + i;
                notifica(infoNot);
            }
        }

        @Test
        public void testCarga5() {

            InfoNot infoNot = InfoNot.builder()
                    .identificador(String.format("%1$10s", getCounter().toString()).replace(' ', '0'))
                    .entitatDir3Codi("A04003003")
                    .organDir3Codi("A04035965")
                    .procedimentCodi("874510")
                    .enviamentTipus(EnviamentTipusEnum.COMUNICACIO)
                    .usuariCodi("e18225486x")
//                    .docUuid("8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9")
                    .destinataris(Arrays.asList(new Persona[][]{
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Andreu").llinatge1("Adrover").llinatge2("Amoros").nif("11111111H").telefon("611111111").email("usuari1@limit.es").build() }}))
                    .build();

            String id;
            int index;
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 5");
                id = infoNot.getIdentificador();
                index = id.lastIndexOf("_");
                id = (index != -1 ? id.substring(0, index) : id) + "_" + i;
                notifica(infoNot);
            }
        }

        @Test
        public void testCarga6() {

            InfoNot infoNot = InfoNot.builder()
                    .identificador(String.format("%1$10s", getCounter().toString()).replace(' ', '0'))
                    .entitatDir3Codi("A04003003")
                    .organDir3Codi("A04035965")
                    .procedimentCodi("874510")
                    .enviamentTipus(EnviamentTipusEnum.COMUNICACIO)
                    .usuariCodi("e18225486x")
//                    .docUuid("8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9")
                    .destinataris(Arrays.asList(new Persona[][]{
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Andreu").llinatge1("Adrover").llinatge2("Amoros").nif("11111111H").telefon("611111111").email("usuari1@limit.es").build() },
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Colau").llinatge1("Cladera").llinatge2("Cerda").nif("33333333P").telefon("633333333").email("usuari3@limit.es").build() }}))
                    .build();

            String id;
            int index;
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 6");
                id = infoNot.getIdentificador();
                index = id.lastIndexOf("_");
                id = (index != -1 ? id.substring(0, index) : id) + "_" + i;
                notifica(infoNot);
            }
        }

        @Test
        public void testCarga7() {

            InfoNot infoNot = InfoNot.builder()
                    .identificador(String.format("%1$10s", getCounter().toString()).replace(' ', '0'))
                    .entitatDir3Codi("A04003003")
                    .organDir3Codi("A04035965")
                    .procedimentCodi("874510")
                    .enviamentTipus(EnviamentTipusEnum.COMUNICACIO)
                    .usuariCodi("e18225486x")
//                    .docUuid("8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9")
                    .destinataris(Arrays.asList(new Persona[][]{
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Andreu").llinatge1("Adrover").llinatge2("Amoros").nif("11111111H").telefon("611111111").email("usuari1@limit.es").build(),
                                    Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Bernat").llinatge1("Berga").llinatge2("Balcells").nif("22222222J").telefon("622222222").email("usuari2@limit.es").build() },
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Colau").llinatge1("Cladera").llinatge2("Cerda").nif("33333333P").telefon("633333333").email("usuari3@limit.es").build() }}))
                    .build();

            String id;
            int index;
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 7");
                id = infoNot.getIdentificador();
                index = id.lastIndexOf("_");
                id = (index != -1 ? id.substring(0, index) : id) + "_" + i;
                notifica(infoNot);
            }
        }

        @Test
        public void testCarga8() {

            InfoNot infoNot = InfoNot.builder()
                    .identificador(String.format("%1$10s", getCounter().toString()).replace(' ', '0'))
                    .entitatDir3Codi("A04003003")
                    .organDir3Codi("A04035965")
                    .procedimentCodi("874510")
                    .enviamentTipus(EnviamentTipusEnum.NOTIFICACIO)
                    .usuariCodi("e18225486x")
//                    .docUuid("8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9")
                    .destinataris(Arrays.asList(new Persona[][]{
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Andreu").llinatge1("Adrover").llinatge2("Amoros").nif("11111111H").telefon("611111111").email("usuari1@limit.es").build(),
                                    Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Bernat").llinatge1("Berga").llinatge2("Balcells").nif("22222222J").telefon("622222222").email("usuari2@limit.es").build() },
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Colau").llinatge1("Cladera").llinatge2("Cerda").nif("33333333P").telefon("633333333").email("usuari3@limit.es").build() }}))
                    .build();

            String id;
            int index;
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 8");
                id = infoNot.getIdentificador();
                index = id.lastIndexOf("_");
                id = (index != -1 ? id.substring(0, index) : id) + "_" + i;
                notifica(infoNot);
            }
        }

        @Test
        public void testCarga9() {

            InfoNot infoNot = InfoNot.builder()
                    .identificador(String.format("%1$10s", getCounter().toString()).replace(' ', '0'))
                    .entitatDir3Codi("A04003003")
                    .organDir3Codi("A04035965")
                    .procedimentCodi("874510")
                    .enviamentTipus(EnviamentTipusEnum.NOTIFICACIO)
                    .usuariCodi("e18225486x")
//                    .docUuid("8c01b36f-4dd6-46fd-b0a3-5a3f0581d2b9")
                    .destinataris(Arrays.asList(new Persona[][]{
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Andreu").llinatge1("Adrover").llinatge2("Amoros").nif("11111111H").telefon("611111111").email("usuari1@limit.es").build(),
                                Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Bernat").llinatge1("Berga").llinatge2("Balcells").nif("22222222J").telefon("622222222").email("usuari2@limit.es").build() },
                            {   Persona.builder().interessatTipus(InteressatTipusEnumDto.FISICA).nom("Colau").llinatge1("Cladera").llinatge2("Cerda").nif("33333333P").telefon("633333333").email("usuari3@limit.es").build() }}))
                    .build();

            String id;
            int index;
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 9");
                id = infoNot.getIdentificador();
                index = id.lastIndexOf("_");
                id = (index != -1 ? id.substring(0, index) : id) + "_" + i;
                notifica(infoNot);
            }
        }

        private void notifica(InfoNot infoNot) {

            try {
                Long ti = System.currentTimeMillis();
                System.out.println(infoNot.getIdentificador() + ".");
                NotificacioV2 notificacio = generarNotificacio(infoNot);
                System.out.println(">>> Petició de la notificació: " + notificacio.getConcepte());
                RespostaAltaV2 respostaAlta = client.alta(notificacio);
                if (respostaAlta.isError()) {
                    System.out.println(">>> Reposta amb error: " + respostaAlta.getErrorDescripcio());
                } else {
                    System.out.println(">>> Reposta Ok");
                    setNotificacio(respostaAlta.getIdentificador());
                    for (EnviamentReferenciaV2 ref: respostaAlta.getReferencies()) {
                        setEnviament(ref.getReferencia());
                    }
                }
                System.out.println(">>> Finalitzan de la notificació: " + notificacio.getConcepte());
                System.out.println(" Duració: " + (System.currentTimeMillis() - ti) + "ms");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private NotificacioV2 generarNotificacio(InfoNot infoNot) {

            String notificacioId = infoNot.getIdentificador();
            NotificacioV2 notificacio = new NotificacioV2();
            notificacio.setEmisorDir3Codi(infoNot.getEntitatDir3Codi());
            notificacio.setEnviamentTipus(infoNot.getEnviamentTipus());
            notificacio.setUsuariCodi(infoNot.getUsuariCodi());
            notificacio.setOrganGestor(infoNot.getOrganDir3Codi());
            notificacio.setConcepte("concepte_" + notificacioId);
            notificacio.setDescripcio("descripcio_" + notificacioId);
            notificacio.setEnviamentDataProgramada(null);
            notificacio.setRetard(5);
            notificacio.setCaducitat(new Date(System.currentTimeMillis() + 12 * 24 * 3600 * 1000));
            DocumentV2 document = new DocumentV2();
            document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
            if (infoNot.getDocUuid() != null) {
                document.setUuid(infoNot.getDocUuid());
            } else if (infoNot.getDocCsv() != null) {
                document.setCsv(infoNot.getDocCsv());
            } else {
                document.setContingutBase64(arxiuContingut);
            }

            document.setNormalitzat(false);

            notificacio.setDocument(document);
            notificacio.setProcedimentCodi(infoNot.getProcedimentCodi());
            for (Persona[] persones: infoNot.getDestinataris()) {
                Enviament enviament = new Enviament();
                enviament.setTitular(persones[0]);
                if (persones.length > 1) {
                    enviament.getDestinataris().add(persones[1]);
                }
                EntregaDeh entregaDeh = new EntregaDeh();
                entregaDeh.setObligat(false);
                entregaDeh.setProcedimentCodi(infoNot.getProcedimentCodi());
                enviament.setEntregaDeh(entregaDeh);
                enviament.setServeiTipus(NotificaServeiTipusEnumDto.NORMAL);
                notificacio.getEnviaments().add(enviament);
            }
            return notificacio;
        }


        @Data
        @Builder
        public static class InfoNot {
            private String identificador;
            private String entitatDir3Codi;
            private String organDir3Codi;
            private String procedimentCodi;
            private EnviamentTipusEnum enviamentTipus;
            private String usuariCodi;
            private String docUuid;
            private String docCsv;
//            private NotificaDomiciliConcretTipusEnumDto tipusEntregaPostal; // --> No fem proves amb entrega postal
            private List<Persona[]> destinataris;
        }
    }

    public static class ParallelConsultaNotificacioTest {

        private NotificacioRestClientV2 client;
        @Before
        public void setUp() throws Exception {

            String keystorePath = ClientRestTest.class.getResource("/es/caib/notib/client/truststore.jks").toURI().getPath();
            System.setProperty("javax.net.ssl.trustStore", keystorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");

            client = NotificacioRestClientFactory.getRestClientV2(URL, USERNAME, PASSWORD);
        }

        @Test
        public void testConsulta1() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                String identificador = getNotificacio();
                if (identificador != null) {
                    consultaNotificacio(identificador);
                }
            }
        }

        @Test
        public void testConsulta2() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                String identificador = getNotificacio();
                if (identificador != null) {
                    consultaNotificacio(identificador);
                }
            }
        }

        @Test
        public void testConsulta3() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                String identificador = getNotificacio();
                if (identificador != null) {
                    consultaNotificacio(identificador);
                }
            }
        }

        private void consultaNotificacio(String identificador) {
            try {
                Long ti = System.currentTimeMillis();
                System.out.println(">>> Consulta de la notificació: " + identificador);
                RespostaConsultaEstatNotificacioV2 respostaConsultaEstatNotificacio = client.consultaEstatNotificacio(getNotificacio());
                if (respostaConsultaEstatNotificacio.isError()) {
                    System.out.println(">>> Reposta amb error: " + respostaConsultaEstatNotificacio.getErrorDescripcio());
                } else {
                    System.out.println(">>> Reposta Ok");
                }
                System.out.println(">>> Finalitzant consulta de la notificació: " + identificador);
                System.out.println(" Duració: " + (System.currentTimeMillis() - ti) + "ms");
            } catch (Exception ex) {
                System.out.println("Error realitzant la consulta de la notificació: " + identificador + ": ");
                ex.printStackTrace();
            }
        }

    }

    public static class ParallelConsultaEnviamentTest {

        private NotificacioRestClientV2 client;
        @Before
        public void setUp() throws Exception {

            String keystorePath = ClientRestTest.class.getResource("/es/caib/notib/client/truststore.jks").toURI().getPath();
            System.setProperty("javax.net.ssl.trustStore", keystorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");

            client = NotificacioRestClientFactory.getRestClientV2(URL, USERNAME, PASSWORD);
        }

        @Test
        public void testConsulta1() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                String referencia = getEnviament();
                if (referencia != null) {
                    consultaEnviament(referencia);
                }
            }
        }

        @Test
        public void testConsulta2() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                String referencia = getEnviament();
                if (referencia != null) {
                    consultaEnviament(referencia);
                }
            }
        }

        @Test
        public void testConsulta3() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                String referencia = getEnviament();
                if (referencia != null) {
                    consultaEnviament(referencia);
                }
            }
        }

        private void consultaEnviament(String referencia) {
            try {
                Long ti = System.currentTimeMillis();
                System.out.println(">>> Consulta de l'enviament: " + referencia);
                RespostaConsultaEstatEnviamentV2 respostaConsultaEstatEnviament = client.consultaEstatEnviament(getEnviament());
                if (respostaConsultaEstatEnviament.isError()) {
                    System.out.println(">>> Reposta amb error: " + respostaConsultaEstatEnviament.getErrorDescripcio());
                } else {
                    System.out.println(">>> Reposta Ok");
                }
                System.out.println(">>> Finalitzant consulta de l'enviament: " + referencia);
                System.out.println(" Duració: " + (System.currentTimeMillis() - ti) + "ms");
            } catch (Exception ex) {
                System.out.println("Error realitzant la consulta de l'enviament: " + referencia + ": ");
                ex.printStackTrace();
            }
        }

    }

    public static class ParallelConsultaRegistreTest {

        private NotificacioRestClientV2 client;
        @Before
        public void setUp() throws Exception {

            String keystorePath = ClientRestTest.class.getResource("/es/caib/notib/client/truststore.jks").toURI().getPath();
            System.setProperty("javax.net.ssl.trustStore", keystorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");

            client = NotificacioRestClientFactory.getRestClientV2(URL, USERNAME, PASSWORD);
        }

        @Test
        public void testConsulta1() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                DadesConsulta dadesConsulta = DadesConsulta.builder().identificador(getNotificacio()).build();
                if (dadesConsulta.getIdentificador() != null) {
                    consultaRegistre(dadesConsulta);
                }
            }
        }

        @Test
        public void testConsulta2() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                DadesConsulta dadesConsulta = DadesConsulta.builder().referencia(getEnviament()).build();
                if (dadesConsulta.getReferencia() != null) {
                    consultaRegistre(dadesConsulta);
                }
            }
        }

        @Test
        public void testConsulta3() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                DadesConsulta dadesConsulta = DadesConsulta.builder().referencia(getEnviament()).ambJustificant(true).build();
                if (dadesConsulta.getReferencia() != null) {
                    consultaRegistre(dadesConsulta);
                }
            }
        }

        private void consultaRegistre(DadesConsulta dadesConsulta) {
            String msg = dadesConsulta.getIdentificador() != null ? "la notificacio: " + dadesConsulta.getIdentificador() : "l'enviament: " + dadesConsulta.getReferencia();
            try {
                Long ti = System.currentTimeMillis();
                System.out.println(">>> Consulta de les dades de registre de " + msg);
                RespostaConsultaDadesRegistreV2 respostaConsultaDadesRegistre = client.consultaDadesRegistre(dadesConsulta);
                if (respostaConsultaDadesRegistre.isError()) {
                    System.out.println(">>> Reposta amb error: " + respostaConsultaDadesRegistre.getErrorDescripcio());
                } else {
                    System.out.println(">>> Reposta Ok");
                }
                System.out.println(">>> Finalitzant consulta de les dades de registre de " + msg);
                System.out.println(" Duració: " + (System.currentTimeMillis() - ti) + "ms");
            } catch (Exception ex) {
                System.out.println("Error realitzant la consulta de les dades de registre de " + msg);
                ex.printStackTrace();
            }
        }

    }

    public static class ParallelConsultaCertificacioTest {

        private NotificacioRestClientV2 client;
        @Before
        public void setUp() throws Exception {

            String keystorePath = ClientRestTest.class.getResource("/es/caib/notib/client/truststore.jks").toURI().getPath();
            System.setProperty("javax.net.ssl.trustStore", keystorePath);
            System.setProperty("javax.net.ssl.trustStorePassword", "tecnologies");

            client = NotificacioRestClientFactory.getRestClientV2(URL, USERNAME, PASSWORD);
        }

        @Test
        public void testConsulta1() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                String identificador = getNotificacio();
                if (identificador != null) {
                    consultaJustificant(identificador);
                }
            }
        }

        @Test
        public void testConsulta2() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                String identificador = getNotificacio();
                if (identificador != null) {
                    consultaJustificant(identificador);
                }
            }
        }

        @Test
        public void testConsulta3() {
            for (int i = 0; i < 250000; i++) {
                System.out.println("Execució 1");
                String identificador = getNotificacio();
                if (identificador != null) {
                    consultaJustificant(identificador);
                }
            }
        }

        private void consultaJustificant(String identificador) {
            try {
                Long ti = System.currentTimeMillis();
                System.out.println(">>> Consulta del justificant de la notificació: " + identificador);
                RespostaConsultaJustificantEnviament respostaConsultaJustificantEnviament = client.consultaJustificantEnviament(getNotificacio());
                if (respostaConsultaJustificantEnviament.isError()) {
                    System.out.println(">>> Reposta amb error: " + respostaConsultaJustificantEnviament.getErrorDescripcio());
                } else {
                    System.out.println(">>> Reposta Ok");
                }
                System.out.println(">>> Finalitzant consulta del justificant de la notificació: " + identificador);
                System.out.println(" Duració: " + (System.currentTimeMillis() - ti) + "ms");
            } catch (Exception ex) {
                System.out.println("Error realitzant la consulta del justificant de la notificació " + identificador + ": ");
                ex.printStackTrace();
            }
        }

    }

}
