import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

const NotificacioDetailTabHistoric: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'tipusOperacio',
                flex: 1,
            },
            {
                field: 'joinPoint',
                flex: 3,
            },
            {
                field: 'createdBy',
                flex: 1,
            },
            {
                field: 'createdDate',
                flex: 1,
            },
            {
                field: 'notificacioId',
                flex: 1,
            },
            {
                field: 'comunicacioTipus',
                flex: 1,
            },
            {
                field: 'tipusUsuari',
                flex: 1,
            },
            {
                field: 'usuari',
                flex: 1,
            },
            {
                field: 'emisor',
                flex: 1,
            },
            {
                field: 'tipus',
                flex: 1,
            },
            {
                field: 'entitatId',
                flex: 1,
            },
            {
                field: 'organ',
                flex: 1,
            },
            {
                field: 'procediment',
                flex: 1,
            },
            {
                field: 'grup',
                flex: 1,
            },
            {
                field: 'concepte',
                flex: 1,
            },
            {
                field: 'descripcio',
                flex: 1,
            },
            {
                field: 'numExpedient',
                flex: 1,
            },
            {
                field: 'enviamentDataProgramada',
                flex: 1,
            },
            {
                field: 'retard',
                flex: 1,
            },
            {
                field: 'caducitat',
                flex: 1,
            },
            {
                field: 'documentId',
                flex: 1,
            },
            {
                field: 'estat',
                flex: 1,
            },
            {
                field: 'estatDate',
                flex: 1,
            },
            {
                field: 'estatProcessatDate',
                flex: 1,
            },
            {
                field: 'motiu',
                flex: 1,
            },
            {
                field: 'registreEnviamentIntent',
                flex: 1,
            },
            {
                field: 'registreNumero',
                flex: 1,
            },
            {
                field: 'registreNumeroFormatat',
                flex: 1,
            },
            {
                field: 'registreData',
                flex: 1,
            },
            {
                field: 'notificaEnviamentData',
                flex: 1,
            },
            {
                field: 'notificaEnviamentIntent',
                flex: 1,
            },
            {
                field: 'errorLastCallback',
                flex: 1,
            },
            {
                field: 'errorEventId',
                flex: 1,
            },
            {
                field: 'referencia',
                flex: 1,
            }
        ],
        []
    );

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title=''
                resourceName="notificacioAuditResource"
                fixedFilter={'notificacioId:' + id}
                columns={columns}
                readOnly
                toolbarHideQuickFilter
            />
        </GridPage>
    );
};

export default NotificacioDetailTabHistoric;
