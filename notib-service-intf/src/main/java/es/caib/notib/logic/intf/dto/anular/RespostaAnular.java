package es.caib.notib.logic.intf.dto.anular;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.caib.notib.client.domini.RespostaAnulacio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespostaAnular {

    List<RespostaAnulacio> respostes = new ArrayList<>();
    private List<String> noExecutades;

    public boolean isOk() {

        if (respostes == null || respostes.isEmpty()) {
            return false;
        }
        for (var resposta : respostes) {
            if (resposta.isError()) {
                return false;
            }
        }
        return true;
    }

    public void addResposta(RespostaAnulacio resposta) {
        respostes.add(resposta);
    }

    public List<String> getErrors() {

        List<String> errors = new ArrayList<>();
        for (var resposta : respostes) {
            if (!resposta.isError()) {
                continue;
            }
            errors.add(resposta.getCodiResposta() + " - " + resposta.getCodiResposta());
        }
        return errors;
    }
}
