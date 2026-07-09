import React from 'react';
import {useTranslation} from 'react-i18next';
import {GridPage, MuiActionReportButton, MuiDataGrid, MuiDataGridColDef, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService,} from 'reactlib';
import {useActiveMqDetailDetailDialog} from "./ActivmeMqDetailDialog.tsx";


const useActiveMqAction = (refresh?: () => void) => {

    const { t } = useTranslation();
    const { isReady: apiIsReady, artifactAction: apiAction } = useResourceApiService('activeMqResource');
    const { temporalMessageShow } = useBaseAppContext();

    const buidar = (ids: any[]) => {
        apiAction(undefined, { code: 'BUIDAR_CUA', data: { ids } })
            .then(() => {
                refresh?.();
                const msg = t('page.activemq.buidarOk');
                temporalMessageShow(null, msg , 'success');
            })
            .catch(error => temporalMessageShow(null, error?.message, 'error'))
    };

    return { apiIsReady, buidar };
};

export const ActiveMqGrid = () => {

    const { t } = useTranslation();
    const gridApiRef = useMuiDataGridApiRef();
    const { buidar } = useActiveMqAction(gridApiRef?.current?.refresh);
    const { dialogComponentActiveMq, onDetailClickActiveMq } = useActiveMqDetailDetailDialog();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'nom',
                flex: 1.5
            },
            {
                field: 'descripcio',
                flex: 4,
            },
            {
                field: 'mida',
                flex: 1,
            },
            {
                field: 'enqueueCount',
                flex: 1,
            },
            {
                field: 'dequeueCount',
                flex: 1,
            },
            {
                field: 'forwardCount',
                flex: 1,
            },
            {
                field: 'inFlightCount',
                flex: 1,
            },
            {
                field: 'expiredCount',
                flex: 1,
            },
            {
                field: 'storeMessageSize',
                flex: 1,
            }

        ],
        []
    );

    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.activemq.grid.title')}
                resourceName="activeMqResource"
                columns={columns}
                toolbarType="upper"
                toolbarHideQuickFilter
                checkboxSelection={false}
                toolbarElementsWithPositions={[{
                    position: 2,
                    element: <MuiActionReportButton
                                resourceName={"activeMqResource"}
                                report="DESCARREGAR_JOB_SCHEDULER_JSON"
                                reportFileType="CUSTOM"
                                title={t('page.activemq.descargarJobScheduler')}
                                buttonComponentProps={{variant: "contained",size:"small"}}
                                buttonIcon="file_download"/>
                }]}
                rowAdditionalActions={[
                    {
                        label: t('page.activemq.grid.missatges'),
                        title: t('page.activemq.grid.missatges'),
                        icon: 'info',
                        showInMenu: true,
                        onClick: id => onDetailClickActiveMq(id),
                    },
                    {
                        label: t('page.activemq.grid.buidar'),
                        title: t('page.activemq.grid.buidar'),
                        icon: 'delete',
                        showInMenu: true,
                        onClick: id => buidar([id]),
                    }
                ]}
            />
            {dialogComponentActiveMq}
        </GridPage>
    );
};

export default ActiveMqGrid;
