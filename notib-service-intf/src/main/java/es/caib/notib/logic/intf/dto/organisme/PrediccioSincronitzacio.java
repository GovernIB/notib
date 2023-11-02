package es.caib.notib.logic.intf.dto.organisme;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrediccioSincronitzacio {

    MultiValuedMap<UnitatOrganitzativaDto, UnitatOrganitzativaDto> splitMap = new ArrayListValuedHashMap<>();
    MultiValuedMap<UnitatOrganitzativaDto, UnitatOrganitzativaDto>  mergeMap = new ArrayListValuedHashMap<>();
    MultiValuedMap<UnitatOrganitzativaDto, UnitatOrganitzativaDto>  substMap = new ArrayListValuedHashMap<>();
    List<UnitatOrganitzativaDto> unitatsVigents = new ArrayList<>();
    List<UnitatOrganitzativaDto> unitatsNew = new ArrayList<>();
    List<UnitatOrganitzativaDto> unitatsExtingides = new ArrayList<>();
    boolean isFirstSincronization;

    public Map<UnitatOrganitzativaDto, List<UnitatOrganitzativaDto>> getSplitMap() {

        Map<UnitatOrganitzativaDto, List<UnitatOrganitzativaDto>>  map = new HashMap<>();
        var keys = splitMap.keys();
        for (var key : keys) {
            map.put( key, (List<UnitatOrganitzativaDto>) splitMap.get(key));
        }
        return map;
    }

    public Map<UnitatOrganitzativaDto, List<UnitatOrganitzativaDto>> getMergeMap() {

        Map<UnitatOrganitzativaDto, List<UnitatOrganitzativaDto>>  map = new HashMap<>();
        var keys = mergeMap.keys();
        for (var key : keys) {
            map.put( key, (List<UnitatOrganitzativaDto>) mergeMap.get(key));
        }
        return map;
    }

    public Map<UnitatOrganitzativaDto, List<UnitatOrganitzativaDto>> getSubstMap() {

        Map<UnitatOrganitzativaDto, List<UnitatOrganitzativaDto>>  map = new HashMap<>();
        var keys = substMap.keys();
        for (var key : keys) {
            map.put( key, (List<UnitatOrganitzativaDto>) substMap.get(key));
        }
        return map;
    }

}
