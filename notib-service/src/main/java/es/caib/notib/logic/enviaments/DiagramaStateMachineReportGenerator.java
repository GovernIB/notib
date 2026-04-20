package es.caib.notib.logic.enviaments;

import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.ReportGenerationException;
import es.caib.notib.logic.intf.base.model.DownloadableFile;
import es.caib.notib.logic.intf.base.model.ReportFileType;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DiagramaStateMachineReportGenerator implements BaseReadonlyResourceService.ReportGenerator<NotificacioEnviamentResourceEntity, Serializable, NotificacioEnviamentResource> {

	@Override
	public List<NotificacioEnviamentResource> generateData(String code, NotificacioEnviamentResourceEntity entity, Serializable params) throws ReportGenerationException {
		return List.of();
	}

	@Override
	public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {

		var input = this.getClass().getClassLoader().getResourceAsStream("es/caib/notib/logic/statemachine/diagramaStateMachine.png");
		var nom = "diagramaStateMachine.png";
		if (input == null) {
			log.error("Error generant InputStream per diagramaStatemachine.png");
			return DownloadableFile.builder().name(nom).content(new byte[]{}).contentType("image/png").build();
		}
		try {
			var content = IOUtils.toByteArray(input);
			return DownloadableFile.builder().name("diagramaStateMachine.png").content(content).contentType("image/png").build();
		} catch (Exception ex) {
			log.error("Error convertint a byte[] l'InputStream per diagramaStatemachine.png");
			return DownloadableFile.builder().name("diagramaStateMachine.png").content(new byte[]{}).contentType("image/png").build();
		}
	}


	@Override
	public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {

	}
}
