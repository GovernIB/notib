import React from 'react';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { useTheme } from '@mui/material/styles';
import {
    GridTreeDataGroupingCell,
    GridRowsProp,
    useGridApiRef,
    gridRowNodeSelector,
} from '@mui/x-data-grid-pro';
import { MuiFilter } from 'reactlib';
import { useNotibContext } from '../components/NotibContext';

export const useDatagridPageSizeOptionsProps = () => {
    const { currentUser, currentUserGridPageSizeOptions } = useNotibContext();
    return {
        defaultPaginationModel: {
            page: 0,
            pageSize: currentUser.numElementsPaginaDefecteAsInt ?? -1,
        },
        pageSizeOptions: currentUserGridPageSizeOptions,
    };
};

export const useDatagridFilterProps = (
    resourceName: string,
    code: string,
    springFilterBuilder: (data: any) => string | undefined,
    content: React.ReactElement,
    minHeight: string = '40px'
) => {
    const [autoFindDisabled, setAutoFindDisabled] = React.useState<boolean>(true);
    const handleSpringFilterChange = (springFilter: string | undefined) => {
        const springFilterEmpty = springFilter == null || springFilter === '';
        autoFindDisabled && setAutoFindDisabled(!springFilterEmpty);
    };
    const filterComponent = (
        <MuiFilter
            resourceName={resourceName}
            code={code}
            persistentStateActive
            springFilterBuilder={springFilterBuilder}
            onSpringFilterChange={handleSpringFilterChange}
            componentProps={{ sx: { mb: 0, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            {content}
        </MuiFilter>
    );
    return {
        loading: autoFindDisabled ? autoFindDisabled : undefined,
        autoFindDisabled,
        toolbarHideQuickFilter: true as true,
        toolbarAdditionalRow: filterComponent,
        toolbarAdditionalRowMinHeight: minHeight,
    };
};

export const useDatagridTreeData = (
    active: boolean,
    headerName: string,
    defaultGroupingExpansionDepth?: number,
    groupingColDefProps?: any
) => {
    const { t } = useTranslation();
    const theme = useTheme();
    const datagridApiRef = useGridApiRef();
    const [rowIds, setRowIds] = React.useState<any[]>();
    const handleRowsChange = (rows: GridRowsProp) => {
        setRowIds(rows?.map((r) => r.id));
    };
    const changeAllNodesExpansion = (expanded: boolean) => {
        rowIds?.forEach((id) => {
            const node = gridRowNodeSelector(datagridApiRef, id) as any;
            if (node?.children?.length) {
                if (expanded) {
                    datagridApiRef.current?.setRowChildrenExpansion(id, expanded);
                } else if (defaultGroupingExpansionDepth == null || node.depth >= defaultGroupingExpansionDepth) {
                    datagridApiRef.current?.setRowChildrenExpansion(id, expanded);
                }
            }
        });
    };
    const getTreeDataPath = (row: any) => {
        return row.path?.map((p: any) => p.description) ?? [row.id];
    };
    return active
        ? {
              perspectives: ['TREE'],
              treeData: true as true,
              getTreeDataPath,
              onRowsChange: handleRowsChange,
              datagridApiRef,
              groupingColDef: {
                  headerName,
                  renderHeader: (params: any) => {
                      return (
                          <div
                              style={{
                                  display: 'flex',
                                  flexGrow: 1,
                                  alignItems: 'center',
                                  justifyContent: 'space-between',
                              }}
                          >
                              <div>{params.colDef.headerName}</div>
                              <div>
                                  <IconButton
                                      size="small"
                                      onClick={() => changeAllNodesExpansion(false)}
                                      title={t('hook.useDataGrid.treeData.collapseAll')}
                                  >
                                      <Icon fontSize="inherit">unfold_less</Icon>
                                  </IconButton>
                                  <IconButton
                                      size="small"
                                      onClick={() => changeAllNodesExpansion(true)}
                                      title={t('hook.useDataGrid.treeData.expandAll')}
                                  >
                                      <Icon fontSize="inherit">unfold_more</Icon>
                                  </IconButton>
                              </div>
                          </div>
                      );
                  },
                  renderCell: (params: any) => {
                      return (
                          <div
                              style={{
                                  display: 'flex',
                                  color: params.rowNode.isAutoGenerated
                                      ? theme.palette.text.disabled
                                      : undefined,
                              }}
                          >
                              <GridTreeDataGroupingCell {...params} />
                          </div>
                      );
                  },
                  ...groupingColDefProps,
              },
              defaultGroupingExpansionDepth,
              sx: {
                  '& [data-field="__tree_data_group__"] .MuiDataGrid-columnHeaderTitleContainerContent':
                      {
                          flexGrow: 1,
                      },
              },
          }
        : {
              paginationActive: true as true,
              getTreeDataPath,
          };
};
