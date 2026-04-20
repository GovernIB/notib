import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Chip from '@mui/material/Chip';
import {
    GridRenderCellParams,
    useGridApiContext,
    useGridSelector,
    gridDetailPanelExpandedRowsContentCacheSelector,
    gridDetailPanelExpandedRowIdsSelector,
    GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
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
import { Grid, IconButton } from '@mui/material';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import AccionsMassives, { MenuOption } from '../../components/AccionsMassives';

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
            title={isExpanded ? t('page.notificacio.grid.column.ocultar') : t('page.notificacio.grid.column.mostrar')}
            aria-label={isExpanded ? t('page.notificacio.grid.column.ocultar') : t('page.notificacio.grid.column.mostrar')}
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

const useDataGridColumns = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'enviamentTipus',
                flex: 0.4,
                renderHeader: () => null,
                renderCell: (params: any) => {
                    const letter = params.value?.substring(0, 1);
                    return <Chip label={letter} size="small" title={params.formattedValue} />;
                },
            },
            {
                field: 'createdDate',
                flex: 1.4,
            },
            {
                field: 'enviadaDate',
                flex: 1.4,
            },
            {
                field: 'registreNums',
                flex: 1.4,
            },
            {
                field: 'organGestor',
                flex: 2,
            },
            {
                field: 'procediment',
                flex: 2,
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
                flex: 1,
            },
            {
                field: 'concepte',
                flex: 3,
            },
            {
                field: 'createdBy',
                flex: 1,
            },
            {
                field: 'titular',
                flex: 1,
            },
            {
                field: 'estat',
                flex: 1,
            },
            {
                ...GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
                hideable: false,
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

const MassiveActionsButton: React.FC = () => {
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

    return <AccionsMassives options={opcionsMenu} sizeSelection={selection?.ids?.size} />;
};

const ContentFilter: React.FC = () => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const [advancedFilter, setAdvancedFilter] = React.useState(false);

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
    const { dialogComponent, onDetailClick } = useNotificacioDetailDialog();
    const { currentActions: apiCurrentActions } = useResourceApiService('notificacioResource');
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;
    const columns = useDataGridColumns();
    const springFilterBuilder = useSpringFilterBuilder();
    const filterDataGridProps = useDatagridFilterProps(
        'notificacioResource',
        'FILTER_NOTIFICACIO',
        springFilterBuilder,
        <ContentFilter />
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                title={t('page.notificacio.grid.title')}
                resourceName="notificacioResource"
                columns={columns}
                defaultSortModel={[{ field: 'createdDate', sort: 'desc' }]}
                paginationActive
                selectionActive
                readOnly
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
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
                        element: <MassiveActionsButton />,
                    },
                ]}
                onRowClick={(params) => onDetailClick(params.id)}
                rowActionsColumnIndex={11}
                rowActionsColumnProps={{
                    flex: 0.5,
                }}
                rowAdditionalActions={[
                    {
                        label: t('page.notificacio.grid.column.detalls'),
                        title: t('page.notificacio.grid.column.detalls'),
                        icon: 'info',
                        showInMenu: false,
                        onClick: (id) => onDetailClick(id),
                    },
                ]}
                getDetailPanelContent={({ row }) => <NotificacioGridEnviaments id={row.id} />}
                getDetailPanelHeight={() => 'auto'}
            />
            {dialogComponent}
        </GridPage>
    );
};

export default NotificacioGrid;
