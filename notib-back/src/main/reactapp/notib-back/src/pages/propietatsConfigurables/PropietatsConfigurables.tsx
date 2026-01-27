import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

const PropietatsConfigurables: React.FC<any> = () => {
    const { t } = useTranslation();

    const columns: MuiDataGridColDef[] = React.useMemo(() => {
        return [
            { field: 'key', flex: 1 },
            { field: 'description', flex: 2.5 },
            // {
            //     field: 'treePath',
            //     flex: 1.2,
            //     headerName: t('page.tasques.grid.column.appEntorn'), // TODO: Posar TRAD
            //     valueFormatter: (value: any) =>
            //         //   value?.[0].startsWith(INVALID_ENTORNAPP)
            //         //       ? treePathFormatInvalidEntornApp(value[0])
            //         //       : `${value?.[0]} - ${value?.[1]}`,
            //         `${value?.[0]} - ${value?.[1]}`,
            // },
        ];
    }, [t]);

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.propietatsConfiguracio.grid.title')}
                resourceName="configGroupResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};
export default PropietatsConfigurables;
