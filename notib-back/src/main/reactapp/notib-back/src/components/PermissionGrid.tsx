import React from 'react';
import { useTranslation } from 'react-i18next';
import { FormField, MuiDataGrid, useFormContext } from 'reactlib';

export type PermissionGridEntry = {
    headerName: string;
    field: string;
};

const PermissionGrid: React.FC<{
    resourceName: string;
    id: any;
    permissionEntries: PermissionGridEntry[];
}> = (props) => {
    const { resourceName, id, permissionEntries } = props;
    const { t } = useTranslation();
    const { apiRef: formApiRef } = useFormContext();
    const sidGrantedAuthorityEnumOptions = [
        {
            value: false,
            description: t('component.PermissionGrid.grantedAuthority.user'),
        },
        {
            value: true,
            description: t('component.PermissionGrid.grantedAuthority.role'),
        },
    ];
    const columns: any[] = React.useMemo(() => {
        const columns = [];
        columns.push(
            {
                headerName: t('component.PermissionGrid.tipus'),
                field: 'sidGrantedAuthority',
                sortable: false,
                flex: 1,
                valueFormatter: (value: any) =>
                    value
                        ? t('component.PermissionGrid.grantedAuthority.role')
                        : t('component.PermissionGrid.grantedAuthority.user'),
                renderEditCell: (params: any) => {
                    return (
                        <FormField
                            name={params.field}
                            label=""
                            type="enum"
                            options={sidGrantedAuthorityEnumOptions}
                            required
                            inline
                        />
                    );
                },
            },
            {
                field: 'sidName',
                sortable: false,
                flex: 4,
                renderEditCell: (params: any) => {
                    return (
                        <FormField
                            name={params.field}
                            label=""
                            required
                            inline
                            readOnly={!params.id.startsWith('###')}
                        />
                    );
                },
            }
        );
        columns.push(
            ...permissionEntries.map((e) => ({
                headerName: e.headerName,
                field: e.field,
                sortable: false,
                flex: 1,
            }))
        );
        return columns;
    }, [t, permissionEntries]);
    const handleDataGridRowChanges = () => {
        formApiRef.current?.refresh();
    };
    return (
        <MuiDataGrid
            title=""
            resourceName="aclEntryResource"
            columns={columns}
            staticFilter={"resourceName:'" + resourceName + "' and resourceId:" + id}
            formAdditionalData={{
                sidGrantedAuthority: false,
                resourceName,
                resourceId: id,
            }}
            paginationActive
            //density="standard"
            toolbarHideQuickFilter
            inlineEditActive
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default PermissionGrid;
