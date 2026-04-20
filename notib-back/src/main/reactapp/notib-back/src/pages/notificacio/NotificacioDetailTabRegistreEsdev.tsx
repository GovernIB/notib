import { Icon, Tooltip } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

const NotificacioDetailTabRegistreEsdev: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'data',
                flex: 1,
            },
            {
                field: 'tipus',
                flex: 3,
            },
            {
                field: 'error',
                flex: 1,
                renderCell: (params: any) => (
                    <Tooltip
                        title={
                            params.value
                                ? t('page.enviament.detail.tab.registreEsdev.estatError')
                                : t('page.enviament.detail.tab.registreEsdev.estatSuccess')
                        }
                    >
                        <Icon color={params.value ? 'error' : 'success'}>
                            {params.value ? 'error' : 'done'}
                        </Icon>
                    </Tooltip>
                ),
            },
            {
                field: 'intents',
                flex: 1,
            },
        ],
        []
    );

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title=''
                resourceName="eventResource"
                fixedFilter={'notificacio.id:' + id}
                columns={columns}
                readOnly
                toolbarHideQuickFilter
            />
        </GridPage>
    );
};

export default NotificacioDetailTabRegistreEsdev;
