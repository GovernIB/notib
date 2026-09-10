import React from 'react';
import {Link, useLocation, useNavigate, useSearchParams} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import {GRID_DETAIL_PANEL_TOGGLE_COL_DEF, GridApiPro, useGridApiRef,} from '@mui/x-data-grid-pro';
import {GridPage, MuiDataGrid, MuiDataGridColDef, useMuiDataGridApiRef, useMuiDataGridContext, useResourceApiService,} from 'reactlib';
import {ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_USER, useNotibContext} from '../../components/NotibContext';
import {useDatagridFilterProps, useDatagridPageSizeOptionsProps} from '../../hooks/useDataGrid';
import NotificacioGridEnviaments from './NotificacioGridEnviaments';
import {useNotificacioDetailDialog, useRemesesErrorCallbackDetailDialog, useRemesesErrorRegistreDetailDialog} from './NotificacioDetailDialog';
import {Button, Chip, Icon, IconButton, Menu, MenuItem} from '@mui/material';
import AccionsMassives, {MenuOption, MenuOptionDivider, useAccionsMassives} from '../../components/AccionsMassives';
import ButtonDetailExpandColapse from '../../components/ButtonDetailExpandColapse';
import {DataCommonAdditionalAction} from '../../../lib/components/mui/datacommon/MuiDataCommon';
import {NotificacioEstatGrid} from './NotificacioEstatRender';
import {useAccionsNotificacio} from '../accions/AccionsNotificacio';
import {generateGridRowStylesFromMap, getGridRowColorClass, NOTIFICACIO_ESTAT_ENUM_MAP,} from '../../utils/estatConfig';
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import CustomDetailPanelToggle from "../../utils/CustomDetailPanelToggle.tsx";
import ContentFilter, {useSpringFilterBuilder} from "./NotificacioFiltre.tsx";
import useSseRowRefresh from "../../hooks/useSseRowRefresh";

const useDataGridColumns = (datagridApiRef: any,
                                                            notificacionsEsborrades: boolean,
                                                            notificacioErrorRegistre: boolean,
                                                            notificacioCallbackError: boolean,
                                                            refreshGrid: unknown) => {

    const noEsTaulaRemeses = notificacionsEsborrades || notificacioErrorRegistre || notificacioCallbackError;
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            ...(notificacioCallbackError ? [] : [{
                field: 'enviamentTipus',
                width: 40,
                renderHeader: () => null,
                renderCell: (params: any) => {
                    const letter = params.value?.substring(0, 1);
                    return <Chip label={letter} size="small" title={params.formattedValue}/>;
                }
            },
            {
                field: 'createdDate',
                width: 155,
            }]),
            {
                field: 'enviadaDate',
                width: 155,
            },
            ...(noEsTaulaRemeses ? [] : [{
                field: 'registreNums',
                width: 130,
            }
            ]),
            ...(notificacioErrorRegistre || notificacioCallbackError ? [] : [{
                field: 'organGestor',
                width: 180,
            }]),
            {
                field: 'procediment',
                flex:1,
                width: 180,
                renderCell: (params: any) => {
                    const letter = params.row.procediment != null ? 'P' : 'S';
                    const title = letter === 'P' ? t('page.notificacio.grid.procediment') : t('page.notificacio.grid.servei');
                    return (<><Chip label={letter} size="small" title={title} sx={{ mr: 1 }} />{params.formattedValue}</>);
                },
            },
            ...(notificacioErrorRegistre || notificacioCallbackError ? [] : [{
                field: 'numExpedient',
                width: 130,
            }]),
            {
                field: 'concepte',
                flex:1,
                width: 120,
            },
            ...(notificacioErrorRegistre ? [] : [{
                flex: 1,
                field: 'createdBy',
            },
                ...(notificacioCallbackError ? [] : [{
                    field: 'titular',
                }]),
            {
                field: 'estatString',
                width: 225,
                renderCell: (params: any) => {
                    const estatJson = params?.formattedValue;
                    return (<NotificacioEstatGrid estatJson={estatJson} estatEnum={params?.row?.estat} notificacioId={params?.row?.id} refreshGrid={refreshGrid}/>);
                },
            }]),
            ...(noEsTaulaRemeses ? [] : [{
                ...GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
                hideable: noEsTaulaRemeses,
                sortable: false,
                resizable: false,
                width: 90,
                align: 'center',
                renderHeader: () => <ButtonDetailExpandColapse datagridApiRef={datagridApiRef} />,
                renderCell: (params: any) => (
                    <CustomDetailPanelToggle id={params.id}
                                             value={params.value}
                                             msgMostrar={t('page.notificacio.grid.column.mostrar')}
                                             msgOcultar={t('page.notificacio.grid.column.ocultar')} />
                ),
            }]),
        ] as MuiDataGridColDef[],
        [datagridApiRef, notificacionsEsborrades, notificacioErrorRegistre, notificacioCallbackError, t]
    );
    return columns;
};

const NotificacioAddButton: React.FC = () => {

    const { t } = useTranslation();
    const { currentEntitatLoading, currentEntitat } = useNotibContext();
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => setAnchorEl(event.currentTarget);;
    const handleClose = () => setAnchorEl(null);
    const crearAny = currentEntitat?.crearNotificacions || currentEntitat?.crearComunicacions || currentEntitat?.crearComunicacions;
    return (
        <>
            <Button
                variant="contained"
                loading={currentEntitatLoading}
                disabled={!crearAny}
                startIcon={<Icon>add</Icon>}
                onClick={handleClick}
                sx={{ mr: 1 }}
            >
                {t('page.notificacio.grid.new.title')}
            </Button>
            <Menu anchorEl={anchorEl} open={open} onClose={handleClose}>
                {currentEntitat?.crearNotificacions && (
                    <MenuItem component={Link} to="form?type=NOTIFICACIO" onClick={handleClose}>
                        {t('page.notificacio.grid.new.NOTIFICACIO')}
                    </MenuItem>
                )}
                {currentEntitat?.crearComunicacions && (
                    <MenuItem component={Link} to="form?type=COMUNICACIO" onClick={handleClose}>
                        {t('page.notificacio.grid.new.COMUNICACIO')}
                    </MenuItem>
                )}
                {currentEntitat?.crearSir && (
                    <MenuItem component={Link} to="form?type=SIR" onClick={handleClose}>
                        {t('page.notificacio.grid.new.SIR')}
                    </MenuItem>
                )}
            </Menu>
        </>
    );
};

const MassiveActionsButton: React.FC<{ apiRef: React.RefObject<GridApiPro | null>,
                                       notificacionsErrorRegistre: boolean | null,
                                       notificacionsCallbackError: boolean | null,
                                       refresh: () => void }>
                                       = ({apiRef, notificacionsErrorRegistre, notificacionsCallbackError, refresh}) => {

    const { selection } = useMuiDataGridContext();
    const { currentRole} = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const amagarEntrada = currentRole === ROLE_USER;
    const { t } = useTranslation();

    const { descarregarExcel,
        descarregarJustificants,
        descarregarCertificacions,
        actualitzarEstat,
        reenviarAmbError,
        esborrarMassiu,
        reactivarConsulesCanviEstatMassiu,
        reactivarCallbacksMassiu,
        enviarNotificacionsMovilMassiu,
        marcarProcessatMassiu, marcarProcessatMassiuDialog,
        anularRemesaMassiu, anularRemesaMassiuDialog,
        ampliarTerminiMassiu, ampliarTerminiMassiuDialog,
        reintentarRegistre,
        reenviarCallbacksMassiu
    } = useAccionsMassives("notificacioResource", refresh);

    const ids = selection?.ids ?? [];
    const opcionsMenu: (MenuOption | MenuOptionDivider)[] = isRoleAdminLectura && !notificacionsErrorRegistre ? [
        {
            label: t('page.accioMassiva.accions.exportarFullCalcul.label'),
            tooltip: t('page.accioMassiva.accions.exportarFullCalcul.tooltip'),
            onClick: () => descarregarExcel(selection?.ids, "NOTIFICACIO"),
        },
        {
            label: t('page.accioMassiva.accions.justificantEnviament.label'),
            tooltip: t('page.accioMassiva.accions.justificantEnviament.tooltip'),
            onClick: () => descarregarJustificants(selection?.ids, "NOTIFICACIO"),
        },
        {
            label: t('page.accioMassiva.accions.certificacioRecepcio.label'),
            tooltip: t('page.accioMassiva.accions.certificacioRecepcio.tooltip'),
            onClick: () => descarregarCertificacions(selection?.ids, "NOTIFICACIO"),

        },
    ] :  [
        ...(notificacionsErrorRegistre ? [
            {
                label: t('page.accioMassiva.accions.reintentarRegistre.label'),
                tooltip: t('page.accioMassiva.accions.reintentarRegistre.tooltip'),
                onClick: () => reintentarRegistre(selection?.ids, "NOTIFICACIO")
            }]
            : notificacionsCallbackError ? [
                {
                    label: t('page.callbacks.error.accionsMassives.reenviar.label'),
                    tooltip: t('page.callbacks.error.accionsMassives.reenviar.label'),
                    onClick: () => reenviarCallbacksMassiu(selection?.ids, "NOTIFICACIO")
                }]
            :
            [{
                label: t('page.accioMassiva.accions.marcarProcessades.label'),
                tooltip: t('page.accioMassiva.accions.marcarProcessades.tooltip'),
                disabled: true,
                onClick: () => marcarProcessatMassiu(null, t('page.accioMassiva.accions.marcarProcessades.label'), {ids: ids, seleccioTipus: "NOTIFICACIO"})
            },
            {
                label: t('page.accioMassiva.accions.actualitzarEstat.label'),
                tooltip: t('page.accioMassiva.accions.actualitzarEstat.tooltip'),
                onClick: () => actualitzarEstat(selection?.ids, "NOTIFICACIO"),
            },
            {
                label: t('page.accioMassiva.accions.reenviarAmbError.label'),
                tooltip: t('page.accioMassiva.accions.reenviarAmbError.tooltip'),
                onClick: () => reenviarAmbError(selection?.ids, "NOTIFICACIO"),
            },
            {
                label: t('page.accioMassiva.accions.esborrar.label'),
                tooltip: t('page.accioMassiva.accions.esborrar.tooltip'),
                onClick: () => esborrarMassiu(selection?.ids, "NOTIFICACIO"),
            },
            {
                label: t('page.accioMassiva.accions.exportarFullCalcul.label'),
                tooltip: t('page.accioMassiva.accions.exportarFullCalcul.tooltip'),
                onClick: () => descarregarExcel(selection?.ids, "NOTIFICACIO"),
            },
            {
                label: t('page.accioMassiva.accions.justificantEnviament.label'),
                tooltip: t('page.accioMassiva.accions.justificantEnviament.tooltip'),
                onClick: () => descarregarJustificants(selection?.ids, "NOTIFICACIO"),
            },
            {
                label: t('page.accioMassiva.accions.certificacioRecepcio.label'),
                tooltip: t('page.accioMassiva.accions.certificacioRecepcio.tooltip'),
                onClick: () => descarregarCertificacions(selection?.ids, "NOTIFICACIO"),

            },
            {
                label: t('page.accioMassiva.accions.anular.label'),
                tooltip: t('page.accioMassiva.accions.anular.label.tooltip'),
                onClick: () => anularRemesaMassiu(null, t('page.accioMassiva.accions.anular.label'), {ids, seleccioTipus: "NOTIFICACIO"})
            },
            {
                label: t('page.accioMassiva.accions.ampliarTermini.label'),
                tooltip: t('page.accioMassiva.accions.ampliarTermini.tooltip'),
                onClick: () => ampliarTerminiMassiu(null, t('page.accioMassiva.accions.ampliarTermini.label'), {ids, seleccioTipus: "NOTIFICACIO"})
            },
            ...(amagarEntrada ? [] : [
                { type: 'divider' },
                {
                    label: t('page.accioMassiva.accions.reactivarCanviEstat.label'),
                    tooltip: t('page.accioMassiva.accions.reactivarCanviEstat.tooltip'),
                    onClick: () => reactivarConsulesCanviEstatMassiu(selection?.ids, "NOTIFICACIO"),
                },
                {
                    label: t('page.accioMassiva.accions.reactivarCallbacks.label'),
                    tooltip: t('page.accioMassiva.accions.reactivarCallbacks.tooltip'),
                    onClick: () => reactivarCallbacksMassiu(selection?.ids, "NOTIFICACIO"),
                },
                {
                    label: t('page.accioMassiva.accions.notificacionsMovil.label'),
                    tooltip: t('page.accioMassiva.accions.notificacionsMovil.tooltip'),
                    onClick: () => enviarNotificacionsMovilMassiu(selection?.ids, "NOTIFICACIO"),
                }]
            )
        ])
    ] as (MenuOption | MenuOptionDivider)[];

    return (<>
            <AccionsMassives options={opcionsMenu} apiRef= {apiRef} resource={"notificacioResource"} sizeSelection={selection?.ids?.size}/>
            {marcarProcessatMassiuDialog}
            {anularRemesaMassiuDialog}
            {ampliarTerminiMassiuDialog}
        </>
    )
};

type NotificacioGridParams = { notificacionsEsborrades?: boolean; notificacionsErrorRegistre?: boolean; notificacionsCallbackError?: boolean };
const NotificacioGrid = ({notificacionsEsborrades = false, notificacionsErrorRegistre = false, notificacionsCallbackError= false}: NotificacioGridParams) => {

    const { t } = useTranslation();
    const [ params ] = useSearchParams();
    const { currentRole} = useNotibContext();
    const isRoleAdminLectura = currentRole === ROLE_ADMIN_LECTURA;
    const notificacioMassiva = params?.get('notificacioMassiva');
    const { state } = useLocation();
    let titolSecundari = state?.titolMassiva;
    titolSecundari = notificacionsEsborrades ? t('page.notificacio.grid.notificacionsEsborrades.title') : titolSecundari;
    titolSecundari = notificacionsErrorRegistre ? t('page.notificacio.grid.notificacionsErrorRegistre.title') : titolSecundari;
    titolSecundari = notificacionsCallbackError ? t('page.notificacio.grid.notificacionsCallbackError.title') : titolSecundari;
    const { dialogComponent, onDetailClick } = useNotificacioDetailDialog(notificacionsEsborrades);
    const { dialogComponentErrorRegistre, onDetailClickErrorRegistre } = useRemesesErrorRegistreDetailDialog();
    const { dialogComponentErrorCallback, onDetailClickErrorCallback } = useRemesesErrorCallbackDetailDialog();
    const { currentActions: apiCurrentActions } = useResourceApiService('notificacioResource');
    const isCreateLinkPresent = !isRoleAdminLectura && apiCurrentActions?.['create'] != null;
    const datagridApiRef = useGridApiRef();
    const apiRef = useMuiDataGridApiRef();
    const [reloadKey, setReloadKey] = React.useState(0);
    const refreshGrid = React.useCallback(() => setReloadKey(k => k + 1), []);
    const columns = useDataGridColumns(datagridApiRef, notificacionsEsborrades, notificacionsErrorRegistre, notificacionsCallbackError, refreshGrid);
    // Actualitza automàticament, via SSE, les files de remeses visibles quan el seu estat canvia
    // al servidor (p.ex. per una resposta de Notifica, un event de registre, un callback...), sense
    // necessitat que l'usuari refresqui el llistat manualment.
    useSseRowRefresh('notificacioResource', datagridApiRef, 'REMESA_ENVIAMENT_ESTAT', 'NOTIFICACIO_ESTAT_CANVIAT');
    const springFilterBuilder = useSpringFilterBuilder();
    const [searchParams] = useSearchParams();
    const referencia = searchParams.get('referencia');
    const filterDataGridProps = useDatagridFilterProps(
        'notificacioResource',
        'FILTER_NOTIFICACIO',
        springFilterBuilder,
        <ContentFilter openByDefault={!!referencia}
                       notificacionsEsborrades={notificacionsEsborrades}
                       notificacionsErrorRegistre={notificacionsErrorRegistre}
                       notificacionsCallbackError={notificacionsCallbackError}
        />,
        undefined,
        {referencia: referencia}
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();

    const { descarregarJustificantEnviament,
            descarregarDocumentEnviat,
            descarregarCertificacio,
            anularRemesa,  anularRemesaDialog,
            ampliarTermini, ampliarTerminiDialog,
            marcarProcessat, marcarProcessatDialog,
            esborrarRemesa,
            recuperarRemesa} = useAccionsNotificacio();

    const mostrarEditarBorrar = (estat : string) => {
        return isRoleAdminLectura || (estat !== 'PENDENT' && estat !== 'REGISTRADA');
    }

    const noEsTaulaRemeses = (notificacionsEsborrades || notificacionsErrorRegistre || notificacionsCallbackError);
    const rowAdditionalActions: DataCommonAdditionalAction[] = [
            {
                label: t('page.notificacio.grid.column.detalls'),
                title: t('page.notificacio.grid.column.detalls'),
                icon: 'info',
                showInMenu: !notificacionsCallbackError,
                onClick: id => notificacionsErrorRegistre ? onDetailClickErrorRegistre(id)
                                    : notificacionsCallbackError ? onDetailClickErrorCallback(id)
                                    : onDetailClick(id),
            },
            {
                label: t('page.notificacio.grid.accions.documentEnviat'),
                title: t('page.notificacio.grid.accions.documentEnviat'),
                icon: 'download',
                showInMenu: true,
                onClick: (id) => {
                    console.log("click");
                descarregarDocumentEnviat(id)} ,
                hidden: noEsTaulaRemeses
            },
            {
                label: t('page.notificacio.grid.accions.anular.botoTitle'),
                title: t('page.notificacio.grid.accions.anular.botoTitle'),
                icon: 'block',
                showInMenu: true,
                action: 'ANULAR_REMESA',
                onClick: id => anularRemesa(id, t('page.notificacio.grid.accions.anular.modalTitle')),
                hidden: row => !row.anulable || noEsTaulaRemeses,
            },
            {
                label: t('page.notificacio.grid.accions.certificacio'),
                title: t('page.notificacio.grid.accions.certificacio'),
                icon: 'download',
                showInMenu: true,
                onClick: id => descarregarCertificacio(id),
                hidden: row => !row.envCerData || noEsTaulaRemeses,
            },
            {
                label: t('page.notificacio.grid.accions.processat'),
                title: t('page.notificacio.grid.accions.processat'),
                icon: 'check_circle',
                showInMenu: true,
                action: 'MARCAR_PROCESSAT',
                onClick: id => marcarProcessat(id, t('page.notificacio.grid.accions.processatTitle')),
                hidden: row => !(!isRoleAdminLectura && ((currentRole === ROLE_ADMIN && row.estat === 'FINALITZADA')
                                    || row.permisProcessar)) || noEsTaulaRemeses
            },
            {
                label: t('page.notificacio.grid.accions.justificantEnviament'),
                title: t('page.notificacio.grid.accions.justificantEnviament'),
                icon: 'download',
                showInMenu: true,
                onClick: (id) => descarregarJustificantEnviament(id),
                hidden: (row) => !row?.justificantCreat || noEsTaulaRemeses,
            },
            {
                label: t('page.notificacio.grid.accions.ampliarTermini.botoTitle'),
                title: t('page.notificacio.grid.accions.ampliarTermini.botoTitle'),
                icon: 'calendar_month',
                showInMenu: true,
                action: 'AMPLIAR_TERMINI',
                onClick: (id, row) => ampliarTermini(id, t('page.notificacio.grid.accions.ampliarTermini.modalTitle'), {caducitat: row.caducitat}),
                hidden: row => isRoleAdminLectura || row?.entregaPostal || row?.estat !== 'ENVIADA' || noEsTaulaRemeses
            },
            // {
            //     label: t('page.notificacio.grid.accions.editar'),
            //     title: t('page.notificacio.grid.accions.editar'),
            //     icon: 'edit_icon',
            //     showInMenu: true,
            //     // onClick: (id) => console.log("editar " + id),
            //     clickShowUpdateDialog: true
            //     // hidden: (row) => mostrarEditarBorrar(row?.estat, currentRole),
            // },
            {
                label: t('page.notificacio.grid.accions.esborrar.title'),
                title: t('page.notificacio.grid.accions.esborrar.title'),
                icon: 'delete_icon',
                showInMenu: true,
                onClick: (id) => esborrarRemesa(id),
                hidden: (row) => mostrarEditarBorrar(row?.estat) || noEsTaulaRemeses
            },
            ...(isRoleAdminLectura ? [] : [{
                label: t('page.notificacio.grid.notificacionsEsborrades.recuperar'),
                title: t('page.notificacio.grid.notificacionsEsborrades.recuperar'),
                icon: 'replay',
                showInMenu: true,
                onClick: (id: any) => recuperarRemesa(id),
                hidden: (!notificacionsEsborrades || notificacionsErrorRegistre)
            }])
        ];
    const navigate = useNavigate();
    const filtreMassiva = notificacioMassiva ? `notificacioMassiva.id :${notificacioMassiva}` : undefined
    const filtreEsborrades = notificacionsEsborrades ? `deleted:${true}` : `deleted:${false}`;
    const filtreErrorRegistre = notificacionsErrorRegistre ? "estat : 'PENDENT' and registreEnviamentIntent >: 3" : "";
    const filtreCallbackError = notificacionsCallbackError ? "errorLastCallback:true" : "";
    const fixedFilter = filtreEsborrades + (filtreMassiva ? " and " + filtreMassiva : "")  + (filtreErrorRegistre ? " and " + filtreErrorRegistre : "")
                                + (filtreCallbackError ? " and " + filtreCallbackError : "");


    const detailPanelProps = !noEsTaulaRemeses ? {
                getDetailPanelContent: ({ row }: any) => (<NotificacioGridEnviaments id={row.id} />),
                getDetailPanelHeight: () => 'auto' as const,
            } : {};
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                key={`${currentRole}-${notificacionsEsborrades}-${notificacionsErrorRegistre}-${notificacionsCallbackError}-${reloadKey}`}
                datagridApiRef={datagridApiRef}
                apiRef={apiRef}
                title={t('page.notificacio.grid.title') + (titolSecundari || "")}
                resourceName="notificacioResource"
                columns={columns}
                defaultSortModel={[{ field: 'createdDate', sort: 'desc' }]}
                paginationActive
                popupEditUpdateActive={true}
                // popupEditFormContent={<><NotificacioFormContent /><NotificacioFormEnviaments /><NotificacioFormDocuments /></>}
                selectionActive={!notificacionsEsborrades ? true : undefined}
                rowUpdateLink="form/{{id}}"
                rowUpdateShowInMenu
                // persistentStateClearPageSortPropsOnTopLevelRouteChange
                // persistentStateActive
                rowHideDeleteButton
                rowHideUpdateButton={params => (mostrarEditarBorrar(params.estat) || noEsTaulaRemeses)}
                {...filterDataGridProps}
                fixedFilter={fixedFilter}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarHideCreate
                toolbarCreateLink="form"
                toolbarElementsWithPositions={[
                    ...(isCreateLinkPresent ? [{ position: 2, element: <NotificacioAddButton /> }] : []),
                    ...(notificacionsEsborrades || (isRoleAdminLectura && notificacionsErrorRegistre) ? []
                        : [{
                            position: 2,
                            element: <MassiveActionsButton apiRef={datagridApiRef} notificacionsErrorRegistre={notificacionsErrorRegistre} notificacionsCallbackError={notificacionsCallbackError} refresh={refreshGrid}/>,
                        }]),
                    ...(!notificacioMassiva ? []
                        : [{
                            position: 0,
                            element: <IconButton onClick={()=> navigate('/notificacio/massiva')} sx={{mr:1}}><ArrowBackIosIcon /></IconButton>,
                        }]),
                ]}
                onRowClick={(params) => notificacionsErrorRegistre ? onDetailClickErrorRegistre(params.id) : onDetailClick(params.id)}
                rowActionsColumnIndex={11}
                rowActionsColumnProps={{ width: 90 }}
                rowAdditionalActions={rowAdditionalActions}
                {...detailPanelProps}
                getRowHeight={() => 'auto'}
                getRowClassName={(params) => getGridRowColorClass(params.row.estat, NOTIFICACIO_ESTAT_ENUM_MAP)}
                sx={generateGridRowStylesFromMap(NOTIFICACIO_ESTAT_ENUM_MAP)}
            />
            {dialogComponent}
            {dialogComponentErrorRegistre}
            {dialogComponentErrorCallback}
            {anularRemesaDialog}
            {ampliarTerminiDialog}
            {marcarProcessatDialog}
        </GridPage>
    );
};

export default NotificacioGrid;
