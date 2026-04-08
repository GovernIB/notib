import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Chip from '@mui/material/Chip';
import {
    GridColDef,
    GridApiPro,
    GridColumnResizeParams,
    useGridApiRef,
    gridColumnFieldsSelector,
    GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
} from '@mui/x-data-grid-pro';
import { GridPage, MuiDataGrid, useResourceApiService, MuiDataGridColDef } from 'reactlib';
import { useNotibContext } from '../../components/NotibContext';
import NotificacioGridEnviaments from './NotificacioGridEnviaments';
import { useNotificacioDetailDialog } from './NotificacioDetailDialog';

const LOCAL_STORAGE_PREFIX = 'NOTIB_ST_';

const useDatagridPersistentState = (
    apiRef: React.RefObject<GridApiPro | null>,
    columns: GridColDef[],
    key: string,
    storeInLocalStorage?: boolean
) => {
    const storageKey = LOCAL_STORAGE_PREFIX + key;
    const loadInitialState = () => {
        try {
            const storage = storeInLocalStorage ? localStorage : sessionStorage;
            const raw = storage.getItem(storageKey);
            return raw ? JSON.parse(raw) : null;
        } catch {
            return null;
        }
    };
    const saveState = (state: any) => {
        try {
            const storage = storeInLocalStorage ? localStorage : sessionStorage;
            storage.setItem(storageKey, JSON.stringify(state));
        } catch {}
    };
    const initialState = loadInitialState();
    const [widths, setWidths] = React.useState<Record<string, number>>(initialState?.widths || {});
    const [orderedFields, setOrderedFields] = React.useState<string[]>(
        initialState?.orderedFields || columns.map((c) => c.field)
    );
    const [columnVisibilityModel, setColumnVisibilityModel] = React.useState(
        initialState?.columnVisibilityModel || {}
    );
    const [pinnedColumns, setPinnedColumns] = React.useState(initialState?.pinnedColumns || {});
    React.useEffect(() => {
        saveState({
            widths,
            orderedFields,
            columnVisibilityModel,
            pinnedColumns,
        });
    }, [widths, orderedFields, columnVisibilityModel, pinnedColumns]);
    const onColumnWidthChange = React.useCallback(
        (params: GridColumnResizeParams) => {
            const { colDef, width } = params;
            setWidths((prev) => ({ ...prev, [colDef.field]: width }));
        },
        [setWidths]
    );
    const onColumnOrderChange = React.useCallback(() => {
        setOrderedFields(gridColumnFieldsSelector(apiRef));
    }, [apiRef, setOrderedFields]);
    const onColumnVisibilityModelChange = React.useCallback((model: any) => {
        setColumnVisibilityModel(model);
    }, []);
    const onPinnedColumnsChange = React.useCallback((model: any) => {
        setPinnedColumns(model);
    }, []);
    const computedColumns = React.useMemo(
        () =>
            orderedFields.reduce<GridColDef[]>((acc, field) => {
                const column = columns.find((col) => col.field === field);
                if (!column) {
                    return acc;
                }
                if (widths[field]) {
                    acc.push({
                        ...column,
                        flex: 0,
                        width: widths[field],
                    });
                    return acc;
                }
                acc.push(column);
                return acc;
            }, []),
        [columns, widths, orderedFields]
    );
    return {
        columns: computedColumns,
        dataGridProps: {
            onColumnWidthChange,
            onColumnOrderChange,
            onColumnVisibilityModelChange,
            onPinnedColumnsChange,
            columnVisibilityModel,
            pinnedColumns,
        },
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
    const apiRef = useGridApiRef();
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
    const { columns: persistentStateColumns, dataGridProps: persistentStateDataGridProps } =
        useDatagridPersistentState(apiRef, columns, 'NOT_DG_STATE');
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.notificacio.grid.title')}
                resourceName="notificacioResource"
                columns={persistentStateColumns}
                sortModel={[{ field: 'createdDate', sort: 'desc' }]}
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
                getDetailPanelContent={({ row }) => <NotificacioGridEnviaments id={row.id} />}
                getDetailPanelHeight={() => 'auto'}
                datagridApiRef={apiRef}
                {...persistentStateDataGridProps}
            />
            {dialogComponent}
        </GridPage>
    );
};

export default NotificacioGrid;
