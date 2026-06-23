import React from 'react';
import {Link, useLocation, useNavigate, useSearchParams} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import {GRID_DETAIL_PANEL_TOGGLE_COL_DEF, GridApiPro, useGridApiRef,} from '@mui/x-data-grid-pro';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    springFilterBuilder,
    springFilterBuilder as filterBuilder,
    useFilterApiContext,
    useMuiDataGridApiRef,
    useMuiDataGridContext,
    useResourceApiService,
} from 'reactlib';
import {useNotibContext} from '../../components/NotibContext';
import {useDatagridFilterProps, useDatagridPageSizeOptionsProps} from '../../hooks/useDataGrid';
import NotificacioGridEnviaments from './NotificacioGridEnviaments';
import {useNotificacioDetailDialog, useRemesesErrorRegistreDetailDialog} from './NotificacioDetailDialog';
import {Button, Chip, Grid, Icon, IconButton, Menu, MenuItem} from '@mui/material';
import GridFormField, {GridButtonField} from '../../components/GridFormField';
import {formatEndOfDay, formatStartOfDay} from '../../utils/dateUtils';
import AccionsMassives, {MenuOption, useAccionsMassives} from '../../components/AccionsMassives';
import ButtonDetailExpandColapse from '../../components/ButtonDetailExpandColapse';
import {DataCommonAdditionalAction} from '../../../lib/components/mui/datacommon/MuiDataCommon';
import {NotificacioEstatGrid} from './NotificacioEstatRender';
import {useAccionsNotificacio} from '../accions/AccionsNotificacio';
import {generateGridRowStylesFromMap, getGridRowColorClass, NOTIFICACIO_ESTAT_ENUM_MAP,} from '../../utils/estatConfig';
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import CustomDetailPanelToggle from "../../utils/CustomDetailPanelToggle.tsx";


const useDataGridColumns = (datagridApiRef: any) => {
    const { t } = useTranslation();

    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'enviamentTipus',
                width: 40,
                renderHeader: () => null,
                renderCell: (params: any) => {
                    const letter = params.value?.substring(0, 1);
                    return <Chip label={letter} size="small" title={params.formattedValue} />;
                },
            },
            {
                field: 'createdDate',
                width: 110,

            },
            {
                field: 'enviadaDate',
                width: 100,
            },
            {
                field: 'registreNums',
                width: 130,
            },
            {
                field: 'organGestor',
                width: 180,
            },
            {
                field: 'procediment',
                width: 180,
                renderCell: (params: any) => {
                    const letter = params.row.procediment != null ? 'P' : 'S';
                    const title =
                        letter === 'P'
                            ? t('page.notificacio.grid.procediment')
                            : t('page.notificacio.grid.servei');
                    return (
                        <>
                            <Chip label={letter} size="small" title={title} sx={{ mr: 1 }} />
                            {params.formattedValue}
                        </>
                    );
                },
            },
            {
                field: 'numExpedient',
                width: 130,
            },
            {
                field: 'concepte',
                width: 120,
            },
            {
                field: 'createdBy',
            },
            {
                field: 'titular',
            },
            {
                field: 'estatString',
                width: 200,
                renderCell: (params: any) => {
                    const estatJson = params?.formattedValue;
                    return (<NotificacioEstatGrid estatJson={estatJson} estatEnum={params?.row?.estat}/>);
                },
            },
            {
                ...GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
                hideable: false,
                sortable: false,
                resizable: false,
                width: 90,
                align: 'center',
                renderHeader: () => <ButtonDetailExpandColapse datagridApiRef={datagridApiRef} />,
                renderCell: (params: any) => (
                    <CustomDetailPanelToggle id={params.id} value={params.value} />
                ),
            },
        ],
        []
    );
    return columns;
};

const useSpringFilterBuilder = () => {

    const { currentUser } = useNotibContext();
    return (data: any) => {
        filterBuilder.and(
            filterBuilder.eq('enviamentTipus', `'${data?.enviamentTipus}'`),
            filterBuilder.like('concepte', data.concepte),
            filterBuilder.eq('estat', `'${data?.estat}'`),
            data?.dataIniciInici && filterBuilder.gte('createdDate', `'${formatStartOfDay(data?.dataIniciInici)}'`),
            data?.dataIniciFi && filterBuilder.lte('createdDate', `'${formatEndOfDay(data?.dataIniciFi)}'`),
            filterBuilder.like('titular', data?.interessat),
            filterBuilder.like('numExpedient', data.numExpedient),
            filterBuilder.like('notificaIds', data.identificadorNotifica),
            filterBuilder.eq('organGestor.id', data?.organGestor?.id),
            filterBuilder.eq('procediment.id', data?.procediment?.id),
            filterBuilder.eq('procediment.id', data?.servei?.id),
            filterBuilder.eq('tipusUsuari', `'${data?.tipusUsuari}'`),
            filterBuilder.eq('createdBy', `'${data?.createdBy}'`),
            filterBuilder.like('referencia', data?.referencia),
            filterBuilder.like('registreNums', data?.registreNumeroSortida),
            data?.dataCaducitatInici && filterBuilder.gte('caducitat', `'${formatStartOfDay(data?.dataCaducitatInici)}'`),
            data?.dataCaducitatFi && filterBuilder.lte('caducitat', `'${formatEndOfDay(data?.dataCaducitatFi)}'`),
            data?.nomesLesMeves && filterBuilder.eq('createdBy', `'${currentUser.codi}'`),
            data?.entregaPostal && filterBuilder.eq('entregaPostal', `'${data.entregaPostal}'`),
            data?.errorLastCallback && filterBuilder.eq('errorLastCallback', `'${data.errorLastCallback}'`)
        );
    };
};

const NotificacioAddButton: React.FC = () => {

    const { t } = useTranslation();
    const { currentEntitatLoading, currentEntitat } = useNotibContext();
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => setAnchorEl(event.currentTarget);;
    const handleClose = () => setAnchorEl(null);
    const crearAny = currentEntitat?.crearNotificacions || currentEntitat?.crearComunicacions || currentEntitat?.crearSir;
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

const MassiveActionsButton: React.FC<{ apiRef: React.RefObject<GridApiPro | null>, notificacionsErrorRegistre: boolean | null }> = ({apiRef, notificacionsErrorRegistre}) => {

    const { selection } = useMuiDataGridContext();
    const { currentRole} = useNotibContext();
    let amagarEntrada = currentRole === 'tothom' || currentRole === 'NOT_ADMIN_LECTURA';
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
        reintentarRegistre
    } = useAccionsMassives();

    const ids = selection?.ids ?? [];

    const opcionsMenu: MenuOption[] = [
        ...(notificacionsErrorRegistre ? [
            {
                label: t('page.accioMassiva.accions.reintentarRegistre.label'),
                tooltip: t('page.accioMassiva.accions.reintentarRegistre.tooltip'),
                onClick: () => reintentarRegistre(selection?.ids, "NOTIFICACIO")
            },

        ] : [
            {
                label: t('page.accioMassiva.accions.marcarProcessades.label'),
                tooltip: t('page.accioMassiva.accions.marcarProcessades.tooltip'),
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
                onClick: () => esborrarMassiu(selection?.ids, "NOTIFICACIO", apiRef),
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
    ];

    return (<>
            <AccionsMassives options={opcionsMenu} apiRef= {apiRef} resource={"notificacioResource"} sizeSelection={selection?.ids?.size}/>
            {marcarProcessatMassiuDialog}
            {anularRemesaMassiuDialog}
            {ampliarTerminiMassiuDialog}
        </>
    )
};

const ContentFilter: React.FC<{openByDefault?: boolean}> = ({openByDefault}) => {

    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const [advancedFilter, setAdvancedFilter] = React.useState(openByDefault ?? false);
    const handleButtonClick = () => filterApiRef.current?.clear();
    const advancedFilterClick = () => setAdvancedFilter(!advancedFilter);

    return (
        <Grid container spacing={1}>
            <GridFormField size={2} name="enviamentTipus" />
            <GridFormField size={advancedFilter ? 4 : 2.5} name="concepte" />
            <GridFormField size={2.5} name="estat" />
            <GridFormField size={1.75} name="dataIniciInici" />
            <GridFormField size={1.75} name="dataIniciFi" />

            {advancedFilter && (
                <>
                    <GridFormField size={2} name="interessat" />
                    <GridFormField size={2} name="numExpedient" />
                    <GridFormField size={2} name="identificadorNotifica" />
                    <GridFormField size={6} name="organGestor" />
                    <GridFormField
                        size={3.5}
                        name="procediment"
                        filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'PROCEDIMENT'`))}
                    />
                    <GridFormField
                        size={3.5}
                        name="servei"
                        filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'SERVEI'`))}
                    />
                    <GridFormField size={2} name="tipusUsuari" />
                    <GridFormField size={3} name="createdBy" />
                    <GridFormField size={3} name="referencia" />
                    <GridFormField size={2.5} name="registreNumeroSortida" />
                    <GridFormField size={1.75} name="dataCaducitatInici" />
                    <GridFormField size={1.75} name="dataCaducitatFi" />
                    <GridButtonField size={0.5} name="nomesLesMeves" icon={'person'} hiddenLabel />
                    <GridButtonField size={0.5} name="entregaPostal" icon={'email'} hiddenLabel />
                    <GridButtonField size={0.5} name="errorLastCallback" icon={'report_problem'} hiddenLabel/>
                </>
            )}
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={advancedFilterClick} title={t(advancedFilter ? 'comu.tancarFiltreAvançat' : 'comu.obrirFiltreAvançat')}>
                    <Icon sx={{ transform: advancedFilter ? 'rotate(180deg)' : 'none' }}>filter_list</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

const NotificacioGrid = () => {

    const { t } = useTranslation();
    const [ params ] = useSearchParams();
    const { currentRole} = useNotibContext();
    const notificacioMassiva = params?.get('notificacioMassiva');
    const { state } = useLocation();
    let titolSecundari = state?.titolMassiva;
    const notificacionsEsborrades = params?.get('esborrades');
    const notificacionsErrorRegistre : boolean | null = Boolean(params?.get('errorRegistre'));
    titolSecundari = notificacionsEsborrades ? t('page.notificacio.grid.notificacionsEsborrades.title') : titolSecundari;
    titolSecundari = notificacionsErrorRegistre ? t('page.notificacio.grid.notificacionsErrorRegistre.title') : titolSecundari;
    const { dialogComponent, onDetailClick } = useNotificacioDetailDialog();
    const { dialogComponentErrorRegistre, onDetailClickErrorRegistre } = useRemesesErrorRegistreDetailDialog();
    const { currentActions: apiCurrentActions } = useResourceApiService('notificacioResource');
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;
    const datagridApiRef = useGridApiRef();
    const apiRef = useMuiDataGridApiRef();
    const columns = useDataGridColumns(datagridApiRef);
    const springFilterBuilder = useSpringFilterBuilder();
    const [searchParams] = useSearchParams();
    const referencia = searchParams.get('referencia');
    const filterDataGridProps = useDatagridFilterProps(
        'notificacioResource',
        'FILTER_NOTIFICACIO',
        springFilterBuilder,
        <ContentFilter openByDefault={!!referencia} />,
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

    const mostrarEditarBorrar = (estat : string, currentRole : string | undefined ) => {
        return currentRole === 'NOT_ADMIN_LECTURA' || (estat !== 'PENDENT' && estat !== 'REGISTRADA');
    }

    const noEsTaulaRemeses = !!(notificacionsEsborrades || notificacionsErrorRegistre);
    const rowAdditionalActions: DataCommonAdditionalAction[] = [
            {
                label: t('page.notificacio.grid.column.detalls'),
                title: t('page.notificacio.grid.column.detalls'),
                icon: 'info',
                showInMenu: true,
                onClick: id => notificacionsErrorRegistre ? onDetailClickErrorRegistre(id) : onDetailClick(id),
            },
            {
                label: t('page.notificacio.grid.accions.documentEnviat'),
                title: t('page.notificacio.grid.accions.documentEnviat'),
                icon: 'download',
                showInMenu: true,
                onClick: (id) => descarregarDocumentEnviat(id),
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
                hidden: row => !(currentRole !== 'NOT_ADMIN_LECTURA' && ((currentRole === 'NOT_ADMIN' && row.estat === 'FINALITZADA')
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
                hidden: row => currentRole === 'NOT_ADMIN_LECTURA'  || row?.entregaPostal || row?.estat !== 'ENVIADA' || noEsTaulaRemeses
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
                label: t('page.notificacio.grid.accions.esborrar'),
                title: t('page.notificacio.grid.accions.esborrar'),
                icon: 'delete_icon',
                showInMenu: true,
                onClick: (id) => esborrarRemesa(id),
                hidden: (row) => mostrarEditarBorrar(row?.estat, currentRole) || noEsTaulaRemeses
            },
            {
                label: t('page.notificacio.grid.notificacionsEsborrades.recuperar'),
                title: t('page.notificacio.grid.notificacionsEsborrades.recuperar'),
                icon: 'replay',
                showInMenu: true,
                onClick: (id) => recuperarRemesa(id),
                hidden: (!notificacionsEsborrades || notificacionsErrorRegistre)
            },
        ];
    const navigate = useNavigate();
    const filtreMassiva = notificacioMassiva ? `notificacioMassiva.id :${notificacioMassiva}` : undefined
    const filtreEsborrades = notificacionsEsborrades ? `deleted :${true}` : `deleted :${false}`;
    const filtreErrorRegistre = notificacionsErrorRegistre ? "estat : 'PENDENT' and registreEnviamentIntent >: 3" : "";
    const fixedFilter = filtreEsborrades + (filtreMassiva ? " and " + filtreMassiva : "")  + (filtreErrorRegistre ? " and " + filtreErrorRegistre : "");
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                key={`${currentRole}-${notificacionsEsborrades}-${notificacionsErrorRegistre}`}
                datagridApiRef={datagridApiRef}
                apiRef={apiRef}
                title={t('page.notificacio.grid.title') + (titolSecundari || "")}
                resourceName="notificacioResource"
                columns={columns}
                defaultSortModel={[{ field: 'createdDate', sort: 'desc' }]}
                paginationActive
                popupEditUpdateActive={true}
                // popupEditFormContent={<><NotificacioFormContent /><NotificacioFormEnviaments /><NotificacioFormDocuments /></>}
                selectionActive
                rowUpdateLink="form/{{id}}"
                rowUpdateShowInMenu
                persistentStateActive
                rowHideDeleteButton
                rowHideUpdateButton={params => (mostrarEditarBorrar(params.estat, currentRole) || noEsTaulaRemeses)}
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                fixedFilter={fixedFilter}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarHideCreate
                toolbarCreateLink="form"
                toolbarElementsWithPositions={[
                    ...(isCreateLinkPresent
                        ? [
                              {
                                  position: 2,
                                  element: <NotificacioAddButton />,
                              },
                          ]
                        : []),
                    ...(notificacionsEsborrades ? []
                        : [{
                            position: 2,
                            element: <MassiveActionsButton apiRef={datagridApiRef} notificacionsErrorRegistre={notificacionsErrorRegistre}/>,
                        }]),
                    ...(notificacioMassiva
                        ? [
                            {
                                position: 0,
                                element: <IconButton onClick={()=> navigate('/notificacio/massiva')} sx={{mr:1}}><ArrowBackIosIcon /></IconButton>,
                            },
                        ]
                        : []),
                ]}
                onRowClick={(params) => notificacionsErrorRegistre ? onDetailClickErrorRegistre(params.id) : onDetailClick(params.id)}
                rowActionsColumnIndex={11}
                rowActionsColumnProps={{
                    width: 90,
                }}
                rowAdditionalActions={rowAdditionalActions}
                getDetailPanelContent={({ row }) => <NotificacioGridEnviaments id={row.id} />}
                getDetailPanelHeight={() => 'auto'}
                getRowHeight={() => 'auto'}
                getRowClassName={(params) =>
                    getGridRowColorClass(params.row.estat, NOTIFICACIO_ESTAT_ENUM_MAP)
                }
                sx={generateGridRowStylesFromMap(NOTIFICACIO_ESTAT_ENUM_MAP)}
            />
            {dialogComponent}
            {dialogComponentErrorRegistre}
            {anularRemesaDialog}
            {ampliarTerminiDialog}
            {marcarProcessatDialog}
        </GridPage>
    );
};

export default NotificacioGrid;
