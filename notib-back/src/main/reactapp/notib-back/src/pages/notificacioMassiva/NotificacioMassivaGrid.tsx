import {Box, Chip, Grid, Icon, IconButton} from '@mui/material';
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
import Typography from "@mui/material/Typography";
import { useNotificacioMassivaResumDialog } from './NotificacioMassivaResumDialog';
import {useNavigate} from "react-router-dom";

const iconOk= React.cloneElement(<Icon>check</Icon>, { fontSize: "inherit", sx: { verticalAlign: "center"} });
const iconError= React.cloneElement(<Icon>close</Icon>, { fontSize: "inherit", sx: { verticalAlign: "center" } });
const iconCancelada= React.cloneElement(<Icon>block</Icon>, { fontSize: "inherit", sx: { verticalAlign: "center", ml: 0.1 } });

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
                        <Box sx={{ display: 'flex', alignItems:'center', flexDirection: 'row', justifyContent: 'space-between', width: '100%'}}>
                            <Typography>{params.row.csvFilename}</Typography>
                            <MuiActionReportButton
                                id={params?.id}
                                resourceName={"notificacioMassivaResource"}
                                report="DESCARREGAR_FITXER_CSV_NOTIFICACIO_MASSIVA"
                                reportFileType="CUSTOM"
                                title={t('page.notificacioMassiva.grid.csvTooltip')}
                                iconComponentProps={{fontSize: '10px', color:'primary'}}
                                icon="file_download"/>
                        </Box>
                    );
                },
            },
            {
                field: 'zipFilename',
                headerName: t('page.notificacioMassiva.grid.zipFilename'),
                flex: 1,
                renderCell: (params: any) => {
                    return (
                        <Box sx={{ display: 'flex', alignItems:'center', flexDirection: 'row', justifyContent: 'space-between', width: '100%'}}>
                            <Typography>{params.row.zipFilename}</Typography>
                            <MuiActionReportButton
                                id={params?.id}
                                resourceName={"notificacioMassivaResource"}
                                report="DESCARREGAR_FITXER_ZIP_NOTIFICACIO_MASSIVA"
                                reportFileType="CUSTOM"
                                title={t('page.notificacioMassiva.grid.zipTooltip')}
                                iconComponentProps={{fontSize: '10px', color:'primary'}}
                                icon="file_download"/>
                        </Box>
                    );
                },
            },
            {
                field: 'estatValidacio',
                headerName: t('page.notificacioMassiva.grid.estatValidacio'),
                flex: 1,
                renderCell: (params: any) => {

                    const labelText = t(`page.notificacioMassiva.grid.estats.${params.row.estatValidacio}`);
                    const totalNotificacions = params.row.totalNotificacions;
                    const notificacionsValidades = params.row.notificacionsValidades;
                    const descarregar = (
                        <MuiActionReportButton
                            id={params?.id}
                            resourceName={"notificacioMassivaResource"}
                            report="DESCARREGAR_FITXER_ERRORS_VALIDACIO_NOTIFICACIO_MASSIVA"
                            reportFileType="CUSTOM"
                            title={t('page.notificacioMassiva.grid.zipTooltip')}
                            iconComponentProps={{fontSize: '10px', color:'primary'}}
                            icon="file_download"/>);

                    switch (params.row.estatValidacio) {
                        case "PENDENT":
                            return (<Chip label={`${labelText} [${totalNotificacions}]`} variant="outlined"/>)
                        case "FINALITZAT_AMB_ERRORS": {
                            return (
                                <Box display="flex" alignItems="center" justifyContent="space-between" width="100%" gap={1}>
                                    <Chip label= {<>{labelText} [{iconOk} {notificacionsValidades}{" / "}{iconError} {totalNotificacions - notificacionsValidades}]</>} color="warning"/>
                                    {descarregar}
                                </Box>);
                        }
                        case "FINALITZAT":
                            return (<Chip label={<>{labelText} {"["}{iconOk} {notificacionsValidades}{"]"}</>} color="success"/>);
                        case "ERRONIA":
                            return (
                                <Box display="flex" alignItems="center" justifyContent="space-between" width="100%" gap={1}>
                                    <Chip label={<>{labelText} {"["}{iconError} {totalNotificacions}{"]"}</>} color="error"/>
                                    {descarregar}
                                </Box>);
                        default:
                            return <Chip label={labelText} />;
                    }
                }
            },
            {
                field: 'estatProces',
                headerName: t('page.notificacioMassiva.grid.estatProces'),
                flex: 1,
                renderCell: (params: any) => {

                    const labelText = t(`page.notificacioMassiva.grid.estats.${params.row.estatProces}`);
                    const notificacionsProcessades = params.row.notificacionsProcessades;
                    const notificacionsProcessadesAmbError = params.row.notificacionsProcessadesAmbError;
                    const notificacionsValidades = params.row.notificacionsValidades;
                    const notificacionsCancelades = params.row.notificacionsCancelades;
                    const progress = params.row.progress;
                    switch (params.row.estatProces) {
                        case "PENDENT":
                            return <Chip label={labelText + ` [${notificacionsValidades}]`} variant="outlined" />;
                        case "EN_PROCES":
                            return (<Chip color="info" label={<>{labelText} ({progress}%){' '}[{iconOk} {notificacionsProcessades}]</>}/>);
                        case "EN_PROCES_AMB_ERRORS":
                            return (<Chip color="warning" label={<>{labelText} ({progress}%){' '}[{iconOk} {notificacionsProcessades} / {iconError} {notificacionsProcessadesAmbError}]</>}/>);
                        case "FINALITZAT":
                            return (<Chip color="success" label={<>{labelText} [{iconOk} {notificacionsProcessades}]</>}/>);
                        case "FINALITZAT_AMB_ERRORS":
                            return (<Chip color="warning" label={<>{labelText} [{iconOk} {notificacionsProcessades} / {iconError} {notificacionsProcessadesAmbError}]</>}/>);
                        case "ERRONIA":
                            return (<Chip color="error" label={<>{labelText} [{iconError} {notificacionsValidades}]</>}/>);
                        case "CANCELADA":
                            return (<Chip color="warning" label={<>{labelText} [{iconOk} {notificacionsProcessades}] [{iconCancelada} {notificacionsCancelades}]</>}/>);
                        case "FINALITZAT_PARCIAL":
                            return (<Chip color="info" label={<>{labelText} [{iconOk} {notificacionsProcessades} / {notificacionsProcessadesAmbError}] [{iconCancelada} {notificacionsCancelades}]</>}/>);
                        default:
                            return <Chip label={labelText} />;
                    }
                }
            },
            {
                field: 'createdBy',
                headerName: t('page.notificacioMassiva.grid.createdBy'),
            },

        ],
        []
    );
return columns;
};


const useSpringFilterBuilder = () => {
    return (data: any) => {
        return filterBuilder.and(
            filterBuilder.eq('estatProces', data?.estatProces),
            filterBuilder.like('createdBy', data?.createdBy),
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
        <Box>
            <Grid container spacing={1}>
                <GridFormField size={1.75} name="dataIniciInici" />
                <GridFormField size={1.75} name="dataIniciFi" />
                <GridFormField size={2.5} name="estatProces" label={t('page.notificacioMassiva.grid.estatProces')} />
                <GridFormField size={1.75} name="createdBy" />

                <Grid size={0.5} sx={{ textAlign: 'center' }}>
                    <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                        <Icon>filter_alt_off</Icon>
                    </IconButton>
                </Grid>
            </Grid>
            <Box paddingTop="15px" display="flex" justifyContent="end" fontSize="10px">
                [<Box>{iconOk} {t('page.notificacioMassiva.grid.llegenda.numProcessats')}</Box>]
                [<Box>{iconError} {t('page.notificacioMassiva.grid.llegenda.numErronis')}</Box>]
                [<Box>{iconCancelada} {t('page.notificacioMassiva.grid.llegenda.numCancelats')}</Box>]
            </Box>
        </Box>
    );
};

export const NotifiacioMassivaGrid = () => {

    const { t } = useTranslation();
    const springFilterBuilder = useSpringFilterBuilder();
    const { dialogComponent, onDetailClick } = useNotificacioMassivaResumDialog();
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

    const rowAdditionalActions = (row) => {

        const { artifactAction: apiAction, isReady } = useResourceApiService('notificacioMassivaResource');
        const navigate = useNavigate();
        const listActions: DataCommonAdditionalAction[] = [
            {
                label: t('page.notificacioMassiva.grid.accions.resum'),
                title: t('page.notificacioMassiva.grid.accions.resum'),
                icon: 'info',
                showInMenu: true,
                onClick: (id)=> onDetailClick(id),
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
                onClick: (id) => isReady && apiAction(id, { code: 'POSPOSAR_NOTIFICACIO_MASSIVA'}),// posposarAccioMassiva(id),
            },
            {
                label: t('page.notificacioMassiva.grid.accions.reactivar'),
                title: t('page.notificacioMassiva.grid.accions.reactivar   '),
                icon: 'bolt',
                showInMenu: true,
                onClick: (id) => reactivarAccioMassiva(id),
            },
            {
                label: t('page.notificacioMassiva.grid.accions.mostrarRemeses.label'),
                title: t('page.notificacioMassiva.grid.accions.mostrarRemeses.label'),
                icon: 'list',
                showInMenu: true,
                onClick: (id, row) => {
                    const msg1 = t('page.notificacioMassiva.grid.accions.mostrarRemeses.msg1');
                    const msg2 = t('page.notificacioMassiva.grid.accions.mostrarRemeses.msg2');
                    const dt = new Date(row.createdDate);
                    const formatted = new Intl.DateTimeFormat('ca-ES', {
                        year: 'numeric', month: '2-digit', day: '2-digit',
                        hour: '2-digit', minute: '2-digit'
                    }).format(dt);
                    const titolMassiva = ` ${msg1} ${formatted} - ${row.csvFilename} (${msg2}: ${row.createdBy})`;
                    navigate(`/notificacions?notificacioMassiva=${id}`, { replace: true, state: { titolMassiva: titolMassiva }  });
                },
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
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarCreateLink="form"
                rowAdditionalActions={rowAdditionalActions()}
                rowUpdateLink="form/{{id}}"
            />
            {dialogComponent}
        </GridPage>

    );
};

export default NotifiacioMassivaGrid;
