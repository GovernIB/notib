import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Chip from '@mui/material/Chip';
import { GRID_DETAIL_PANEL_TOGGLE_COL_DEF } from '@mui/x-data-grid-pro';
import { GridPage, MuiDataGrid, useResourceApiService, MuiDataGridColDef } from 'reactlib';
import { useNotibContext } from '../../components/NotibContext';
import NotificacioGridEnviaments from './NotificacioGridEnviaments';
import { useNotificacioDetailDialog } from './NotificacioDetailDialog';

const useDatagridPageSizeOptionsProps = () => {
    const { currentUser, currentUserGridPageSizeOptions } = useNotibContext();
    return {
        defaultPaginationModel: {
            page: 0,
            pageSize: currentUser.numElementsPaginaDefecteAsInt ?? -1,
        },
        pageSizeOptions: currentUserGridPageSizeOptions,
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

const NotificacioGrid = () => {
    const { t } = useTranslation();
    const { dialogComponent, onDetailClick } = useNotificacioDetailDialog();
    const { currentActions: apiCurrentActions } = useResourceApiService('notificacioResource');
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;
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
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.notificacio.grid.title')}
                resourceName="notificacioResource"
                columns={columns}
                defaultSortModel={[{ field: 'createdDate', sort: 'desc' }]}
                paginationActive
                toolbarHideCreate
                toolbarCreateLink="form"
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
                getDetailPanelContent={({ row }) => <NotificacioGridEnviaments id={row.id} />}
                getDetailPanelHeight={() => 'auto'}
                {...pageSizeOptionsDataGridProps}
            />
            {dialogComponent}
        </GridPage>
    );
};

export default NotificacioGrid;
