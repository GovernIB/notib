import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { GridPage, MuiDataGrid, useResourceApiService } from 'reactlib';

const columns = [
    {
        field: 'registreData',
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
];

const NotificacioAddButton: React.FC = () => {
    const { t } = useTranslation();
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };
    return (
        <>
            <Button
                variant="contained"
                startIcon={<Icon>add</Icon>}
                onClick={handleClick}
                sx={{ mr: 1 }}
            >
                {t('page.notificacio.grid.new.title')}
            </Button>
            <Menu anchorEl={anchorEl} open={open} onClose={handleClose}>
                <MenuItem component={Link} to="form?type=NOTIFICACIO" onClick={handleClose}>
                    {t('page.notificacio.grid.new.NOTIFICACIO')}
                </MenuItem>
                <MenuItem component={Link} to="form?type=COMUNICACIO" onClick={handleClose}>
                    {t('page.notificacio.grid.new.COMUNICACIO')}
                </MenuItem>
                <MenuItem component={Link} to="form?type=SIR" onClick={handleClose}>
                    {t('page.notificacio.grid.new.SIR')}
                </MenuItem>
            </Menu>
        </>
    );
};

const NotificacioGrid = () => {
    const { t } = useTranslation();
    const { currentActions: apiCurrentActions } = useResourceApiService('notificacioResource');
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;
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
            />
        </GridPage>
    );
};

export default NotificacioGrid;
