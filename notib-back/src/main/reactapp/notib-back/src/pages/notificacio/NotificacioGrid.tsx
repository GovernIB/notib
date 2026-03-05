import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Chip from '@mui/material/Chip';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { GRID_DETAIL_PANEL_TOGGLE_COL_DEF } from '@mui/x-data-grid-pro';
import { GridPage, MuiDataGrid, useResourceApiService, MuiDataGridColDef } from 'reactlib';
import { useNotibContext } from '../../components/NotibContext';

const NotificacioDetailPanel: React.FC<{ id: any }> = (props) => {
    const { id } = props;
    const { t } = useTranslation();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService(
        'notificacioEnviamentResource'
    );
    const [enviaments, setEnviaments] = React.useState<any[]>();
    React.useEffect(() => {
        if (apiIsReady) {
            const args = {
                filter: 'notificacio.id:' + id,
                unpaged: true,
            };
            apiFind(args).then((response) => {
                setEnviaments(response.rows);
            });
        }
    }, [apiIsReady]);
    return (
        enviaments != null && (
            <TableContainer
                component={Paper}
                elevation={2}
                sx={{
                    mx: 2,
                    my: 2,
                    width: 'calc(100% - 32px)',
                }}
            >
                <Table size="small" aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                {t('page.notificacio.grid.enviament.column.interessat')}
                            </TableCell>
                            <TableCell>
                                {t('page.notificacio.grid.enviament.column.representant')}
                            </TableCell>
                            <TableCell>
                                {t('page.notificacio.grid.enviament.column.estatPostal')}
                            </TableCell>
                            <TableCell>
                                {t('page.notificacio.grid.enviament.column.estatTelematica')}
                            </TableCell>
                            <TableCell></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {enviaments.map((e) => (
                            <TableRow key={e.id}>
                                <TableCell component="th" scope="row">
                                    {e.titular.description}
                                </TableCell>
                                <TableCell component="th" scope="row"></TableCell>
                                <TableCell component="th" scope="row"></TableCell>
                                <TableCell component="th" scope="row"></TableCell>
                                <TableCell component="th" scope="row" sx={{ width: '1px' }}>
                                    <Button
                                        variant="outlined"
                                        size="small"
                                        startIcon={<Icon>info</Icon>}
                                    >
                                        Detalls
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        )
    );
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
    const { currentActions: apiCurrentActions } = useResourceApiService('notificacioResource');
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'enviamentTipus',
                headerName: '',
                flex: 0.4,
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
                flex: 1.2,
                sortable: false,
                hideable: false,
                exportable: false,
                pinnable: false,
                renderCell: (_params: any) => {
                    return (
                        <Button variant="outlined" size="small" startIcon={<Icon>info</Icon>}>
                            {t('page.notificacio.grid.enviament.detalls')}
                        </Button>
                    );
                },
            },
            GRID_DETAIL_PANEL_TOGGLE_COL_DEF,
        ],
        []
    );
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.notificacio.grid.title')}
                resourceName="notificacioResource"
                columns={columns}
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
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
                readOnly
                selectionActive
                getDetailPanelContent={({ row }) => <NotificacioDetailPanel id={row.id} />}
                getDetailPanelHeight={() => 'auto'}
            />
        </GridPage>
    );
};

export default NotificacioGrid;
