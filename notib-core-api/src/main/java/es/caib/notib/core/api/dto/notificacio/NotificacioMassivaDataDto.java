package es.caib.notib.core.api.dto.notificacio;

import es.caib.notib.core.api.dto.AuditoriaDto;
import es.caib.notib.core.api.dto.cie.OperadorPostalDto;
import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificacioMassivaDataDto extends AuditoriaDto {
    private Long id;
    private String csvFilename;
    private String zipFilename;
    private Date caducitat;
    private String email;
    private OperadorPostalDto pagadorPostal;
}
