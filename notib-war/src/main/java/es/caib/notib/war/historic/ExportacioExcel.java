package es.caib.notib.war.historic;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.notib.core.api.dto.historic.HistoricMetriquesEnumDto;

public class ExportacioExcel {

	protected HSSFWorkbook wb;
	private HSSFFont bold;
	private HSSFFont greyFont;
	private DataFormat format;
	protected HSSFCellStyle cellDataStyle;
	protected HSSFCellStyle defaultStyle;

	public ExportacioExcel() {

		wb = new HSSFWorkbook();

		bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.WHITE.index);

		greyFont = wb.createFont();
		greyFont.setColor(HSSFColor.GREY_25_PERCENT.index);
		greyFont.setCharSet(HSSFFont.ANSI_CHARSET);
		format = wb.createDataFormat();

		cellDataStyle = wb.createCellStyle();
		cellDataStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd-MM-yyyy"));
		cellDataStyle.setWrapText(true);

		defaultStyle = wb.createCellStyle();
		defaultStyle.setDataFormat(format.getFormat("0"));
	}

	protected void createHeader(HSSFSheet sheet, List<?> columns) {
		HSSFFont bold;
		HSSFCellStyle headerStyle;

		bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.WHITE.index);

		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(HSSFColor.GREY_80_PERCENT.index);
		headerStyle.setFont(bold);
		int rowNum = 0;
		int colNum = 0;

		// Capçalera
		HSSFRow xlsRow = sheet.createRow(rowNum++);
		HSSFCell cell;

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Data"));
		cell.setCellStyle(headerStyle);

		for (Object c : columns) {
			cell = xlsRow.createCell(colNum++);
			cell.setCellValue(new HSSFRichTextString(c.toString()));
			cell.setCellStyle(headerStyle);
		}
	}

	protected static final Logger logger = LoggerFactory.getLogger(ExportacioExcel.class);

}
