package es.caib.notib.logic.intf.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActiveMqInfo {

    private Long id;
    private String nom;
    private String descripcio;
    private long mida;
    private long consumersCount;
    private long enqueueCount;
    private long dequeueCount;
    private long forwardCount;
    private long inFlightCount;
    private long expiredCount;
    private long storeMessageSize;

}
