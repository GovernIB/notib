package es.caib.notib.logic.intf.statemachine.dto;

import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRegistreDto {
    protected String titol;
    protected String nom;
    protected byte[] contingut;
    protected String mimeType;
    protected TipusDocumentalEnum tipusDocumental;
    protected OrigenEnum origen;
    protected ValidesaEnum validesa;
    protected Boolean modeFirma;
    protected Date dataCaptura;
    protected String csv;
}
