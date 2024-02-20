package es.caib.notib.logic.objectes;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.notificacio.Document;
import es.caib.notib.logic.intf.dto.notificacio.EntregaPostal;
import es.caib.notib.logic.intf.dto.notificacio.Enviament;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.logic.intf.dto.notificacio.Persona;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.cie.EntregaCieEntity;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class MassivaFileTest {

    @Mock
    private ConfigHelper configHelper;

    @Mock
    private MessageHelper messageHelper;

    @Mock
    private PluginHelper pluginHelper;

    private MassivaFile massivaFile;

    @BeforeEach
    public void setup() {
        openMocks(MassivaFileTest.class);
        configHelper = Mockito.mock(ConfigHelper.class);
        messageHelper = Mockito.mock(MessageHelper.class);
        pluginHelper = Mockito.mock(PluginHelper.class);

        massivaFile = new MassivaFile(configHelper, messageHelper, pluginHelper);

        when(configHelper.getConfigAsLong(eq("es.caib.notib.massives.maxim.files"), eq(999L))).thenReturn(999L);
        when(configHelper.getConfigAsBoolean(eq("es.caib.notib.plugin.registre.documents.enviar"), eq(false))).thenReturn(true);
    }

    @Test
    public void testCreateInitialization() throws Exception {

        // Given
        NotificacioMassivaDto notificacioMassiva = NotificacioMassivaDto.builder()
                .ficheroCsvNom("testm1.csv")
                .ficheroZipNom("test1.zip")
                .ficheroCsvBytes(getResourceFileAsBytes("/es/caib/notib/logic/massiu/test.csv"))
                .ficheroZipBytes(getResourceFileAsBytes("/es/caib/notib/logic/massiu/test1.zip"))
                .caducitat(new Date())
                .build();
        EntitatEntity entitatEntity = EntitatEntity.hiddenBuilder().dir3Codi("A04000000").entregaCie(new EntregaCieEntity()).build();
        String usuari = "username";
        var expectedHeader = Arrays.asList("Codigo Unidad Remisora", "Concepto", "Tipo de Envio", "Referencia Emisor", "Nombre Fichero", "UUID Fichero", "CSV Fichero", "Normalizado", "Prioridad Servicio", "Nombre", "Apellidos", "CIF/NIF", "Email", "Codigo destino", "Tipo documento", "Linea 1", "Linea 2", "Codigo Postal", "Retardo Postal", "Codigo Procedimiento", "Fecha Envio Programado", "Origen", "Estado Elaboracion", "Tipo documental", "PDF Firmado", "Descripcion");
        var expectedRow1 = Arrays.asList("E04975701", "Concepto 1", "Notificacion", "NOTIF2103021", "prueba.pdf", null, null, "Si", "Normal", "ANF", "RODRIGUEZ SANCHEZ", "12345678Z", "mail@mail.com", "L01280796", "PASSAPORT", "Castelló 115", "28016 Madrid", "37891", "0", "101310", "20/02/2024", "ADMINISTRACIO", "ORIGINAL", "INFORME", "true", "Desc 1");
        var expectedRow2 = Arrays.asList("E04975702", "Concepto 2", "Comunicacion", "NOTIF2103022", null, "f1c9f8c8-d326-4315-8a8f-0ec23b970ae5", null, "No", "Urgente", "PEP", "SANCHEZ RODRIGUEZ", "99999999R", "email@mail.com", null, null, "Bas 3", "07500 Manacor", "07500", "2", "123456", "19/02/2024", "CIUTADA", "ORIGINAL", "ACTA", "false", "Desc 2");
        var expectedRow3 = Arrays.asList("E04975703", "Concepto 3", "SIR", "NOTIF2103023", null, null, "3968d0f21388c950d4fbd2654f3867771d3ee1d4d017c477872173ec69980359", "No", "Normal", "XXX", "XXXXXX XXXXXX", "22222222J", "jmail@mail.com", null, null, "Bas 6", "07501 Petra", "07501", "0", null, null, null, null, null, null, "Desc 3");
        var contingut = getResourceFileAsBytes("/es/caib/notib/logic/massiu/prueba.pdf");
        var expectedNot1 = Notificacio.builder()
                .emisorDir3Codi(entitatEntity.getDir3Codi())
                .organGestor(expectedRow1.get(0))
                .procedimentCodi(expectedRow1.get(19))
                .concepte(expectedRow1.get(1))
                .descripcio(expectedRow1.get(25))
                .numExpedient(expectedRow1.get(3))
                .enviamentTipus(EnviamentTipus.NOTIFICACIO)
                .caducitat(notificacioMassiva.getCaducitat())
                .retard(Integer.parseInt(expectedRow1.get(18)))
                .enviamentDataProgramada(new SimpleDateFormat("dd/MM/yyyy").parse(expectedRow1.get(20)))
                .document(Document.builder().arxiuNom(expectedRow1.get(4)).contingutBase64(Base64.encodeBase64String(contingut)).normalitzat(true).generarCsv(false).mediaType("application/pdf").mida(Long.valueOf(contingut.length)).origen(OrigenEnum.ADMINISTRACIO).validesa(ValidesaEnum.ORIGINAL).tipoDocumental(TipusDocumentalEnum.INFORME).modoFirma(true).build())
                .enviaments(List.of(Enviament.builder().serveiTipus(ServeiTipus.NORMAL).entregaDehActiva(false).entregaPostalActiva(true).entregaPostal(EntregaPostal.builder().tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR).linea1(expectedRow1.get(15)).linea2(expectedRow1.get(16)).codiPostal(expectedRow1.get(17)).build()).titular(Persona.builder().interessatTipus(InteressatTipus.FISICA).nom(expectedRow1.get(9)).llinatge1(expectedRow1.get(10)).documentTipus(null).nif(expectedRow1.get(11)).dir3Codi(expectedRow1.get(13)).email(expectedRow1.get(12)).incapacitat(false).build()).perEmail(false).build()))
                .usuariCodi(usuari)
                .build();
        var expectedNot2 = Notificacio.builder()
                .emisorDir3Codi(entitatEntity.getDir3Codi())
                .organGestor(expectedRow2.get(0))
                .procedimentCodi(expectedRow2.get(19))
                .concepte(expectedRow2.get(1))
                .descripcio(expectedRow2.get(25))
                .numExpedient(expectedRow2.get(3))
                .enviamentTipus(EnviamentTipus.COMUNICACIO)
                .caducitat(notificacioMassiva.getCaducitat())
                .retard(Integer.parseInt(expectedRow2.get(18)))
                .enviamentDataProgramada(new SimpleDateFormat("dd/MM/yyyy").parse(expectedRow2.get(20)))
                .document(Document.builder().uuid(expectedRow2.get(5)).normalitzat(false).generarCsv(false).origen(OrigenEnum.CIUTADA).validesa(ValidesaEnum.ORIGINAL).tipoDocumental(TipusDocumentalEnum.ACTA).modoFirma(false).build())
                .enviaments(List.of(Enviament.builder().serveiTipus(ServeiTipus.URGENT).entregaDehActiva(false).entregaPostalActiva(true).entregaPostal(EntregaPostal.builder().tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR).linea1(expectedRow2.get(15)).linea2(expectedRow2.get(16)).codiPostal(expectedRow2.get(17)).build()).titular(Persona.builder().interessatTipus(InteressatTipus.FISICA).nom(expectedRow2.get(9)).llinatge1(expectedRow2.get(10)).documentTipus(null).nif(expectedRow2.get(11)).dir3Codi(expectedRow2.get(13)).email(expectedRow2.get(12)).incapacitat(false).build()).perEmail(false).build()))
                .usuariCodi(usuari)
                .build();
        var expectedNot3 = Notificacio.builder()
                .emisorDir3Codi(entitatEntity.getDir3Codi())
                .organGestor(expectedRow3.get(0))
                .procedimentCodi(null)
                .concepte(expectedRow3.get(1))
                .descripcio(expectedRow3.get(25))
                .numExpedient(expectedRow3.get(3))
                .enviamentTipus(EnviamentTipus.SIR)
                .caducitat(notificacioMassiva.getCaducitat())
                .retard(Integer.parseInt(expectedRow3.get(18)))
                .enviamentDataProgramada(null)
                .document(Document.builder().csv(expectedRow3.get(6)).normalitzat(false).generarCsv(false).modoFirma(false).build())
                .enviaments(List.of(Enviament.builder().serveiTipus(ServeiTipus.NORMAL).entregaDehActiva(false).entregaPostalActiva(true).entregaPostal(EntregaPostal.builder().tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR).linea1(expectedRow3.get(15)).linea2(expectedRow3.get(16)).codiPostal(expectedRow3.get(17)).build()).titular(Persona.builder().interessatTipus(InteressatTipus.FISICA).nom(expectedRow3.get(9)).llinatge1(expectedRow3.get(10)).documentTipus(null).nif(expectedRow3.get(11)).dir3Codi(expectedRow3.get(13)).email(expectedRow3.get(12)).incapacitat(false).build()).perEmail(false).build()))
                .usuariCodi(usuari)
                .build();

        // When
        massivaFile.initCreate(notificacioMassiva, entitatEntity, usuari);

        // Then
        assertEquals(expectedHeader, massivaFile.getHeader(), "La capçalera no és igual");
        assertEquals(3, massivaFile.getNombreEnviamentsCsv(), "La nombre d'enviaments no és correcte");
        assertEquals(expectedRow1, massivaFile.getEnviamentsCsv().get(0), "La primera fila no és igual");
        assertEquals(expectedRow2, massivaFile.getEnviamentsCsv().get(1), "La segona fila no és igual");
        assertEquals(expectedRow3, massivaFile.getEnviamentsCsv().get(2), "La tercera fila no és igual");
        assertEquals(expectedNot1, massivaFile.getNotificacions().get(0), "La primera notificació no és igual");
        assertEquals(expectedNot2, massivaFile.getNotificacions().get(1), "La segona notificació no és igual");
        assertEquals(expectedNot3, massivaFile.getNotificacions().get(2), "La tercera notificació no és igual");

    }

    @Test
    public void testCreateInitializationWithUnorderedColumns() throws Exception {

        // Given
        NotificacioMassivaDto notificacioMassiva = NotificacioMassivaDto.builder()
                .ficheroCsvNom("testm2.csv")
                .ficheroZipNom("test1.zip")
                .ficheroCsvBytes(getResourceFileAsBytes("/es/caib/notib/logic/massiu/testm2.csv"))
                .ficheroZipBytes(getResourceFileAsBytes("/es/caib/notib/logic/massiu/test1.zip"))
                .caducitat(new Date())
                .build();
        EntitatEntity entitatEntity = EntitatEntity.hiddenBuilder().dir3Codi("A04000000").entregaCie(new EntregaCieEntity()).build();
        String usuari = "username";
        var expectedHeader = Arrays.asList("Concepto", "Tipo de Envio", "Referencia Emisor", "Nombre Fichero", "UUID Fichero", "CSV Fichero", "Normalizado", "Prioridad Servicio", "Nombre", "Apellidos", "CIF/NIF", "Email", "Codigo destino", "Tipo documento", "Linea 1", "Linea 2", "Codigo Postal", "Retardo Postal", "Codigo Procedimiento", "Fecha Envio Programado", "Origen", "Estado Elaboracion", "Tipo documental", "PDF Firmado", "Descripcion", "Codigo Unidad Remisora");
        var expectedRow1 = Arrays.asList("Concepto 1", "Notificacion", "NOTIF2103021", "prueba.pdf", null, null, "Si", "Normal", "ANF", "RODRIGUEZ SANCHEZ", "12345678Z", "mail@mail.com", "L01280796", "PASSAPORT", "Castelló 115", "28016 Madrid", "37891", "0", "101310", "20/02/2024", "ADMINISTRACIO", "ORIGINAL", "INFORME", "true", "Desc 1", "E04975701");
        var expectedRow2 = Arrays.asList("Concepto 2", "Comunicacion", "NOTIF2103022", null, "f1c9f8c8-d326-4315-8a8f-0ec23b970ae5", null, "No", "Urgente", "PEP", "SANCHEZ RODRIGUEZ", "99999999R", "email@mail.com", null, null, "Bas 3", "07500 Manacor", "07500", "2", "123456", "19/02/2024", "CIUTADA", "ORIGINAL", "ACTA", "false", "Desc 2", "E04975702");
        var expectedRow3 = Arrays.asList("Concepto 3", "SIR", "NOTIF2103023", null, null, "3968d0f21388c950d4fbd2654f3867771d3ee1d4d017c477872173ec69980359", "No", "Normal", "XXX", "XXXXXX XXXXXX", "22222222J", "jmail@mail.com", null, null, "Bas 6", "07501 Petra", "07501", "0", null, null, null, null, null, null, "Desc 3", "E04975703");
        var contingut = getResourceFileAsBytes("/es/caib/notib/logic/massiu/prueba.pdf");
        var expectedNot1 = Notificacio.builder()
                .emisorDir3Codi(entitatEntity.getDir3Codi())
                .organGestor(expectedRow1.get(25))
                .procedimentCodi(expectedRow1.get(18))
                .concepte(expectedRow1.get(0))
                .descripcio(expectedRow1.get(24))
                .numExpedient(expectedRow1.get(2))
                .enviamentTipus(EnviamentTipus.NOTIFICACIO)
                .caducitat(notificacioMassiva.getCaducitat())
                .retard(Integer.parseInt(expectedRow1.get(17)))
                .enviamentDataProgramada(new SimpleDateFormat("dd/MM/yyyy").parse(expectedRow1.get(19)))
                .document(Document.builder().arxiuNom(expectedRow1.get(3)).contingutBase64(Base64.encodeBase64String(contingut)).normalitzat(true).generarCsv(false).mediaType("application/pdf").mida(Long.valueOf(contingut.length)).origen(OrigenEnum.ADMINISTRACIO).validesa(ValidesaEnum.ORIGINAL).tipoDocumental(TipusDocumentalEnum.INFORME).modoFirma(true).build())
                .enviaments(List.of(Enviament.builder().serveiTipus(ServeiTipus.NORMAL).entregaDehActiva(false).entregaPostalActiva(true).entregaPostal(EntregaPostal.builder().tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR).linea1(expectedRow1.get(14)).linea2(expectedRow1.get(15)).codiPostal(expectedRow1.get(16)).build()).titular(Persona.builder().interessatTipus(InteressatTipus.FISICA).nom(expectedRow1.get(8)).llinatge1(expectedRow1.get(9)).documentTipus(null).nif(expectedRow1.get(10)).dir3Codi(expectedRow1.get(12)).email(expectedRow1.get(11)).incapacitat(false).build()).perEmail(false).build()))
                .usuariCodi(usuari)
                .build();
        var expectedNot2 = Notificacio.builder()
                .emisorDir3Codi(entitatEntity.getDir3Codi())
                .organGestor(expectedRow2.get(25))
                .procedimentCodi(expectedRow2.get(18))
                .concepte(expectedRow2.get(0))
                .descripcio(expectedRow2.get(24))
                .numExpedient(expectedRow2.get(2))
                .enviamentTipus(EnviamentTipus.COMUNICACIO)
                .caducitat(notificacioMassiva.getCaducitat())
                .retard(Integer.parseInt(expectedRow2.get(17)))
                .enviamentDataProgramada(new SimpleDateFormat("dd/MM/yyyy").parse(expectedRow2.get(19)))
                .document(Document.builder().uuid(expectedRow2.get(4)).normalitzat(false).generarCsv(false).origen(OrigenEnum.CIUTADA).validesa(ValidesaEnum.ORIGINAL).tipoDocumental(TipusDocumentalEnum.ACTA).modoFirma(false).build())
                .enviaments(List.of(Enviament.builder().serveiTipus(ServeiTipus.URGENT).entregaDehActiva(false).entregaPostalActiva(true).entregaPostal(EntregaPostal.builder().tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR).linea1(expectedRow2.get(14)).linea2(expectedRow2.get(15)).codiPostal(expectedRow2.get(16)).build()).titular(Persona.builder().interessatTipus(InteressatTipus.FISICA).nom(expectedRow2.get(8)).llinatge1(expectedRow2.get(9)).documentTipus(null).nif(expectedRow2.get(10)).dir3Codi(expectedRow2.get(12)).email(expectedRow2.get(11)).incapacitat(false).build()).perEmail(false).build()))
                .usuariCodi(usuari)
                .build();
        var expectedNot3 = Notificacio.builder()
                .emisorDir3Codi(entitatEntity.getDir3Codi())
                .organGestor(expectedRow3.get(25))
                .procedimentCodi(null)
                .concepte(expectedRow3.get(0))
                .descripcio(expectedRow3.get(24))
                .numExpedient(expectedRow3.get(2))
                .enviamentTipus(EnviamentTipus.SIR)
                .caducitat(notificacioMassiva.getCaducitat())
                .retard(Integer.parseInt(expectedRow3.get(17)))
                .enviamentDataProgramada(null)
                .document(Document.builder().csv(expectedRow3.get(5)).normalitzat(false).generarCsv(false).modoFirma(false).build())
                .enviaments(List.of(Enviament.builder().serveiTipus(ServeiTipus.NORMAL).entregaDehActiva(false).entregaPostalActiva(true).entregaPostal(EntregaPostal.builder().tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR).linea1(expectedRow3.get(14)).linea2(expectedRow3.get(15)).codiPostal(expectedRow3.get(16)).build()).titular(Persona.builder().interessatTipus(InteressatTipus.FISICA).nom(expectedRow3.get(8)).llinatge1(expectedRow3.get(9)).documentTipus(null).nif(expectedRow3.get(10)).dir3Codi(expectedRow3.get(12)).email(expectedRow3.get(11)).incapacitat(false).build()).perEmail(false).build()))
                .usuariCodi(usuari)
                .build();

        // When
        massivaFile.initCreate(notificacioMassiva, entitatEntity, usuari);

        // Then
        assertEquals(expectedHeader, massivaFile.getHeader(), "La capçalera no és igual");
        assertEquals(3, massivaFile.getNombreEnviamentsCsv(), "La nombre d'enviaments no és correcte");
        assertEquals(expectedRow1, massivaFile.getEnviamentsCsv().get(0), "La primera fila no és igual");
        assertEquals(expectedRow2, massivaFile.getEnviamentsCsv().get(1), "La segona fila no és igual");
        assertEquals(expectedRow3, massivaFile.getEnviamentsCsv().get(2), "La tercera fila no és igual");
        assertEquals(expectedNot1, massivaFile.getNotificacions().get(0), "La primera notificació no és igual");
        assertEquals(expectedNot2, massivaFile.getNotificacions().get(1), "La segona notificació no és igual");
        assertEquals(expectedNot3, massivaFile.getNotificacions().get(2), "La tercera notificació no és igual");

    }

    @Test
    public void testCreateInitializationWithUnorderedColumnsWithoutUuidAndCsvColumns() throws Exception {

        // Given
        NotificacioMassivaDto notificacioMassiva = NotificacioMassivaDto.builder()
                .ficheroCsvNom("testm3.csv")
                .ficheroZipNom("test2.zip")
                .ficheroCsvBytes(getResourceFileAsBytes("/es/caib/notib/logic/massiu/testm3.csv"))
                .ficheroZipBytes(getResourceFileAsBytes("/es/caib/notib/logic/massiu/test2.zip"))
                .caducitat(new Date())
                .build();
        EntitatEntity entitatEntity = EntitatEntity.hiddenBuilder().dir3Codi("A04000000").entregaCie(new EntregaCieEntity()).build();
        String usuari = "username";
        var expectedHeader = Arrays.asList("Concepto", "Tipo de Envio", "Referencia Emisor", "Nombre Fichero", "Normalizado", "Prioridad Servicio", "Nombre", "Apellidos", "CIF/NIF", "Email", "Codigo destino", "Tipo documento", "Linea 1", "Linea 2", "Codigo Postal", "Retardo Postal", "Codigo Procedimiento", "Fecha Envio Programado", "Origen", "Estado Elaboracion", "Tipo documental", "PDF Firmado", "Descripcion", "Codigo Unidad Remisora");
        var expectedRow1 = Arrays.asList("Concepto 1", "Notificacion", "NOTIF2103021", "prueba.pdf", "Si", "Normal", "ANF", "RODRIGUEZ SANCHEZ", "12345678Z", "mail@mail.com", "L01280796", "PASSAPORT", "Castelló 115", "28016 Madrid", "37891", "0", "101310", "20/02/2024", "ADMINISTRACIO", "ORIGINAL", "INFORME", "true", "Desc 1", "E04975701");
        var expectedRow2 = Arrays.asList("Concepto 2", "Comunicacion", "NOTIF2103022", "prueba2.pdf", "No", "Urgente", "PEP", "SANCHEZ RODRIGUEZ", "99999999R", "email@mail.com", null, null, "Bas 3", "07500 Manacor", "07500", "2", "123456", "19/02/2024", "CIUTADA", "ORIGINAL", "ACTA", "false", "Desc 2", "E04975702");
        var expectedRow3 = Arrays.asList("Concepto 3", "SIR", "NOTIF2103023", "prueba3.pdf", "No", "Normal", "XXX", "XXXXXX XXXXXX", "22222222J", "jmail@mail.com", null, null, "Bas 6", "07501 Petra", "07501", "0", null, null, null, null, null, null, "Desc 3", "E04975703");
        var contingut = getResourceFileAsBytes("/es/caib/notib/logic/massiu/prueba.pdf");
        var expectedNot1 = Notificacio.builder()
                .emisorDir3Codi(entitatEntity.getDir3Codi())
                .organGestor(expectedRow1.get(23))
                .procedimentCodi(expectedRow1.get(16))
                .concepte(expectedRow1.get(0))
                .descripcio(expectedRow1.get(22))
                .numExpedient(expectedRow1.get(2))
                .enviamentTipus(EnviamentTipus.NOTIFICACIO)
                .caducitat(notificacioMassiva.getCaducitat())
                .retard(Integer.parseInt(expectedRow1.get(15)))
                .enviamentDataProgramada(new SimpleDateFormat("dd/MM/yyyy").parse(expectedRow1.get(17)))
                .document(Document.builder().arxiuNom(expectedRow1.get(3)).contingutBase64(Base64.encodeBase64String(contingut)).normalitzat(true).generarCsv(false).mediaType("application/pdf").mida(Long.valueOf(contingut.length)).origen(OrigenEnum.ADMINISTRACIO).validesa(ValidesaEnum.ORIGINAL).tipoDocumental(TipusDocumentalEnum.INFORME).modoFirma(true).build())
                .enviaments(List.of(Enviament.builder().serveiTipus(ServeiTipus.NORMAL).entregaDehActiva(false).entregaPostalActiva(true).entregaPostal(EntregaPostal.builder().tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR).linea1(expectedRow1.get(12)).linea2(expectedRow1.get(13)).codiPostal(expectedRow1.get(14)).build()).titular(Persona.builder().interessatTipus(InteressatTipus.FISICA).nom(expectedRow1.get(6)).llinatge1(expectedRow1.get(7)).documentTipus(null).nif(expectedRow1.get(8)).dir3Codi(expectedRow1.get(10)).email(expectedRow1.get(9)).incapacitat(false).build()).perEmail(false).build()))
                .usuariCodi(usuari)
                .build();
        var expectedNot2 = Notificacio.builder()
                .emisorDir3Codi(entitatEntity.getDir3Codi())
                .organGestor(expectedRow2.get(23))
                .procedimentCodi(expectedRow2.get(16))
                .concepte(expectedRow2.get(0))
                .descripcio(expectedRow2.get(22))
                .numExpedient(expectedRow2.get(2))
                .enviamentTipus(EnviamentTipus.COMUNICACIO)
                .caducitat(notificacioMassiva.getCaducitat())
                .retard(Integer.parseInt(expectedRow2.get(15)))
                .enviamentDataProgramada(new SimpleDateFormat("dd/MM/yyyy").parse(expectedRow2.get(17)))
                .document(Document.builder().arxiuNom(expectedRow2.get(3)).contingutBase64(Base64.encodeBase64String(contingut)).normalitzat(false).generarCsv(false).mediaType("application/pdf").mida(Long.valueOf(contingut.length)).origen(OrigenEnum.CIUTADA).validesa(ValidesaEnum.ORIGINAL).tipoDocumental(TipusDocumentalEnum.ACTA).modoFirma(false).build())
                .enviaments(List.of(Enviament.builder().serveiTipus(ServeiTipus.URGENT).entregaDehActiva(false).entregaPostalActiva(true).entregaPostal(EntregaPostal.builder().tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR).linea1(expectedRow2.get(12)).linea2(expectedRow2.get(13)).codiPostal(expectedRow2.get(14)).build()).titular(Persona.builder().interessatTipus(InteressatTipus.FISICA).nom(expectedRow2.get(6)).llinatge1(expectedRow2.get(7)).documentTipus(null).nif(expectedRow2.get(8)).dir3Codi(expectedRow2.get(10)).email(expectedRow2.get(9)).incapacitat(false).build()).perEmail(false).build()))
                .usuariCodi(usuari)
                .build();
        var expectedNot3 = Notificacio.builder()
                .emisorDir3Codi(entitatEntity.getDir3Codi())
                .organGestor(expectedRow3.get(23))
                .procedimentCodi(null)
                .concepte(expectedRow3.get(0))
                .descripcio(expectedRow3.get(22))
                .numExpedient(expectedRow3.get(2))
                .enviamentTipus(EnviamentTipus.SIR)
                .caducitat(notificacioMassiva.getCaducitat())
                .retard(Integer.parseInt(expectedRow3.get(15)))
                .enviamentDataProgramada(null)
                .document(Document.builder().arxiuNom(expectedRow3.get(3)).contingutBase64(Base64.encodeBase64String(contingut)).normalitzat(false).generarCsv(false).mediaType("application/pdf").mida(Long.valueOf(contingut.length)).modoFirma(false).build())
                .enviaments(List.of(Enviament.builder().serveiTipus(ServeiTipus.NORMAL).entregaDehActiva(false).entregaPostalActiva(true).entregaPostal(EntregaPostal.builder().tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR).linea1(expectedRow3.get(12)).linea2(expectedRow3.get(13)).codiPostal(expectedRow3.get(14)).build()).titular(Persona.builder().interessatTipus(InteressatTipus.FISICA).nom(expectedRow3.get(6)).llinatge1(expectedRow3.get(7)).documentTipus(null).nif(expectedRow3.get(8)).dir3Codi(expectedRow3.get(10)).email(expectedRow3.get(9)).incapacitat(false).build()).perEmail(false).build()))
                .usuariCodi(usuari)
                .build();

        // When
        massivaFile.initCreate(notificacioMassiva, entitatEntity, usuari);

        // Then
        assertEquals(expectedHeader, massivaFile.getHeader(), "La capçalera no és igual");
        assertEquals(3, massivaFile.getNombreEnviamentsCsv(), "La nombre d'enviaments no és correcte");
        assertEquals(expectedRow1, massivaFile.getEnviamentsCsv().get(0), "La primera fila no és igual");
        assertEquals(expectedRow2, massivaFile.getEnviamentsCsv().get(1), "La segona fila no és igual");
        assertEquals(expectedRow3, massivaFile.getEnviamentsCsv().get(2), "La tercera fila no és igual");
        assertEquals(expectedNot1, massivaFile.getNotificacions().get(0), "La primera notificació no és igual");
        assertEquals(expectedNot2, massivaFile.getNotificacions().get(1), "La segona notificació no és igual");
        assertEquals(expectedNot3, massivaFile.getNotificacions().get(2), "La tercera notificació no és igual");

    }

    private byte[] getResourceFileAsBytes(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getResource(fileName)).getFile()));
    }
}
