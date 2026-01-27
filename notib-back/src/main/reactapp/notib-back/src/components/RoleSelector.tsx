import React from 'react';
import { useTranslation } from 'react-i18next';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import { useNotibContext } from './NotibContext';

const RoleSelector: React.FC = () => {
    const { t } = useTranslation();
    const { currentUserRealmRoles, currentRole, setCurrentRole } = useNotibContext();
    React.useEffect(() => {
        if (currentUserRealmRoles?.length === 1) {
            setCurrentRole(currentUserRealmRoles[0]);
        }
    }, [currentUserRealmRoles]);
    return currentUserRealmRoles ? (
        <TextField
            value={currentRole ?? ''}
            onChange={(event) => setCurrentRole(event.target.value)}
            size="small"
            select
            slotProps={{
                input: {
                    startAdornment: (
                        <InputAdornment position="start" sx={{ mr: 2 }}>
                            <Icon>assignment_ind</Icon>
                        </InputAdornment>
                    ),
                },
            }}
            sx={{ mr: 1 }}>
            {currentUserRealmRoles.map((r) => (
                <MenuItem key={r} value={r}>
                    <ListItemText>{t('component.RoleSelector.role.' + r)}</ListItemText>
                </MenuItem>
            ))}
        </TextField>
    ) : null;
};

export default RoleSelector;
