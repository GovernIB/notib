import React from 'react';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { useTheme } from '@mui/material/styles';
import {
    GridTreeDataGroupingCell,
    useGridApiRef,
    gridRowNodeSelector,
    type DataGridProProps,
    type GridEventListener,
} from '@mui/x-data-grid-pro';
import { useNotibContext } from '../components/NotibContext';
import StyledMuiFilter from '../components/StyledMuiFilter';
import FilterCount from '../components/FilterCount';

export const useDatagridPageSizeOptionsProps = (
    innerScroll: boolean = true,
    autoHeightMinHeight?: string
) => {
    const { currentUser, currentUserGridPageSizeOptions } = useNotibContext();
    const [autoPageSize, setAutoPageSize] = React.useState<boolean>();
    const handlePersistentStateChange = (state: any) => {
        !innerScroll && setAutoPageSize(state.autoPageSize);
    };
    return {
        defaultPaginationModel: {
            page: 0,
            pageSize: currentUser.numElementsPaginaDefecteAsInt ?? -1,
        },
        pageSizeOptions: currentUserGridPageSizeOptions,
        onPersistentStateChange: handlePersistentStateChange,
        ...(!innerScroll &&
            !autoPageSize && {
                autoHeight: true,
                sx: { minHeight: autoHeightMinHeight ?? '400px' },
            }),
    };
};

export const useDatagridFilterProps = (
    resourceName: string,
    code: string,
    springFilterBuilder: (data: any) => string | undefined,
    /**
     * Només els camps del filtre (GridFormField): el StyledMuiFilter pinta el layout Grid i el bloc de botons.
     * Han d'ocupar una sola fila, botons inclosos: sumar `12 - FILTER_BUTTONS_COLUMNS.md` a `md` (11,5) o,
     * amb `advancedSearch`, `12 - FILTER_BUTTONS_COLUMNS_ADVANCED.md` (10,5) al filtre bàsic.
     */
    content: React.ReactElement,
    options?: {
        /** Amb botó per commutar entre el filtre bàsic i l'avançat (`useFormContext().data.advanced`). */
        advancedSearch?: boolean;
        /** Dades amb què s'inicialitza el filtre (també són les que recupera el botó «Neteja»). */
        initialData?: any;
        /** Alçada mínima de la fila del filtre mentre es carrega, perquè la graella no salti. */
        minHeight?: string;
        /** Contingut sota la fila del filtre (p. ex. una llegenda). */
        footer?: React.ReactNode;
        /** Clau amb què es desa el filtre (per defecte, el nom del recurs): cal quan diverses vistes comparteixen recurs. */
        persistentStateKey?: string;
    }
) => {
    const { advancedSearch, initialData, minHeight = '67px', footer, persistentStateKey } = options ?? {};
    const [autoFindDisabled, setAutoFindDisabled] = React.useState<boolean>(true);
    // Últim Spring Filter aplicat, per al recompte de filtres de la barra de títol (FilterCount)
    const [springFilter, setSpringFilter] = React.useState<string | undefined>();
    const handleSpringFilterChange = (springFilter: string | undefined) => {
        const springFilterEmpty = springFilter == null || springFilter === '';
        autoFindDisabled && setAutoFindDisabled(!springFilterEmpty);
        setSpringFilter(springFilter);
    };
    const filterComponent = (
        <StyledMuiFilter
            resourceName={resourceName}
            code={code}
            persistentStateActive
            persistentStateKey={persistentStateKey}
            springFilterBuilder={springFilterBuilder}
            onSpringFilterChange={handleSpringFilterChange}
            // Sense cap filtre inicial no s'aplica res: la graella ha de poder fer la primera cerca
            onEmptyInitialData={() => setAutoFindDisabled(false)}
            componentProps={{ sx: { mb: 0, mt: 0 } }}
            initialData={initialData}
            advancedSearch={advancedSearch}
            footer={footer}
        >
            {content}
            <FilterCount springFilter={springFilter} />
        </StyledMuiFilter>
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
    reorderingActive: boolean,
    defaultGroupingExpansionDepth?: number,
    groupingColDefProps?: any
) => {
    const { t } = useTranslation();
    const theme = useTheme();
    const datagridApiRef = useGridApiRef();
    const changeAllNodesExpansion = (expanded: boolean) => {
        const rowIds = Array.from(datagridApiRef.current?.getRowModels().keys() ?? []);
        rowIds?.forEach((id) => {
            const node = gridRowNodeSelector(datagridApiRef, id) as any;
            if (node?.children?.length) {
                if (expanded) {
                    datagridApiRef.current?.setRowChildrenExpansion(id, expanded);
                } else if (
                    defaultGroupingExpansionDepth == null ||
                    node.depth >= defaultGroupingExpansionDepth
                ) {
                    datagridApiRef.current?.setRowChildrenExpansion(id, expanded);
                }
            }
        });
    };
    const getTreeDataPath: DataGridProProps['getTreeDataPath'] = (row: any) => {
        return row.path?.map((p: any) => p.description) ?? [row.id];
    };
    const setTreeDataPath: DataGridProProps['setTreeDataPath'] = (path, row) => {
        return {
            ...row,
            path: path.map((p) => ({ id: -1, description: p })),
        };
    };
    const processRowUpdate: DataGridProProps['processRowUpdate'] = (newRow: any, oldRow: any) => {
        console.log('>>> reordenació 1', oldRow.id, oldRow.path, newRow.id, newRow.path);
    };
    const onRowOrderChange: GridEventListener<'rowOrderChange'> = (params) => {
        if (params.oldParent === params.newParent) {
            console.log('>>> reordenació 2', params);
        }
    };
    const reorderingProps = reorderingActive
        ? {
              rowReordering: true,
              setTreeDataPath,
              processRowUpdate,
              onRowOrderChange,
          }
        : {};
    return active
        ? {
              perspectives: ['TREE'],
              treeData: true as true,
              getTreeDataPath,
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
              ...reorderingProps,
              sx: {
                  '& [data-field="__tree_data_group__"] .MuiDataGrid-columnHeaderTitleContainerContent':
                      {
                          flexGrow: 1,
                      },
              },
          }
        : {
              paginationActive: true as true,
          };
};
