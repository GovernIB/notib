import React from 'react';
import {useTranslation} from 'react-i18next';
import {GridPage, MuiDataGridColDef, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService,} from 'reactlib';
import {Badge, Button, Icon} from "@mui/material";
import {GridApiPro, useGridApiRef} from "@mui/x-data-grid-pro";
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../utils/icons';
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";

const useCacheAction = (apiRef: React.RefObject<GridApiPro | null>, gridApiRef: React.RefObject<any>) => {

    const { t } = useTranslation();
    const { isReady: apiIsReady, artifactAction: apiAction } = useResourceApiService('cacheResource');
    const { temporalMessageShow } = useBaseAppContext();

    const restart = (id: any) => restartAll([id], false);

    const restartAll = (ids: any[], massivo: boolean = true) => {
        apiAction(undefined, { code: 'BUIDAR_CACHE', data: { ids } })
            .then(() => {
                gridApiRef.current?.refresh?.();     // <- refresca la taula
                const msg = t('page.cache.accions.' + (massivo ? 'buidarMassiuOk' : 'buidarOk'));
                temporalMessageShow(null, msg , 'success');
            })
            .catch(error => temporalMessageShow(null, error?.message, 'error'))
            .finally(() => apiRef?.current?.selectRows([], false, true))
    };
    return { apiIsReady, restart, restartAll };
};

const useDataGridColumns = (datagridApiRef: any) => {

    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'codi',
                flex: 2,
            },
            {
                field: 'descripcio',
                flex: 4,
            },
            {
                field: 'localHeapSize',
                flex: 1,
            }
        ],
        [datagridApiRef]
    );

    return columns;
}

export const CacheGrid = () => {

    const { t } = useTranslation();
    const gridApiRef = useMuiDataGridApiRef();
    const datagridApiRef = useGridApiRef();
    const columns = useDataGridColumns(gridApiRef);
    const [selectedRows, setSelectedRows] = React.useState<Set<string | number>>(new Set());
    const { restart, restartAll } = useCacheAction(datagridApiRef, gridApiRef);
    const accions = [
        {
            label: t('page.cache.accions.buidar'),
            icon: 'delete',
            showInMenu: false,
            onClick: restart,
        },
    ];
    return (
        <>
            <PageTitle title={t('page.cache.grid.title')}></PageTitle>
            <GridPage>
                <ScreenFrame icon={icons.cache} title={t('page.cache.grid.title')} >
                    <StyledMuiGrid // TODO FALTA CANViar
                        apiRef={gridApiRef}
                        datagridApiRef={datagridApiRef}
                        resourceName="cacheResource"
                        columns={columns}
                        toolbarHideQuickFilter
                        toolbarType="upper"
                        rowHideUpdateButton
                        rowHideDeleteButton
                        rowAdditionalActions={accions}
                        selectionActive
                        rowSelectionModel={{type: 'include', ids: selectedRows,}}
                        onRowSelectionModelChange={rowSelectionModel => setSelectedRows(new Set(rowSelectionModel))}
                        toolbarElementsWithPositions={[
                            {
                                position: 2,
                                element: (
                                    <Badge badgeContent={selectedRows.size} color="primary">
                                        <Button
                                            variant="outlined"
                                            color="primary"
                                            startIcon={<Icon>delete</Icon>}
                                            disabled={selectedRows.size === 0}
                                            onClick={() => restartAll([...selectedRows], true)}
                                            sx={{ textTransform: 'none' }}
                                        >
                                            {t('page.cache.accions.buidarMassiu')}
                                        </Button>
                                    </Badge>
                                ),
                            },
                        ]}
                    />
                </ScreenFrame>
            </GridPage>
        </>
    );
};

export default CacheGrid;
