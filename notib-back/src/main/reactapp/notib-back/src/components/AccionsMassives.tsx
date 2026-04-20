import { Box, Button, Chip, Icon, Menu, MenuItem } from '@mui/material';
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
}

const AccionsMassives: React.FC<AccionsMassivesProps> = (props) => {
    const { t } = useTranslation();
    const {
        options,
        buttonLabel = t('component.AccionsMassives.labelBoto'),
        sizeSelection,
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

    return (
        <>
            <Button
                id="basic-button"
                aria-controls={open ? 'basic-menu' : undefined}
                aria-haspopup="true"
                aria-expanded={open ? 'true' : undefined}
                onClick={handleClick}
                endIcon={<Icon>{open ? 'arrow_drop_up' : 'arrow_drop_down'}</Icon>}
                variant="outlined"
                sx={{ mr: 1 }}
            >
                <Box sx={{ display: 'flex', gap: 1, justifyContent: 'space-between' }}>
                    <Chip label={sizeSelection} size="small" color="default" />
                    {buttonLabel}
                </Box>
            </Button>
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
