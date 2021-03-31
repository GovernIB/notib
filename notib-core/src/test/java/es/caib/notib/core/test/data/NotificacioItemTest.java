package es.caib.notib.core.test.data;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.test.AuthenticationTest;
import lombok.Getter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class NotificacioItemTest extends DatabaseItemTest<NotificacioDatabaseDto>{
    @Autowired
    protected NotificacioService notificacioService;
    @Autowired
    protected AuthenticationTest authenticationTest;
    @Autowired
    protected ProcedimentItemTest procedimentItemTest;

    @Getter
    private String[] relatedFields = new String[]{ "procediment" };

    @Override
    public NotificacioDatabaseDto create(NotificacioDatabaseDto element, Long entitatId) throws Exception{
        NotificacioDatabaseDto item = (NotificacioDatabaseDto) element;
        authenticationTest.autenticarUsuari("admin");
        NotificacioDatabaseDto entitatCreada = notificacioService.create(
                entitatId,
                item);
        return entitatCreada;
    }

    @Override
    public void delete(Long entitatId, NotificacioDatabaseDto object) {
        authenticationTest.autenticarUsuari("admin");
        notificacioService.delete(entitatId, object.getId());
    }

//    @Override
//    public NotificacioDatabaseDto getRandomInstance() throws Exception{
//        throw new Exception("Can't build a fully random notificacio");
//    }

    public void relateElement(String key, Object element) throws Exception{
        if (element instanceof ProcedimentDto) {
            this.objects.get(key).setProcediment((ProcedimentDto) element);
        }
    }

    public NotificacioDatabaseDto getRandomInstanceWithoutEnviaments() {
        String notificacioId = new Long(System.currentTimeMillis()).toString();

        DocumentDto document = new DocumentDto();
        try {
            byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
            document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
            document.setHash(
                    Base64.encodeBase64String(
                            Hex.decodeHex(
                                    DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
        } catch (IOException | DecoderException e) {
            e.printStackTrace();
        }

        document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
        document.setNormalitzat(false);
        document.setGenerarCsv(false);

        Date caducitat = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
        Date enviamentDataProgramada = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
        NotificacioDatabaseDto notCreated = NotificacioDatabaseDto.builder()
                .emisorDir3Codi(ConfigTest.ENTITAT_DGTIC_DIR3CODI)
                .enviamentTipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO)
                .enviamentDataProgramada(enviamentDataProgramada)
                .concepte("Test")
                .descripcio("Test descripció")
                .organGestorCodi("A00000000")
                .enviamentDataProgramada(new Date())
                .retard(5)
                .caducitat(caducitat)
//                .procediment(procediment)
//				.procedimentCodiNotib()
//                .grup(grupCreate)
                .enviaments(new ArrayList<NotificacioEnviamentDtoV2>())
                .usuariCodi("admin")
//				.motiu()
                .numExpedient("EXPEDIENTEX")
                .idioma(IdiomaEnumDto.CA)
                .document(new DocumentDto())
                .build();
        notCreated.setDocument(document);
        return notCreated;
    }
    public NotificacioDatabaseDto getRandomInstance() {
        NotificacioDatabaseDto notCreated = getRandomInstanceWithoutEnviaments();
        int numDestinataris = 2;
        List<NotificacioEnviamentDtoV2> enviaments = new ArrayList<>();
//		if (ambEnviamentPostal) {
//			PagadorPostal pagadorPostal = new PagadorPostal();
//			pagadorPostal.setDir3Codi("A04013511");
//			pagadorPostal.setFacturacioClientCodi("ccFac_" + notificacioId);
//			pagadorPostal.setContracteNum("pccNum_" + notificacioId);
//			pagadorPostal.setContracteDataVigencia(new Date(0));
//			notificacio.setPagadorPostal(pagadorPostal);
//			PagadorCie pagadorCie = new PagadorCie();
//			pagadorCie.setDir3Codi("A04013511");
//			pagadorCie.setContracteDataVigencia(new Date(0));
//			notificacio.setPagadorCie(pagadorCie);
//		}
        for (int i = 0; i < numDestinataris; i++) {
            NotificacioEnviamentDtoV2 enviament = new NotificacioEnviamentDtoV2();
            PersonaDto titular = new PersonaDto();
            titular.setInteressatTipus(InteressatTipusEnumDto.FISICA);
            titular.setNom("titularNom" + i);
            titular.setLlinatge1("titLlinatge1_" + i);
            titular.setLlinatge2("titLlinatge2_" + i);
            titular.setNif("00000000T");
            titular.setTelefon("666010101");
            titular.setEmail("titular@gmail.com");
            enviament.setTitular(titular);
            List<PersonaDto> destinataris = new ArrayList<PersonaDto>();
            PersonaDto destinatari = new PersonaDto();
            destinatari.setInteressatTipus(InteressatTipusEnumDto.FISICA);
            destinatari.setNom("destinatariNom" + i);
            destinatari.setLlinatge1("destLlinatge1_" + i);
            destinatari.setLlinatge2("destLlinatge2_" + i);
            destinatari.setNif("12345678Z");
            destinatari.setTelefon("666020202");
            destinatari.setEmail("destinatari@gmail.com");
            destinataris.add(destinatari);
            enviament.setDestinataris(destinataris);
            enviament.setServeiTipus(ServeiTipusEnumDto.URGENT);
            enviament.setNotificaEstat(NotificacioEnviamentEstatEnumDto.NOTIB_PENDENT);
            enviaments.add(enviament);
        }
        notCreated.setEnviaments(enviaments);

        return notCreated;
    }

    private InputStream getContingutNotificacioAdjunt() {
        return getClass().getResourceAsStream(
                "/es/caib/notib/core/notificacio_adjunt.pdf");
    }
}
