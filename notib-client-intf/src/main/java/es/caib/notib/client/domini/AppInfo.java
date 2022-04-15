package es.caib.notib.client.domini;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.client.util.TrimStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppInfo {

	@JsonDeserialize(using = TrimStringDeserializer.class)
	String nom;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	String versio;
	@JsonDeserialize(using = TrimStringDeserializer.class)
	String data;
	
}
