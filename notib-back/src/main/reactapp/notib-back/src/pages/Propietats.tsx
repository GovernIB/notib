import React from 'react';
import Grid from '@mui/material/Grid';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Icon from '@mui/material/Icon';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import { useResourceApiService } from 'reactlib';

const PropietatGroups: React.FC<{ onChange?: (group: string | undefined) => void }> = (props) => {
	const { onChange } = props;
	const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configGroupResource');
	const [configGroups, setConfigGroups] = React.useState<any[]>();
	const [selectedGroup, setSelectedGroup] = React.useState<string>('GENERAL');
	React.useEffect(() => {
		if (apiIsReady) {
			apiFind({ unpaged: true }).then((response) => {
				setConfigGroups(response.rows);
			});
		}
	}, [apiIsReady]);
	React.useEffect(() => {
		onChange?.(selectedGroup);
	}, [selectedGroup]);
	return (
		<List>
			{configGroups?.map((g) => (
				<ListItem key={g.key} disablePadding>
					<ListItemButton
						onClick={() => setSelectedGroup(g.key)}
						selected={g.key === selectedGroup}>
						<ListItemText primary={g.description} />
					</ListItemButton>
				</ListItem>
			))}
		</List>
	);
};

const PropietatsList: React.FC<{ group?: string }> = (props) => {
	const { group } = props;
	const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configResource');
	const [configs, setConfigs] = React.useState<any[]>();
	React.useEffect(() => {
		if (apiIsReady) {
			const args = {
				unpaged: true,
			};
			apiFind(args).then((response) => {
				const configs = response.rows.filter((r) => true);
				console.log('>>> configs', configs);
				setConfigs(configs);
			});
		}
	}, [apiIsReady, group]);
	return (
		<List>
			{configs?.map((g) => (
				<ListItem key={g.key} disablePadding>
					<ListItemButton>
						<ListItemText primary={g.description} />
					</ListItemButton>
				</ListItem>
			))}
		</List>
	);
};

const Propietats: React.FC = () => {
	const [selectedGroup, setSelectedGroup] = React.useState<string>();
	return (
		<Grid container spacing={2}>
			<Grid size={12} sx={{ px: 10 }}>
				<TextField
					label="Cercar a les propietats"
					variant="outlined"
					fullWidth
					size="small"
					slotProps={{
						input: {
							startAdornment: (
								<InputAdornment position="start">
									<Icon fontSize="small">search</Icon>
								</InputAdornment>
							),
						},
					}}
				/>
			</Grid>
			<Grid size={3}>
				<PropietatGroups onChange={setSelectedGroup} />
			</Grid>
			<Grid size={9}>
				<PropietatsList group={selectedGroup} />
			</Grid>
		</Grid>
	);
};

export default Propietats;
