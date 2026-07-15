import * as XLSX from "xlsx";

export const gridRegistry = new Map<string, { columns: any[]; rows: any[] }>();

const autoFitColumns = (worksheet: { [x: string]: any; }, aoa: string | any[]) => {

    const widths = aoa[0].map((_: any, colIdx: string | number) => {
        let maxLen = 10; // minimum width
        for (const element of aoa) {
            const cell = element[colIdx];
            const text = cell === null || cell === undefined ? "" : String(cell);
            maxLen = Math.max(maxLen, text.length);
        }
        return { wch: Math.min(80, maxLen + 2) };
    });
    worksheet["!cols"] = widths;
}

export function exportarGridToExcel(gridIds: string[], fileName = "grids.xlsx") {

    const workbook = XLSX.utils.book_new();
    gridIds.forEach((gridId) => {
        const entry = gridRegistry.get(gridId);
        if (!entry) {
            return;
        }
        const { columns, rows } = entry;

        const visibleColumns = (columns || []).filter((c) => c.field);
        const headers = visibleColumns.map((c) => c.headerName ? c.headerName : c.field);
        const fields = visibleColumns.map((c) => c.field);

        const aoa = [
            headers,
            ...(rows || []).map((row) =>
                fields.map((f) => {
                    const v = row?.[f];
                    if (v === true) {
                        return "✓";
                    }
                    if (v === false) {
                        return "";
                    }
                    return v ?? "";
                })
            ),
        ];

        const worksheet = XLSX.utils.aoa_to_sheet(aoa);
        autoFitColumns(worksheet, aoa);

        // Excel sheet name must be <= 31 chars, and can't contain : \ / ? * [ ]
        const safeSheetName = String(gridId).replace(/[:\\/?*\[\]]/g, "").slice(0, 31);

        XLSX.utils.book_append_sheet(workbook, worksheet, safeSheetName || "Sheet");
    });

    XLSX.writeFile(workbook, fileName);
}
