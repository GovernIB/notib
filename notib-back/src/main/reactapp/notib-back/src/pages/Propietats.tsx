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

const PropietatGroups: React.FC<{ onChange?: (groupId: number | undefined) => void }> = (props) => {
    const { onChange } = props;
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configGroupResource');
    const [configGroups, setConfigGroups] = React.useState<any[]>();
    const [selectedGroupId, setSelectedGroupId] = React.useState<number>();
    React.useEffect(() => {
        if (apiIsReady) {
            apiFind({ unpaged: true }).then((response) => {
                setConfigGroups(response.rows);
            });
        }
    }, [apiIsReady]);
    React.useEffect(() => {
        onChange?.(selectedGroupId);
    }, [selectedGroupId]);
    return (
        <List>
            {configGroups?.map((g) => (
                <ListItem key={g.key} disablePadding>
                    <ListItemButton
                        onClick={() => setSelectedGroupId(g.id)}
                        selected={g.id === selectedGroupId}>
                        <ListItemText primary={g.description} />
                    </ListItemButton>
                </ListItem>
            ))}
        </List>
    );
};

const PropietatsList: React.FC<{ quickFilter?: string; groupId?: number }> = (props) => {
    const { quickFilter, groupId } = props;
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configResource');
    const [configs, setConfigs] = React.useState<any[]>();
    React.useEffect(() => {
        if (apiIsReady && groupId != null) {
            const args = {
                quickFilter,
                filter: 'configGroup.id:' + groupId,
                unpaged: true,
            };
            apiFind(args).then((response) => {
                const configs = response.rows.filter(() => true);
                setConfigs(configs);
            });
        }
    }, [apiIsReady, quickFilter, groupId]);
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
    const [quickFilter, setQuickFilter] = React.useState<string>('');
    const [selectedGroupId, setSelectedGroupId] = React.useState<number>();
    return (
        <Grid container spacing={2}>
            <Grid size={12} sx={{ px: 10 }}>
                <TextField
                    value={quickFilter}
                    onChange={(event) => setQuickFilter(event.target.value)}
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
                <PropietatGroups onChange={setSelectedGroupId} />
            </Grid>
            <Grid size={9}>
                <PropietatsList quickFilter={quickFilter} groupId={selectedGroupId} />
            </Grid>
        </Grid>
    );
};

export default Propietats;
