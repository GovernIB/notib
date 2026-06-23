import {Icon, TextareaAutosize, Tooltip} from '@mui/material';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {GridPage, MuiDataGrid, MuiDataGridColDef} from 'reactlib';
import Box from "@mui/material/Box";
import {GRID_DETAIL_PANEL_TOGGLE_COL_DEF, useGridApiRef} from "@mui/x-data-grid-pro";
import CustomDetailPanelToggle from "../../utils/CustomDetailPanelToggle.tsx";

const useDataGridColumns = (dataGridApiRef) => {
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
            {
                ...GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
                hideable: false,
                sortable: false,
                resizable: false,
                width: 90,
                align: 'center',
                renderCell: (params: any) => {
                    return (params?.row?.error && <CustomDetailPanelToggle id={params.id} value={params.value} />);
                },
            },
        ],
        []
    );
    return columns;
};

const NotificacioDetailTabRegistreEsdev: React.FC<{ id: any }> = (props) => {

    const { id } = props;
    const datagridApiRef = useGridApiRef();
    const columns = useDataGridColumns(datagridApiRef);

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title=''
                datagridApiRef={datagridApiRef}
                resourceName="eventResource"
                fixedFilter={'notificacio.id:' + id}
                columns={columns}
                readOnly
                getDetailPanelContent={({ row }) =>
                    (<Box sx={{ padding: '10px'}}>
                        <TextareaAutosize style={{width:'100%', height:'100px' }}>{row.errorDescripcio}</TextareaAutosize>
                    </Box>)
                }
                getDetailPanelHeight={() => 'auto'}
                toolbarHideQuickFilter
            />
        </GridPage>
    );
};

export default NotificacioDetailTabRegistreEsdev;
