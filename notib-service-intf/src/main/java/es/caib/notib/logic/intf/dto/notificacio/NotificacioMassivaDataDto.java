package es.caib.notib.logic.intf.dto.notificacio;

import es.caib.notib.logic.intf.dto.AuditoriaDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificacioMassivaDataDto extends AuditoriaDto {

    private Long id;
    private String csvFilename;
    private String zipFilename;
    private Date createdDate;
    private Date caducitat;
    private String email;
    private OperadorPostalDto pagadorPostal;
}
