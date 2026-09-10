import React from 'react';
import { useTranslation } from 'react-i18next';
import MenuItem from '@mui/material/MenuItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Icon from '@mui/material/Icon';
import { getClassicAppUrl, markInterficiePreference } from '../appUrls';

// Opció, al menú d'usuari, per anar a la interfície clàssica (JSP).
const SwitchInterfaceMenuItem: React.FC = () => {
    const { t } = useTranslation();
    // No es crida preventDefault: es marca la preferència i es deixa navegar l'enllaç amb normalitat.
    const handleClick = () => markInterficiePreference('jsp');
    return (
        <MenuItem component="a" href={getClassicAppUrl()} onClick={handleClick} sx={{ width: '100%' }}>
            <ListItemIcon>
                <Icon fontSize="small">swap_horiz</Icon>
            </ListItemIcon>
            <ListItemText>{t('component.SwitchInterface.classica')}</ListItemText>
        </MenuItem>
    );
};

export default SwitchInterfaceMenuItem;
