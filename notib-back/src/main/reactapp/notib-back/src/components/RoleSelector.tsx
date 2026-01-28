import React from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import { useNotibContext } from './NotibContext';

const RoleSelector: React.FC = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { rolesAvailable, currentRole, setCurrentRole } = useNotibContext();
    const handleRoleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setCurrentRole(event.target.value);
        navigate('/', { replace: true });
    };
    return rolesAvailable ? (
        <TextField
            value={currentRole ?? ''}
            onChange={handleRoleChange}
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
            {rolesAvailable.map((r) => (
                <MenuItem key={r} value={r}>
                    <ListItemText>{t('component.RoleSelector.role.' + r)}</ListItemText>
                </MenuItem>
            ))}
        </TextField>
    ) : null;
};

export default RoleSelector;
