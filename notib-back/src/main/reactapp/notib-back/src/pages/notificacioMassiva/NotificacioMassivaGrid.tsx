import {Box, Chip, Icon} from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    GridPage, MuiActionReportButton,
    MuiDataGridColDef,
    springFilterBuilder as filterBuilder, useBaseAppContext,
    useMuiActionReportLogic, useMuiDataGridApiRef, useResourceApiService,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import {DataCommonAdditionalAction} from "../../../lib/components/mui/datacommon/MuiDataCommon.tsx";
import Typography from "@mui/material/Typography";
import { useNotificacioMassivaResumDialog } from './NotificacioMassivaResumDialog';
import {useNavigate} from "react-router-dom";
import {ROLE_ADMIN_LECTURA, ROLE_USER, useNotibContext} from "../../components/NotibContext.ts";
import {TemporalMessageSeverity} from "../../../lib/components/BaseAppContext.tsx";
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../utils/icons';
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";

const iconOk= React.cloneElement(<Icon>check</Icon>, { fontSize: "inherit", sx: { verticalAlign: "center"} });
const iconError= React.cloneElement(<Icon>close</Icon>, { fontSize: "inherit", sx: { verticalAlign: "center" } });
const iconCancelada= React.cloneElement(<Icon>block</Icon>, { fontSize: "inherit", sx: { verticalAlign: "center", ml: 0.1 } });

const useDataGridColumns = () => {

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
            filterBuilder.eq('estatProces', `'${data?.estatProces}'`),
            filterBuilder.like('createdBy', data?.createdBy),
            data?.dataIniciInici &&
            filterBuilder.gte('createdDate', `'${formatStartOfDay(data?.dataIniciInici)}'`),
            data?.dataIniciFi &&
            filterBuilder.lte('createdDate', `'${formatEndOfDay(data?.dataIniciFi)}'`),
        );
    };
};

const ContentFilter: React.FC = () => {

    const { currentRole } = useNotibContext();
    const isNotRoleUser = currentRole !== ROLE_USER

    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataIniciInici" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataIniciFi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="estatProces" />
            {isNotRoleUser && <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="createdBy" />}
        </>
    );
};

/** Llegenda de les icones de la columna de resum, sota la fila del filtre. */
const FilterLegend: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Box paddingTop="15px" display="flex" justifyContent="end" fontSize="10px">
            [<Box>{iconOk} {t('page.notificacioMassiva.grid.llegenda.numProcessats')}</Box>]
            [<Box>{iconError} {t('page.notificacioMassiva.grid.llegenda.numErronis')}</Box>]
            [<Box>{iconCancelada} {t('page.notificacioMassiva.grid.llegenda.numCancelats')}</Box>]
        </Box>
    );
};

export const NotifiacioMassivaGrid = () => {

    const { t } = useTranslation();
    const springFilterBuilder = useSpringFilterBuilder();
    const gridApiRef = useMuiDataGridApiRef();
    const { dialogComponent, onDetailClick } = useNotificacioMassivaResumDialog();
    const filterDataGridProps = useDatagridFilterProps(
        'notificacioMassivaResource',
        'FILTER_NOTIFICACIO_MASSIVA',
        springFilterBuilder,
        <ContentFilter />,
        { footer: <FilterLegend /> }
    );
    const columns = useDataGridColumns();
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

    const rowAdditionalActions = () => {

        const { artifactAction: apiAction, isReady } = useResourceApiService('notificacioMassivaResource');
        const navigate = useNavigate();
        const { currentRole} = useNotibContext();
        const {temporalMessageShow} = useBaseAppContext();
        const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
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
            ...(isRoleAdminLectura ? [] : [{
                label: t('page.notificacioMassiva.grid.accions.posposar.title'),
                title: t('page.notificacioMassiva.grid.accions.posposar.title'),
                icon: 'access_time',
                showInMenu: true,
                onClick: (id: any) => {
                    if (!isReady) {
                        return;
                    }
                    apiAction(id, { code: 'POSPOSAR_NOTIFICACIO_MASSIVA'}).then(resposta => {
                        let tipus : TemporalMessageSeverity = resposta ? "success" : "error";
                        temporalMessageShow(null, t("page.notificacioMassiva.grid.accions.posposar." + tipus), tipus);
                    });
                },
            },
            {
                label: t('page.notificacioMassiva.grid.accions.reactivar.title'),
                title: t('page.notificacioMassiva.grid.accions.reactivar.title'),
                icon: 'bolt',
                showInMenu: true,
                onClick: (id: any) =>  {
                    if (!isReady) {
                        return;
                    }
                    apiAction(id, { code: 'REACTIVAR_NOTIFICACIO_MASSIVA'}).then(resposta => {
                        let tipus : TemporalMessageSeverity = resposta ? "success" : "error";
                        temporalMessageShow(null, t("page.notificacioMassiva.grid.accions.reactivar." + tipus), tipus);
                    });
                },
            }]),
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
        <>
            <PageTitle title={t('page.notificacioMassiva.grid.title')}></PageTitle>
            <GridPage>
                <ScreenFrame icon={icons.enviamentMassiu} title={t('page.notificacioMassiva.grid.title')} >
                    <StyledMuiGrid
                        resourceName="notificacioMassivaResource"
                        columns={columns}
                        apiRef={gridApiRef}
                        defaultSortModel={[{ field: 'createdDate', sort: 'desc' }]}
                        paginationActive
                        persistentStateActive
                        persistentStateClearPageSortPropsOnTopLevelRouteChange
                        {...filterDataGridProps}
                        {...pageSizeOptionsDataGridProps}
                        // toolbarCreateLink="form"
                        toolbarHideCreate
                        // toolbarElementsWithPositions={[
                        //     { position: 2, element: <AddButton gridApiRef={gridApiRef} text={t('page.notificacioMassiva.grid.nou')} tooltip={t('page.notificacioMassiva.grid.nouTooltip')} /> },
                        // ]}
                        rowAdditionalActions={rowAdditionalActions()}
                        rowUpdateLink="form/{{id}}"
                    />
                    {dialogComponent}
                </ScreenFrame>
            </GridPage>
        </>
    );
};

export default NotifiacioMassivaGrid;
