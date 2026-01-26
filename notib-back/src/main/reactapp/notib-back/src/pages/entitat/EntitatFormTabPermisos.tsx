import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormField, MuiDataGrid, useFormContext } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const EntitatFormTabPermisosFormContent: React.FC = () => {
    const { t } = useTranslation();
    const enumOptions = [
        {
            value: false,
            description: t('page.entitats.form.permisos.grantedAuthority.user'),
        },
        {
            value: true,
            description: t('page.entitats.form.permisos.grantedAuthority.role'),
        },
    ];
    return (
        <Grid container spacing={2}>
            <GridFormField
                size={4}
                name="sidGrantedAuthority"
                label={t('page.entitats.form.permisos.tipus')}
                type="enum"
                options={enumOptions}
                required
            />
            <GridFormField size={8} name="sidName" />
            <GridFormField
                size={12}
                name="perm0Allowed"
                label={t('page.entitats.form.permisos.usuariAllowed')}
            />
            <GridFormField
                size={12}
                name="perm2Allowed"
                label={t('page.entitats.form.permisos.admEntitatAllowed')}
            />
            <GridFormField
                size={12}
                name="permXAllowed"
                label={t('page.entitats.form.permisos.admLecturaAllowed')}
            />
            <GridFormField
                size={12}
                name="perm3Allowed"
                label={t('page.entitats.form.permisos.aplicacioAllowed')}
            />
        </Grid>
    );
};

const EntitatFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id, apiRef: formApiRef } = useFormContext();
    const sidGrantedAuthorityEnumOptions = [
        {
            value: false,
            description: t('page.entitats.form.permisos.grantedAuthority.user'),
        },
        {
            value: true,
            description: t('page.entitats.form.permisos.grantedAuthority.role'),
        },
    ];
    const columns = React.useMemo(
        () => [
            {
                headerName: t('page.entitats.form.permisos.tipus'),
                field: 'sidGrantedAuthority',
                sortable: false,
                flex: 1,
                valueFormatter: (value: any) =>
                    value
                        ? t('page.entitats.form.permisos.grantedAuthority.role')
                        : t('page.entitats.form.permisos.grantedAuthority.user'),
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
            },
            {
                headerName: t('page.entitats.form.permisos.usuariAllowed'),
                field: 'perm0Allowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.entitats.form.permisos.admEntitatAllowed'),
                field: 'perm2Allowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.entitats.form.permisos.admLecturaAllowed'),
                field: 'permXAllowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.entitats.form.permisos.aplicacioAllowed'),
                field: 'perm3Allowed',
                sortable: false,
                flex: 1,
            },
        ],
        [t]
    );
    const handleDataGridRowChanges = () => {
        formApiRef.current?.refresh();
    };
    return (
        <MuiDataGrid
            title=""
            resourceName="aclEntryResource"
            columns={columns}
            staticFilter={"resourceName:'entitatResource' and resourceId:" + id}
            formAdditionalData={{
                sidGrantedAuthority: false,
                resourceName: 'entitatResource',
                resourceId: id,
            }}
            paginationActive
            //density="standard"
            toolbarHideQuickFilter
            inlineEditActive
            //popupEditActive
            popupEditFormDialogResourceTitle={t('page.entitats.form.resourceNames.permis')}
            popupEditFormContent={<EntitatFormTabPermisosFormContent />}
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default EntitatFormTabPermisos;
