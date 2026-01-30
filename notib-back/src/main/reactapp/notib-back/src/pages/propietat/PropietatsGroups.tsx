import React from 'react';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { TreeItem } from '@mui/x-tree-view/TreeItem';
import { useResourceApiService } from 'reactlib';

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

export const PropietatsGroups: React.FC<{
    quickFilter?: string;
    onChange: (group: any) => void;
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
        onChange?.(configGroups?.find((g) => g.id == selectedGroupId));
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
