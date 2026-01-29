import React from 'react';
import Grid from '@mui/material/Grid';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { TreeItem } from '@mui/x-tree-view/TreeItem';
import { useDebounce, useResourceApiService } from 'reactlib';

const PropietatsQuickFilter: React.FC<{ onChange: (quickFilter: string | undefined) => void }> = (
    props
) => {
    const { onChange } = props;
    const [quickFilter, setQuickFilter] = React.useState<string>('');
    const quickFilterDebounced = useDebounce(quickFilter);
    React.useEffect(() => {
        onChange?.(quickFilterDebounced);
    }, [quickFilterDebounced]);
    return (
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
                    endAdornment: quickFilter && (
                        <InputAdornment position="end">
                            <IconButton size="small" onClick={() => setQuickFilter('')}>
                                <Icon fontSize="inherit">clear</Icon>
                            </IconButton>
                        </InputAdornment>
                    ),
                },
            }}
        />
    );
};

const PropietatsGroupTreeItems: React.FC<{
    configGroups?: any[];
    parentId?: any;
}> = (props) => {
    const { configGroups, parentId } = props;
    const configGroupsFilterByParentId = (parentId?: any) => {
        return configGroups?.filter((g) => (parentId ?? null) === (g.parent?.id ?? null));
    };
    const filteredGroups = configGroupsFilterByParentId(parentId);
    return filteredGroups?.map((g) => (
        <TreeItem key={g.key} itemId={g.id} label={g.description}>
            {configGroupsFilterByParentId(g.id)?.length ? (
                <PropietatsGroupTreeItems configGroups={configGroups} parentId={g.id} />
            ) : null}
        </TreeItem>
    ));
};

const PropietatsGroups: React.FC<{
    quickFilter?: string;
    onChange: (groupId: number | undefined) => void;
}> = (props) => {
    const { quickFilter, onChange } = props;
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configGroupResource');
    const [configGroups, setConfigGroups] = React.useState<any[]>();
    const [selectedGroupId, setSelectedGroupId] = React.useState<number>();
    const [selectedItems, setSelectedItems] = React.useState<string>('');
    React.useEffect(() => {
        if (apiIsReady) {
            const args = {
                filter: quickFilter?.length
                    ? "exists(configs.key~'%" +
                      quickFilter +
                      "%' or configs.description~'%" +
                      quickFilter +
                      "%') or exists(children.configs.key~'%" +
                      quickFilter +
                      "%' or children.configs.description~'%" +
                      quickFilter +
                      "%')"
                    : undefined,
                sorts: ['position,asc'],
                unpaged: true,
            };
            apiFind(args).then((response) => {
                const configGroups = response.rows;
                setConfigGroups(configGroups);
                if (configGroups.length) {
                    const isSelectedGroupIdInConfigGroups = configGroups.find(
                        (g) => g.id === selectedGroupId
                    );
                    if (!isSelectedGroupIdInConfigGroups) {
                        setSelectedGroupId(response.rows[0].id);
                        setSelectedItems('' + response.rows[0].id);
                    }
                }
            });
        }
    }, [apiIsReady, quickFilter]);
    React.useEffect(() => {
        onChange?.(selectedGroupId);
    }, [selectedGroupId]);
    return (
        <SimpleTreeView
            selectedItems={selectedItems}
            onSelectedItemsChange={(_event, ids) =>
                setSelectedGroupId(ids != null ? Number(ids) : undefined)
            }>
            <PropietatsGroupTreeItems configGroups={configGroups} />
        </SimpleTreeView>
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
    const [quickFilter, setQuickFilter] = React.useState<string>();
    const [selectedGroupId, setSelectedGroupId] = React.useState<number>();
    return (
        <Grid container spacing={2}>
            <Grid size={12} sx={{ px: 10 }}>
                <PropietatsQuickFilter onChange={setQuickFilter} />
            </Grid>
            <Grid size={3}>
                <PropietatsGroups quickFilter={quickFilter} onChange={setSelectedGroupId} />
            </Grid>
            <Grid size={9}>
                <PropietatsList groupId={selectedGroupId} quickFilter={quickFilter} />
            </Grid>
        </Grid>
    );
};

export default Propietats;
