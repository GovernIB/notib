import React from 'react';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import { useNotibContext } from './NotibContext';

const OrganSelector: React.FC = () => {

    const { organsAvailable, currentOrganId, setCurrentOrganId } = useNotibContext();
    const handleOrganChange = (event: React.ChangeEvent<HTMLInputElement>) => setCurrentOrganId(Number(event.target.value));
    return !organsAvailable ? null : (
        <TextField
            value={currentOrganId != null ? '' + currentOrganId : ''}
            onChange={handleOrganChange}
            size="small"
            select
            slotProps={{input: {startAdornment: (<InputAdornment position="start" sx={{ mr: 2 }}><Icon>account_tree</Icon></InputAdornment>)}}}
            sx={{ mr: 1 }}
        >
            {organsAvailable.map(o => (
                <MenuItem key={o.id} value={o.id}>
                    <ListItemText>{o.nom}</ListItemText>
                </MenuItem>
            ))}
        </TextField>
    );
};

export default OrganSelector;
