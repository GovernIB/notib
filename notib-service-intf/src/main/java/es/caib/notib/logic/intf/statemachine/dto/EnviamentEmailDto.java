package es.caib.notib.logic.intf.statemachine.dto;

import es.caib.notib.client.domini.EnviamentTipus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnviamentEmailDto {

    // Entitat
    private String entitatCodi;
    private String entitatNom;

    // Notificacio
    private Long notificacioId;
    private String concepte;
    private String descripcio;
    private EnviamentTipus tipusEnviament;

    // Procediment
    private String procedimentNom;
    private String organNomCat;
    private String organNomEs;

    // Interessats
    private String titularNom;
    private String titularEmail;

    // Enviament
    private Long id;
    private String uuid;

}
