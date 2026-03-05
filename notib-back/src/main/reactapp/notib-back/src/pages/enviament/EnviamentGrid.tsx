import React from 'react';
import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

const EnviamentGrid = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'enviamentTipus',
                headerName: '',
                flex: 0.4,
                renderCell: (params: any) => {
                    const letter = params.value?.substring(0, 1);
                    return <Chip label={letter} size="small" title={params.formattedValue} />;
                },
            },
            {
                field: 'createdDate',
                flex: 1.4,
            },
            {
                field: 'enviadaDate',
                flex: 1.4,
            },
            {
                field: 'organGestor',
                flex: 2,
            },
            {
                field: 'procediment',
                flex: 2,
                renderCell: (params: any) => {
                    const letter = params.row.procediment != null ? 'P' : 'S';
                    const title =
                        letter === 'P'
                            ? t('page.notificacio.grid.procediment')
                            : t('page.notificacio.grid.servei');
                    return (
                        <>
                            <Chip label={letter} size="small" title={title} sx={{ mr: 1 }} />
                            {params.formattedValue}
                        </>
                    );
                },
            },
            {
                field: 'numExpedient',
                flex: 1,
            },
            {
                field: 'concepte',
                flex: 3,
            },
            {
                field: 'estat',
                flex: 1,
            },
        ],
        []
    );
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.enviament.grid.title')}
                resourceName="notificacioEnviamentResource"
                columns={columns}
                paginationActive
                toolbarHideCreate
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
                readOnly
            />
        </GridPage>
    );
};

export default EnviamentGrid;
