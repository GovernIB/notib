package es.caib.notib.core.api.dto.config;

import lombok.Data;

import java.util.List;

@Data
public class ConfigGroupDto {
    private String description;
    private List<ConfigDto> configs;
    private List<ConfigGroupDto> innerConfigs;
}
