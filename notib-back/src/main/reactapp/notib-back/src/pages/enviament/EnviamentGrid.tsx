import React from 'react';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';
import { useEnviamentDetailDialog } from './EnviamentDetailDialog';

const EnviamentGrid = () => {
    const { t } = useTranslation();
    const { dialogComponent, onDetailClick } = useEnviamentDetailDialog();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'enviatDate',
            },
            {
                field: 'notificacioOrganGestor',
                flex: 2,
            },
            {
                field: 'notificacioProcediment',
                flex: 2,
            },
            {
                field: 'notificacioConcepte',
                flex: 2,
            },
            {
                field: 'titular',
                flex: 2,
            },
            {
                field: 'representant',
                flex: 2,
            },
            {
                field: 'notificaEstat',
                flex: 2,
            },
            {
                field: ' ',
                flex: 1.2,
                sortable: false,
                hideable: false,
                exportable: false,
                pinnable: false,
                renderCell: (params: any) => {
                    return (
                        <Button
                            variant="outlined"
                            size="small"
                            startIcon={<Icon>info</Icon>}
                            onClick={() => onDetailClick(params.id)}
                        >
                            {t('page.enviament.grid.detalls')}
                        </Button>
                    );
                },
            },
        ],
        []
    );
    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.enviament.grid.title')}
                resourceName="notificacioEnviamentResource"
                columns={columns}
                readOnly
                paginationActive
                toolbarType="upper"
                toolbarHideCreate
            />
            {dialogComponent}
        </GridPage>
    );
};

export default EnviamentGrid;
