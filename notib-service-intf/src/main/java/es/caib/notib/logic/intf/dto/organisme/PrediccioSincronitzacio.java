package es.caib.notib.logic.intf.dto.organisme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrediccioSincronitzacio {

    MultiValuedMap splitMap = new ArrayListValuedHashMap();
    MultiValuedMap  mergeMap = new ArrayListValuedHashMap();
    MultiValuedMap  substMap = new ArrayListValuedHashMap();
    List<UnitatOrganitzativaDto> unitatsVigents = new ArrayList<>();
    List<UnitatOrganitzativaDto> unitatsNew = new ArrayList<>();
    List<UnitatOrganitzativaDto> unitatsExtingides = new ArrayList<>();
    boolean isFirstSincronization;

}
