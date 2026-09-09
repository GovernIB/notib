import React from 'react';
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import { useNotibContext } from './NotibContext';
import { getRoleHomePath, isExternalPath, isPathAccessibleForRole } from '../routeAccess';

const RoleSelector: React.FC = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const location = useLocation();
    const { rolesAvailable, currentRole, setCurrentRole } = useNotibContext();

    const handleRoleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newRole = event.target.value;
        setCurrentRole(newRole);
        // Si la pàgina actual no és accessible amb el nou rol es redirigeix a la pantalla inicial del rol;
        // en cas contrari es manté la pàgina actual.
        if (isPathAccessibleForRole(location.pathname, newRole, t)) {
            return;
        }
        const homePath = getRoleHomePath(newRole);
        if (isExternalPath(homePath)) {
            window.location.href = homePath;
            return;
        }
        navigate(homePath, { replace: true });
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
            sx={{ mr: 1 }}
        >
            {rolesAvailable.map((rol) => (
                <MenuItem key={rol} value={rol}>
                    <ListItemText>{t(`component.RoleSelector.role.${rol}`)}</ListItemText>
                </MenuItem>
            ))}
        </TextField>
    ) : null;
};

export default RoleSelector;
