package es.caib.notib.war.historic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.util.HSSFColor;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationGrupDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationProcedimentDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationUsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricMetriquesEnumDto;


public class ExportacioExcelUsuariHistoric extends ExportacioExcel {


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
			HistoricMetriquesEnumDto.GRUPS); 

	public ExportacioExcelUsuariHistoric() {
		super();
	}

	public byte[] convertDadesUsuari(
			Map<UsuariDto, List<HistoricAggregationUsuariDto>> dades) throws IOException {

		for (UsuariDto usuari : dades.keySet()) {
			HSSFSheet sheet = wb.createSheet(usuari.getCodi() + " - " + usuari.getNom());
			createHeader(sheet, METRIQUES);
			
			int rowNum = 1;
			List<HistoricAggregationUsuariDto> listHistorics = dades.get(usuari);
			for (HistoricAggregationUsuariDto historic : listHistorics) {
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
