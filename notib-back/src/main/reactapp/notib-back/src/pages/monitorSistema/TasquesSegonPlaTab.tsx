import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    GridPage,
    MuiDataGrid,
    useBaseAppContext,
    useMuiDataGridApiRef,
    useResourceApiService,
} from 'reactlib';
import { Badge, Button, Icon } from '@mui/material';

const useTaskAction = (refresh?: () => void) => {

    const { t } = useTranslation();
    const { isReady: apiIsReady, artifactAction: apiAction } = useResourceApiService('backGroundTaskResource');
    const { temporalMessageShow } = useBaseAppContext();

    const restart = (id: any) => restartAll([id], false);

    const restartAll = (ids: any[], massivo: boolean = true) => {
        apiAction(undefined, { code: 'RESTART_TASK', data: { ids } })
            .then(() => {
                refresh?.();
                temporalMessageShow(
                    null,
                    massivo
                        ? t('page.monitorSistema.tab.tasques.restartSelectOk')
                        : t('page.monitorSistema.tab.tasques.restartOk'),
                    'success'
                );
            })
            .catch((error) => temporalMessageShow(null, error?.message, 'error'));
    };

    return {
        apiIsReady,
        restart,
        restartAll,
    };
};

const TasquesSegonPlaTab: React.FC = () => {

    const sortModelTasques: any[] = [{ field: 'id', sort: 'asc' }];
    const gridApiRef = useMuiDataGridApiRef();
    const { t } = useTranslation();
    const { restart, restartAll } = useTaskAction(gridApiRef?.current?.refresh);
    const [selectedRows, setGridSelectedRows] = React.useState<Set<string | number>>(new Set());

    const columns = [
        {
            field: 'nom',
            flex: 2,
        },
        {
            field: 'estat',
            flex: 1,
        },
        {
            field: 'dataInici',
            flex: 1,
        },
        {
            field: 'tempsExecucio',
            flex: 1,
        },
        {
            field: 'properaExecucio',
            flex: 1,
        },
    ];

    const actions = [
        {
            label: t('page.monitorSistema.tab.tasques.restart'),
            icon: 'cached',
            showInMenu: false,
            onClick: restart,
        },
    ];

    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title=""
                apiRef={gridApiRef}
                resourceName="backGroundTaskResource"
                columns={columns}
                sortModel={sortModelTasques}
                toolbarHideCreate
                toolbarElementsWithPositions={[
                    {
                        position: 2,
                        element: (
                            <Badge badgeContent={selectedRows.size} color="primary">
                                <Button
                                    variant="outlined"
                                    color="primary"
                                    startIcon={<Icon>cached</Icon>}
                                    disabled={selectedRows.size === 0}
                                    onClick={() => {
                                        restartAll([...selectedRows], true);
                                    }}
                                    sx={{ textTransform: 'none' }}
                                >
                                    {t('page.monitorSistema.tab.tasques.restartSelect')}
                                </Button>
                            </Badge>
                        ),
                    },
                ]}
                rowHideUpdateButton
                rowHideDeleteButton
                rowAdditionalActions={actions}
                selectionActive
                onRowSelectionModelChange={(rowSelectionModel) => {
                    setGridSelectedRows(rowSelectionModel?.ids);
                }}
            />
        </GridPage>
    );
};

export default TasquesSegonPlaTab;
