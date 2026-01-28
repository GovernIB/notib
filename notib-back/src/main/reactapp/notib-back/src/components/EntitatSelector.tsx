import React from 'react';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import { useNotibContext } from './NotibContext';

const EntitatSelector: React.FC = () => {
	const { entitatsAvailable, currentEntitat, setCurrentEntitat } = useNotibContext();
	const handleEntitatChange = (event: React.ChangeEvent<HTMLInputElement>) => {
		setCurrentEntitat(event.target.value);
		//navigate('/', { replace: true });
	};
	return entitatsAvailable ? (
		<TextField
			value={currentEntitat ?? ''}
			onChange={handleEntitatChange}
			size="small"
			select
			slotProps={{
				input: {
					startAdornment: (
						<InputAdornment position="start" sx={{ mr: 2 }}>
							<Icon>layers</Icon>
						</InputAdornment>
					),
				},
			}}
			sx={{ mr: 1 }}>
			{entitatsAvailable.map((e) => (
				<MenuItem key={e} value={e}>
					<ListItemText>{e.nom}</ListItemText>
				</MenuItem>
			))}
		</TextField>
	) : null;
};

export default EntitatSelector;
