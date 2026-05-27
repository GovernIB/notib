import {Box, Grid, Icon, IconButton} from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    GridPage, MuiActionReportButton,
    MuiDataGrid,
    MuiDataGridColDef,
    springFilterBuilder as filterBuilder,
    useFilterApiContext, useMuiActionReportLogic, useResourceApiService,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import {useGridApiRef} from "@mui/x-data-grid-pro";
import {DataCommonAdditionalAction} from "../../../lib/components/mui/datacommon/MuiDataCommon.tsx";

const useDataGridColumns = (datagridApiRef: any) => {
    const { t } = useTranslation();

    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'createdDate',
                headerName: t('page.notificacioMassiva.grid.createdDate'),
                width: 150,
            },
            {
                field: 'csvFilename',
                headerName: t('page.notificacioMassiva.grid.csvFilename'),
                flex: 1,
                renderCell: (params: any) => {
                    return (
                        <>
                            {params.row.csvFilename}
                            <Box sx={{ display: 'flex', justifyContent: 'end' }}>
                                <MuiActionReportButton
                                    id={params?.id}
                                    resourceName={"notificacioMassivaResource"}
                                    report="DESCARREGAR_FITXER_CSV_NOTIFICACIO_MASSIVA"
                                    reportFileType="CUSTOM"
                                    title={''}
                                    buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                                    buttonIcon="file_download"/>
                            </Box>
                        </>
                    );
                },
            },
            {
                field: 'zipFilename',
                headerName: t('page.notificacioMassiva.grid.zipFilename'),
                flex: 1,
                renderCell: (params: any) => {
                    return (
                        <>
                            {params.row.zipFilename}
                            <Box >
                                <MuiActionReportButton
                                    id={params?.id}
                                    resourceName={"notificacioMassivaResource"}
                                    report="DESCARREGAR_FITXER_ZIP_NOTIFICACIO_MASSIVA"
                                    reportFileType="CUSTOM"
                                    title={''}
                                    buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                                    buttonIcon="file_download"/>
                            </Box>
                        </>
                    );
                },
            },
            {
                field: 'estatValidacio',
                headerName: t('page.notificacioMassiva.grid.estatValidacio'),
                flex: 0.6,
            },
            {
                field: 'estatProces',
                headerName: t('page.notificacioMassiva.grid.estatProces'),
                flex: 0.6,
            },

        ],
        []
    );
return columns;
};


const useSpringFilterBuilder = () => {
    return (data: any) => {
        return filterBuilder.and(
            filterBuilder.eq('estatProces', `'${data?.estatProces}'`),
            data?.dataIniciInici &&
            filterBuilder.gte('createdDate', `'${formatStartOfDay(data?.dataIniciInici)}'`),
            data?.dataIniciFi &&
            filterBuilder.lte('createdDate', `'${formatEndOfDay(data?.dataIniciFi)}'`),
        );
    };
};

const ContentFilter: React.FC = () => {

    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();

    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };

    return (
        <Grid container spacing={1}>
            <GridFormField size={1.75} name="dataIniciInici" />
            <GridFormField size={1.75} name="dataIniciFi" />
            <GridFormField size={2.5} name="estatProces" />

            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

export const NotifiacioMassivaGrid = () => {

    const { t } = useTranslation();
    const springFilterBuilder = useSpringFilterBuilder();
    const filterDataGridProps = useDatagridFilterProps(
        'notificacioMassivaResource',
        'FILTER_NOTIFICACIO_MASSIVA',
        springFilterBuilder,
        <ContentFilter />
    );
    const datagridApiRef = useGridApiRef();
    const columns = useDataGridColumns(datagridApiRef);
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();

    const { exec: descarregarResum } = useMuiActionReportLogic(
        'notificacioMassivaResource',
        undefined,
        'DESCARREGAR_FITXER_RESUM_NOTIFICACIO_MASSIVA',
        'CUSTOM'
    );

    const { exec: descarregarErrorsValidacio } = useMuiActionReportLogic(
        'notificacioMassivaResource',
        undefined,
        'DESCARREGAR_FITXER_ERRORS_VALIDACIO_NOTIFICACIO_MASSIVA',
        'CUSTOM'
    );

    const { exec: descarregarErrorsExecucio } = useMuiActionReportLogic(
        'notificacioMassivaResource',
        undefined,
        'DESCARREGAR_FITXER_ERRORS_EXECUCIO_NOTIFICACIO_MASSIVA',
        'CUSTOM'
    );
    //
    // const { exec: posposarAccioMassiva } = useMuiActionReportLogic(
    //     'notificacioMassivaResource',
    //     'POSPOSAR_NOTIFICACIO_MASSIVA',
    //     // undefined,
    //     // undefined,
    //     // true,
    //     // "missatge confirmacio",
    //     // undefined,
    // );


    const { exec: reactivarAccioMassiva } = useMuiActionReportLogic(
        'notificacioMassivaResource',
        'REACTIVAR_NOTIFICACIO_MASSIVA',
        undefined,
    );

    const rowAdditionalActions = () => {

        const { artifactAction: apiAction } = useResourceApiService('notificacioMassivaResource');
        const listActions: DataCommonAdditionalAction[] = [
            {
                label: t('page.notificacioMassiva.grid.accions.resum'),
                title: t('page.notificacioMassiva.grid.accions.resum'),
                icon: 'info',
                showInMenu: true,
                onClick: (id) => console.error(`En construcció: ${id}`),
            },
            {
                label: t('page.notificacioMassiva.grid.accions.descarregarResum'),
                title: t('page.notificacioMassiva.grid.accions.descarregarResum'),
                icon: 'download',
                showInMenu: true,
                onClick: (id) => descarregarResum(id),
            },
            {
                label: t('page.notificacioMassiva.grid.accions.errorsValidacio'),
                title: t('page.notificacioMassiva.grid.accions.errorsValidacio'),
                icon: 'download',
                showInMenu: true,
                onClick: (id) => descarregarErrorsValidacio(id),
            },
            {
                label: t('page.notificacioMassiva.grid.accions.errorsExecucio'),
                title: t('page.notificacioMassiva.grid.accions.errorsExecucio'),
                icon: 'download',
                showInMenu: true,
                onClick: (id) => descarregarErrorsExecucio(id),
            },
            {
                label: t('page.notificacioMassiva.grid.accions.posposar'),
                title: t('page.notificacioMassiva.grid.accions.posposar'),
                icon: 'access_time',
                showInMenu: true,
                onClick: (id) => apiAction(id, { code: 'POSPOSAR_NOTIFICACIO_MASSIVA', data: { id } }),// posposarAccioMassiva(id),
            },
            {
                label: t('page.notificacioMassiva.grid.accions.reactivar'),
                title: t('page.notificacioMassiva.grid.accions.reactivar   '),
                icon: 'bolt',
                showInMenu: true,
                onClick: (id) => reactivarAccioMassiva(id),
            },
            {
                label: t('page.notificacioMassiva.grid.accions.mostrarRemeses'),
                title: t('page.notificacioMassiva.grid.accions.mostrarRemeses'),
                icon: 'list',
                showInMenu: true,
                onClick: (id) => console.error(`En construcció: ${id}`),
            }
        ];

        return listActions;
    };

    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.notificacioMassiva.grid.title')}
                resourceName="notificacioMassivaResource"
                columns={columns}
                paginationActive
                // persistentStateActive // TODO DESCOMENTAR ABANS DE PUJAR
                // persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarCreateLink="form"
                rowAdditionalActions={rowAdditionalActions()}
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default NotifiacioMassivaGrid;
