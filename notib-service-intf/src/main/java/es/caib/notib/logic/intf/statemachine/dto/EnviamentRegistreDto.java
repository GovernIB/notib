package es.caib.notib.logic.intf.statemachine.dto;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnviamentRegistreDto {

    // Entitat
    private String entitatCodi;
    private String entitatNom;
    private String entitatDir3Codi;

    // Notificacio
    private Long notificacioId;
    private String concepte;
    private String descripcio;
    private EnviamentTipus tipusEnviament;
    private NotificacioEstatEnumDto notificacioEstat;
    private String codiDir3Registre;
    private String organismeRegistre;
    private CodiNomDto oficinaRegistre;
    private CodiNomDto llibreRegistre;
    private String idioma;
    private String numExpedient;
    private String usuariCodi;

    // Procediment
    private String procedimentCodi;
    private String tipusAssumpte;
    private String codiAssumpte;

    // Interessats
    InteressatRegistreDto interessat;

    // Documents
    List<DocumentRegistreDto> documents;

    // Enviament
    private Long id;
    private String uuid;
    private Date registreData;
    private int registreIntent;

}
