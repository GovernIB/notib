import React from 'react';
import {Link, useLocation, useNavigate, useSearchParams} from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
    GridRenderCellParams,
    useGridApiContext,
    useGridSelector,
    gridDetailPanelExpandedRowsContentCacheSelector,
    gridDetailPanelExpandedRowIdsSelector,
    GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
    useGridApiRef,
    GridApiPro,
} from '@mui/x-data-grid-pro';
import {
    GridPage,
    MuiDataGrid,
    useResourceApiService,
    springFilterBuilder as filterBuilder,
    springFilterBuilder,
    useMuiDataGridContext,
    useMuiActionReportLogic,
    useFilterApiContext,
    MuiDataGridColDef,
} from 'reactlib';
import { useNotibContext } from '../../components/NotibContext';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import NotificacioGridEnviaments from './NotificacioGridEnviaments';
import { useNotificacioDetailDialog } from './NotificacioDetailDialog';
import { Grid, IconButton, Button, Icon, Menu, MenuItem, Chip } from '@mui/material';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import AccionsMassives, { MenuOption } from '../../components/AccionsMassives';
import ButtonDetailExpandColapse from '../../components/ButtonDetailExpandColapse';
import { DataCommonAdditionalAction } from '../../../lib/components/mui/datacommon/MuiDataCommon';
import { NotificacioEstatGrid } from './NotificacioEstatRender';
import { useAccionsNotificacio } from '../accions/AccionsNotificacio';
import {
    generateGridRowStylesFromMap,
    getGridRowColorClass,
    NOTIFICACIO_ESTAT_ENUM_MAP,
} from '../../utils/estatConfig';
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";

const CustomDetailPanelToggle = (props: Pick<GridRenderCellParams, 'id' | 'value'>) => {
    const { id } = props;
    const { t } = useTranslation();
    const apiRef = useGridApiContext();
    const contentCache = useGridSelector(apiRef, gridDetailPanelExpandedRowsContentCacheSelector);
    const hasDetail = React.isValidElement(contentCache[id]);
    const expandedRowIds = useGridSelector(apiRef, gridDetailPanelExpandedRowIdsSelector);
    const isExpanded = expandedRowIds.has(id);

    return (
        <IconButton
            size="small"
            tabIndex={-1}
            disabled={!hasDetail}
            title={
                isExpanded
                    ? t('page.notificacio.grid.column.ocultar')
                    : t('page.notificacio.grid.column.mostrar')
            }
            aria-label={
                isExpanded
                    ? t('page.notificacio.grid.column.ocultar')
                    : t('page.notificacio.grid.column.mostrar')
            }
        >
            <Icon
                sx={(theme) => ({
                    transform: `rotateZ(${isExpanded ? 180 : 0}deg)`,
                    transition: theme.transitions.create('transform', {
                        duration: theme.transitions.duration.shortest,
                    }),
                })}
                fontSize="inherit"
            >
                expand_more
            </Icon>
        </IconButton>
    );
};

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
                    return (
                        <NotificacioEstatGrid
                            estatJson={estatJson}
                            estatEnum={params?.row?.estat}
                        />
                    );
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
        return filterBuilder.and(
            filterBuilder.eq('enviamentTipus', `'${data?.enviamentTipus}'`),
            filterBuilder.like('concepte', data.concepte),
            filterBuilder.eq('estat', `'${data?.estat}'`),
            data?.dataIniciInici &&
                filterBuilder.gte('createdDate', `'${formatStartOfDay(data?.dataIniciInici)}'`),
            data?.dataIniciFi &&
                filterBuilder.lte('createdDate', `'${formatEndOfDay(data?.dataIniciFi)}'`),
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
            data?.dataCaducitatInici &&
                filterBuilder.gte('caducitat', `'${formatStartOfDay(data?.dataCaducitatInici)}'`),
            data?.dataCaducitatFi &&
                filterBuilder.lte('caducitat', `'${formatEndOfDay(data?.dataCaducitatFi)}'`),
            data?.nomesLesMeves && filterBuilder.eq('createdBy', `'${currentUser.codi}'`),
            data?.entregaPostal && filterBuilder.eq('entregaPostal', `'${data.entregaPostal}'`),
            data?.errorLastCallback &&
                filterBuilder.eq('errorLastCallback', `'${data.errorLastCallback}'`)
        );
    };
};

const NotificacioAddButton: React.FC = () => {
    const { t } = useTranslation();
    const { currentEntitatLoading, currentEntitat } = useNotibContext();
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };
    const crearAny =
        currentEntitat?.crearNotificacions ||
        currentEntitat?.crearComunicacions ||
        currentEntitat?.crearSir;
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

const MassiveActionsButton: React.FC<{ datagridApiRef: React.RefObject<GridApiPro | null> }> = ({
    datagridApiRef,
}) => {
    const { selection } = useMuiDataGridContext();

    const { exec: execExemple } = useMuiActionReportLogic(
        'notificacioEnviamentResource',
        undefined,
        'EXPORTAR_EXCEL',
        'CSV'
    );

    const opcionsMenu: MenuOption[] = [
        {
            label: 'Marcar com a processades',
            onClick: () => {
                execExemple(selection?.ids);
            },
        },
        {
            label: "Actualitzar l'estat",
            onClick: () => console.log("Actualitzar l'estat"),
        },
        {
            label: 'Tornar a enviar les que han donat error',
            onClick: () => console.log('Tornar a enviar les que han donat error'),
        },
        {
            label: 'Esborrar',
            onClick: () => console.log('Esborrar'),
        },
        {
            label: 'Exporta a full de càlcul',
            onClick: () => console.log('Exporta a full de càlcul'),
        },
        {
            label: "Descarrega justificants d'enviemanet",
            onClick: () => console.log("Descarrega justificants d'enviemanet"),
        },
        {
            label: 'Descarrega certificats de recepció',
            onClick: () => console.log('Descarrega certificats de recepció'),
        },
        {
            label: 'Anul·lar',
            onClick: () => console.log('Anul·lar'),
        },
        {
            label: 'Ampliar termini',
            onClick: () => console.log('Ampliar termini'),
        },
    ];

    return (
        <AccionsMassives
            options={opcionsMenu}
            sizeSelection={selection?.ids?.size}
            datagridApiRef={datagridApiRef}
        />
    );
};

const ContentFilter: React.FC<{openByDefault?: boolean}> = ({openByDefault}) => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const [advancedFilter, setAdvancedFilter] = React.useState(openByDefault ?? false);

    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };

    const advancedFilterClick = () => {
        setAdvancedFilter(!advancedFilter);
    };

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
                        filter={springFilterBuilder.and(
                            springFilterBuilder.eq('tipus', `'PROCEDIMENT'`)
                        )}
                    />
                    <GridFormField
                        size={3.5}
                        name="servei"
                        filter={springFilterBuilder.and(
                            springFilterBuilder.eq('tipus', `'SERVEI'`)
                        )}
                    />
                    <GridFormField size={2} name="tipusUsuari" />
                    <GridFormField size={3} name="createdBy" />
                    <GridFormField size={3} name="referencia" />
                    <GridFormField size={2.5} name="registreNumeroSortida" />
                    <GridFormField size={1.75} name="dataCaducitatInici" />
                    <GridFormField size={1.75} name="dataCaducitatFi" />
                    <GridButtonField size={0.5} name="nomesLesMeves" icon={'person'} hiddenLabel />
                    <GridButtonField size={0.5} name="entregaPostal" icon={'email'} hiddenLabel />
                    <GridButtonField
                        size={0.5}
                        name="errorLastCallback"
                        icon={'report_problem'}
                        hiddenLabel
                    />
                </>
            )}
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton
                    onClick={advancedFilterClick}
                    title={t(
                        advancedFilter ? 'comu.tancarFiltreAvançat' : 'comu.obrirFiltreAvançat'
                    )}
                >
                    <Icon sx={{ transform: advancedFilter ? 'rotate(180deg)' : 'none' }}>
                        filter_list
                    </Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

const NotificacioGrid = () => {

    const { t } = useTranslation();
    const [ params ] = useSearchParams();
    const notificacioMassiva = params?.get('notificacioMassiva');
    const { state } = useLocation();
    const titolMassiva = state?.titolMassiva;
    const { dialogComponent, onDetailClick } = useNotificacioDetailDialog();
    const { currentActions: apiCurrentActions } = useResourceApiService('notificacioResource');
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;
    const datagridApiRef = useGridApiRef();
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

    const { descarregarJustificantEnviament, descarregarDocumentEnviat } = useAccionsNotificacio();
    const rowAdditionalActions = () => {
        const listActions: DataCommonAdditionalAction[] = [
            {
                label: t('page.notificacio.grid.column.detalls'),
                title: t('page.notificacio.grid.column.detalls'),
                icon: 'info',
                showInMenu: true,
                onClick: (id) => onDetailClick(id),
            },
            {
                label: t('page.notificacio.grid.accions.documentEnviat'),
                title: t('page.notificacio.grid.accions.documentEnviat'),
                icon: 'download',
                showInMenu: true,
                onClick: (id) => descarregarDocumentEnviat(id)
            },
            {
                label: t('page.notificacio.grid.accions.anular'),
                title: t('page.notificacio.grid.accions.anular'),
                icon: 'block',
                showInMenu: true,
                onClick: (id) => console.error(`En construcció: ${id}`),
                // hidden: (row) => !row.anulable,
            },
            {
                label: t('page.notificacio.grid.accions.certificacio'),
                title: t('page.notificacio.grid.accions.certificacio'),
                icon: 'download',
                showInMenu: true,
                onClick: (id) => console.error(`En construcció: ${id}`),
                // hidden: (row) => !row.envCerData,
            },
            {
                label: t('page.notificacio.grid.accions.processat'),
                title: t('page.notificacio.grid.accions.processat'),
                icon: 'check_circle',
                showInMenu: true,
                onClick: (id) => console.error(`En construcció: ${id}`),
                // hidden: (row) => row,
                // if ${!isRolActualAdministradorLectura} && ((~hlpIsAdministradorEntitat() && estat == 'FINALITZADA') || permisProcessar)
            },
            {
                label: t('page.notificacio.grid.accions.justificantEnviament'),
                title: t('page.notificacio.grid.accions.justificantEnviament'),
                icon: 'download',
                showInMenu: true,
                onClick: (id) => descarregarJustificantEnviament(id)
                // hidden: (row) => !row?.justificant,
            },
            {
                label: t('page.notificacio.grid.accions.ampliarTermini'),
                title: t('page.notificacio.grid.accions.ampliarTermini'),
                icon: 'calendar_month',
                showInMenu: true,
                onClick: (id) => console.error(`En construcció: ${id}`),
                // hidden: (row) => isRolActualAdministradorLectura && !row?.plazoAmpliable,
            },
        ];

        return listActions;
    };
    const navigate = useNavigate();
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                datagridApiRef={datagridApiRef}
                title={t('page.notificacio.grid.title') + (titolMassiva ? titolMassiva : "")}
                resourceName="notificacioResource"
                columns={columns}
                defaultSortModel={[{ field: 'createdDate', sort: 'desc' }]}
                paginationActive
                selectionActive
                readOnly
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                fixedFilter={notificacioMassiva ? `notificacioMassiva.id :${notificacioMassiva}` : undefined}
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
                    {
                        position: 2,
                        element: <MassiveActionsButton datagridApiRef={datagridApiRef} />,
                    },
                    ...(notificacioMassiva
                        ? [
                            {
                                position: 0,
                                element: <IconButton onClick={()=> navigate('/notificacio/massiva')} sx={{mr:1}}><ArrowBackIosIcon /></IconButton>,
                            },
                        ]
                        : []),
                ]}
                onRowClick={(params) => onDetailClick(params.id)}
                rowActionsColumnIndex={11}
                rowActionsColumnProps={{
                    width: 90,
                }}
                rowAdditionalActions={rowAdditionalActions()}
                getDetailPanelContent={({ row }) => <NotificacioGridEnviaments id={row.id} />}
                getDetailPanelHeight={() => 'auto'}
                getRowHeight={() => 'auto'}
                getRowClassName={(params) =>
                    getGridRowColorClass(params.row.estat, NOTIFICACIO_ESTAT_ENUM_MAP)
                }
                sx={generateGridRowStylesFromMap(NOTIFICACIO_ESTAT_ENUM_MAP)}
                // sx={{
                //     '& .MuiDataGrid-cell': {
                //         alignItems: 'flex-start',
                //     },
                // }}
            />
            {dialogComponent}
        </GridPage>
    );
};

export default NotificacioGrid;
