import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Chip from '@mui/material/Chip';
import { GRID_DETAIL_PANEL_TOGGLE_COL_DEF } from '@mui/x-data-grid-pro';
import {
    MuiDataGrid,
    useResourceApiService,
    MuiDataGridColDef,
    MuiFilter,
    MuiDataGridApiRef,
    useMuiDataGridApiRef,
    FilterApi,
    springFilterBuilder as filterBuilder,
    useFilterApiRef,
    springFilterBuilder,
    useMuiDataGridContext,
    useMuiActionReportLogic,
    useBaseAppContext,
} from 'reactlib';
import { useNotibContext } from '../../components/NotibContext';
import { useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import NotificacioGridEnviaments from './NotificacioGridEnviaments';
import { useNotificacioDetailDialog } from './NotificacioDetailDialog';
import { Box, Grid, IconButton } from '@mui/material';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import { formatEndOfDay, formatStartOfDay } from '../../utils/dateUtils';
import AccionsMassives, { MenuOption } from '../../components/AccionsMassives';

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

const MenuActions: React.FC = () => {
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

const ContentFilter: React.FC<{
    filterApiRef: React.RefObject<FilterApi>;
    gridApiRef: MuiDataGridApiRef;
}> = (props) => {
    const { filterApiRef, gridApiRef } = props;
    const { t } = useTranslation();
    const [advancedFilter, setAdvancedFilter] = React.useState(false);

    const handleButtonClick = () => {
        filterApiRef.current.clear();
    };

    const advancedFilterClick = () => {
        setAdvancedFilter(!advancedFilter);
    };

    const refreshButtonClick = () => {
        gridApiRef.current?.refresh();
    };

    return (
        <Grid container spacing={2}>
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
                    <GridFormField size={3.5} name="procediment" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'PROCEDIMENT'`))}/>
                    <GridFormField size={3.5} name="servei" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'SERVEI'`))}/>
                    <GridFormField size={2} name="tipusUsuari" />
                    <GridFormField size={3} name="createdBy" />
                    <GridFormField size={3} name="referencia" />
                    <GridFormField size={2.5} name="registreNumeroSortida" />
                    <GridFormField size={1.75} name="dataCaducitatInici" />
                    <GridFormField size={1.75} name="dataCaducitatFi" />
                    <GridButtonField size={0.5} name="nomesLesMeves" icon={'person'} hiddenLabel/>
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
            <Grid size={0.5}>
                <IconButton
                    onClick={refreshButtonClick}
                    title={t('component.GridToolbarButton.refresh')}
                >
                    <Icon>refresh</Icon>
                </IconButton>
            </Grid>
            <MenuActions />
        </Grid>
    );
};

const EnviamentGridFilter: React.FC<{ gridApiRef: MuiDataGridApiRef }> = (props) => {
    const { gridApiRef } = props;
    const filterApiRef = useFilterApiRef();
    const { currentUser } = useNotibContext();

    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
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

    return (
        <MuiFilter
            resourceName="notificacioResource"
            code="FILTER_NOTIFICACIO"
            apiRef={filterApiRef}
            springFilterBuilder={springFilterBuilder}
            componentProps={{ sx: { mb: 2, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} gridApiRef={gridApiRef} />
        </MuiFilter>
    );
};

const NotificacioGrid = () => {
    const { t } = useTranslation();
    const gridApiRef = useMuiDataGridApiRef();
    const { dialogComponent, onDetailClick } = useNotificacioDetailDialog();
    const { currentActions: apiCurrentActions } = useResourceApiService('notificacioResource');
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;
    const { setMarginsDisabled } = useBaseAppContext();
    
        React.useEffect(() => {
            setMarginsDisabled(true);
            return () => setMarginsDisabled(false);
        }, [setMarginsDisabled]);

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
                field: ' ',
                headerName: t('page.notificacio.grid.column.detalls'),
                flex: 1.2,
                sortable: false,
                exportable: false,
                pinnable: false,
                hideable: false,
                renderHeader: () => null,
                renderCell: (params: any) => {
                    return (
                        <Button
                            variant="outlined"
                            size="small"
                            startIcon={<Icon>info</Icon>}
                            onClick={() => onDetailClick(params.id)}
                        >
                            {t('page.notificacio.grid.enviament.detalls')}
                        </Button>
                    );
                },
            },
            {
                ...GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
                headerName: t('page.notificacio.grid.column.desplegar'),
                hideable: false,
            },
        ],
        []
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();

    return (
        <Box sx={{ height: '1000px' }}>
            <MuiDataGrid
                apiRef={gridApiRef}
                title={t('page.notificacio.grid.title')}
                resourceName="notificacioResource"
                columns={columns}
                defaultSortModel={[{ field: 'createdDate', sort: 'desc' }]}
                paginationActive
                toolbarHideCreate
                toolbarType="upper"
                toolbarHideQuickFilter
                toolbarHideRefresh
                toolbarCreateLink="form"
                toolbarAdditionalRow={<EnviamentGridFilter gridApiRef={gridApiRef} />}
                toolbarElementsWithPositions={
                    isCreateLinkPresent
                        ? [
                              {
                                  position: 2,
                                  element: <NotificacioAddButton />,
                              },
                          ]
                        : undefined
                }
                readOnly
                selectionActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                getDetailPanelContent={({ row }) => <NotificacioGridEnviaments id={row.id} />}
                getDetailPanelHeight={() => 'auto'}
                {...pageSizeOptionsDataGridProps}
            />
            {dialogComponent}
        </Box>
    );
};

export default NotificacioGrid;
