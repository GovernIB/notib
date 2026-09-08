import {Icon, TextareaAutosize, Tooltip} from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';
import {GRID_DETAIL_PANEL_TOGGLE_COL_DEF} from "@mui/x-data-grid-pro";
import CustomDetailPanelToggle from "../../utils/CustomDetailPanelToggle.tsx";
import Box from "@mui/material/Box";

const EnviamentDetailTabRegistreEsdev: React.FC<{ id: any }> = (props) => {
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
                renderCell: (params: any) => {
                    return (
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
                    )

                },
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
                    return (params?.row?.error && <CustomDetailPanelToggle
                        id={params.id}
                        value={params.value}
                        msgMostrar={t('page.notificacio.grid.column.mostrar')}
                        msgOcultar={t('page.notificacio.grid.column.ocultar')}
                    />);
                },
            },
        ],
        []
    );

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title=''
                resourceName="eventResource"
                fixedFilter={'enviament.id:' + id}
                columns={columns}
                readOnly
                toolbarHideQuickFilter
                getDetailPanelContent={({ row }) =>
                    (<Box sx={{ padding: '10px'}}>
                        <TextareaAutosize style={{width:'100%', height:'100px' }} readOnly value={row.errorDescripcio} />
                    </Box>)
                }
                getDetailPanelHeight={() => 'auto'}
            />
        </GridPage>
    );
};

export default EnviamentDetailTabRegistreEsdev;
