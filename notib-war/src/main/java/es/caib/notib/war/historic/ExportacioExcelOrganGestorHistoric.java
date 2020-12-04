package es.caib.notib.war.historic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationOrganDto;
import es.caib.notib.core.api.dto.historic.HistoricMetriquesEnumDto;

public class ExportacioExcelOrganGestorHistoric extends ExportacioExcel {

	private static final List<HistoricMetriquesEnumDto> METRIQUES = Arrays.asList(
			HistoricMetriquesEnumDto.NOTIFICACIONS_TOTAL,
			HistoricMetriquesEnumDto.NOTIFICACIONS_CORRECTES,
			HistoricMetriquesEnumDto.NOTIFICACIONS_AMB_ERROR,
			HistoricMetriquesEnumDto.NOTIFICACIONS_PROCEDIMENT_COMU,
			HistoricMetriquesEnumDto.NOTIFICACIONS_AMB_GRUP,
			HistoricMetriquesEnumDto.NOTIFICACIONS_ORIGEN_API,
			HistoricMetriquesEnumDto.NOTIFICACIONS_ORIGEN_WEB,
			HistoricMetriquesEnumDto.COMUNICACIONS_TOTAL,
			HistoricMetriquesEnumDto.COMUNICACIONS_CORRECTES,
			HistoricMetriquesEnumDto.COMUNICACIONS_AMB_ERROR,
			HistoricMetriquesEnumDto.COMUNICACIONS_PROCEDIMENT_COMU,
			HistoricMetriquesEnumDto.COMUNICACIONS_AMB_GRUP,
			HistoricMetriquesEnumDto.COMUNICACIONS_ORIGEN_API,
			HistoricMetriquesEnumDto.COMUNICACIONS_ORIGEN_WEB, 
			HistoricMetriquesEnumDto.ENVIAMENTS,
			HistoricMetriquesEnumDto.PROCEDIMENTS,
			HistoricMetriquesEnumDto.GRUPS); 

	public ExportacioExcelOrganGestorHistoric() {
		super();
	}

	public byte[] convertDadesOrgansGestors(
			Map<OrganGestorDto, List<HistoricAggregationOrganDto>> dades) throws IOException {

		for (OrganGestorDto organGestor : dades.keySet()) {
			HSSFSheet sheet = wb.createSheet(organGestor.getCodi() + " - " + organGestor.getNom());
			createHeader(sheet, METRIQUES);
			
			int rowNum = 1;
			List<HistoricAggregationOrganDto> listHistorics = dades.get(organGestor);
			for (HistoricAggregationOrganDto historic : listHistorics) {
				HSSFRow xlsRow = sheet.createRow(rowNum);
				HSSFCell cellData = xlsRow.createCell(0);
				cellData.setCellValue(historic.getData());
				cellData.setCellStyle(cellDataStyle);
				
				int colNum = 1;
				for (HistoricMetriquesEnumDto metrica : METRIQUES) {
					HSSFCell cellUnitatNom = xlsRow.createCell(colNum);
					cellUnitatNom.setCellValue(metrica.getValue(historic));
					cellUnitatNom.setCellStyle(defaultStyle);
					colNum++;
				}

				rowNum++;	
			}
			
			for (int i = 0; i < METRIQUES.size() + 1; i++)
				sheet.autoSizeColumn(i);
		}

		byte[] bytes = wb.getBytes();

		wb.close();

		return bytes;
	}


}
