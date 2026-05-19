import { Box, Button, ButtonGroup, Chip, Icon, Menu, MenuItem, Tooltip } from '@mui/material';
import { GridApiPro } from '@mui/x-data-grid-pro';
import React from 'react';
import { useTranslation } from 'react-i18next';

export interface MenuOption {
    label: string;
    onClick: () => void;
    icon?: string;
    disabled?: boolean;
}

interface AccionsMassivesProps {
    options: MenuOption[];
    buttonLabel?: string;
    sizeSelection?: number;
    datagridApiRef?: React.RefObject<GridApiPro | null>;
}

const AccionsMassives: React.FC<AccionsMassivesProps> = (props) => {
    const { t } = useTranslation();
    const {
        options,
        buttonLabel = t('component.AccionsMassives.labelBoto'),
        sizeSelection,
        datagridApiRef,
    } = props;

    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);

    const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };

    const handleOptionClick = (optionOnClick: () => void) => {
        handleClose();
        optionOnClick();
    };

    const handleSelectAllGlobal = () => {
        alert("Pendent d'implementar el handleSelectAllGlobal");
        // if (datagridApiRef?.current) {
        //     const allIds = datagridApiRef.current.getAllRowIds();
        //     datagridApiRef.current.selectRows(allIds, true, true);
        // }
    };

    const handleDeselectAllGlobal = () => {
        alert("Pendent d'implementar el handleDeselectAllGlobl");
        // if (datagridApiRef?.current) {
        //     datagridApiRef.current.setRowSelectionModel([]);
        // }
    };

    return (
        <>
            <ButtonGroup variant="outlined" aria-label="Basic button group">
                <Tooltip title={t('component.AccionsMassives.selectAll')} arrow>
                    <Button color="primary" onClick={handleSelectAllGlobal} >
                        <Icon fontSize='small'>check_box</Icon>
                    </Button>
                </Tooltip>
                <Tooltip title={t('component.AccionsMassives.deselectAll')} arrow>
                    <Button color="primary" onClick={handleDeselectAllGlobal} >
                        <Icon fontSize='small'>check_box_outline_blank</Icon>
                    </Button>
                </Tooltip>
                <Button
                    id="basic-button"
                    aria-controls={open ? 'basic-menu' : undefined}
                    aria-haspopup="true"
                    aria-expanded={open ? 'true' : undefined}
                    onClick={handleClick}
                    endIcon={<Icon>{open ? 'arrow_drop_up' : 'arrow_drop_down'}</Icon>}
                    variant="outlined"
                    sx={{ mr: 1, textTransform: 'none' }}
                >
                    <Box sx={{ display: 'flex', gap: 1, justifyContent: 'space-between' }}>
                        <Chip label={sizeSelection} size="small" color="default" />
                        {buttonLabel}
                    </Box>
                </Button>
            </ButtonGroup>

            <Menu
                id="basic-menu"
                anchorEl={anchorEl}
                open={open}
                keepMounted
                onClose={handleClose}
                slotProps={{
                    list: {
                        'aria-labelledby': 'basic-button',
                    },
                }}
            >
                {options.map((option, index) => (
                    <MenuItem
                        key={index}
                        onClick={() => handleOptionClick(option.onClick)}
                        disabled={option.disabled}
                    >
                        {option.icon && <Icon sx={{ mr: 1 }}>{option.icon}</Icon>}
                        {option.label}
                    </MenuItem>
                ))}
            </Menu>
        </>
    );
};

export default AccionsMassives;
