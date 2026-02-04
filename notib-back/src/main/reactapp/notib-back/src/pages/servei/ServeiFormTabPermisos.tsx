import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormField, MuiDataGrid, useFormContext } from 'reactlib';

const ServeiFormTabPermisosFormContent: React.FC = () => {
    const { t } = useTranslation();
    const enumOptions = [
        {
            value: false,
            description: t('page.procediments.form.permisos.grantedAuthority.user'),
        },
        {
            value: true,
            description: t('page.procediments.form.permisos.grantedAuthority.role'),
        },
    ];
    return (
        <Grid container spacing={2}>
            <Grid size={4}>
                <FormField
                    name="sidGrantedAuthority"
                    label={t('page.procediments.form.permisos.tipus')}
                    type="enum"
                    options={enumOptions}
                    required
                />
            </Grid>
            <Grid size={8}>
                <FormField name="sidName" />
            </Grid>
            <Grid size={12}>
                <FormField
                    name="perm0Allowed"
                    label={t('page.procediments.form.permisos.usuariAllowed')}
                />
            </Grid>
            <Grid size={12}>
                <FormField
                    name="perm2Allowed"
                    label={t('page.procediments.form.permisos.admEntitatAllowed')}
                />
            </Grid>
            <Grid size={12}>
                <FormField
                    name="permXAllowed"
                    label={t('page.procediments.form.permisos.admLecturaAllowed')}
                />
            </Grid>
            <Grid size={12}>
                <FormField
                    name="perm3Allowed"
                    label={t('page.procediments.form.permisos.aplicacioAllowed')}
                />
            </Grid>
        </Grid>
    );
};

const ServeiFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id, apiRef: formApiRef } = useFormContext();
    const sidGrantedAuthorityEnumOptions = [
        {
            value: false,
            description: t('page.procediments.form.permisos.grantedAuthority.user'),
        },
        {
            value: true,
            description: t('page.procediments.form.permisos.grantedAuthority.role'),
        },
    ];
    const columns = React.useMemo(
        () => [
            {
                headerName: t('page.procediments.form.permisos.tipus'),
                field: 'sidGrantedAuthority',
                sortable: false,
                flex: 1,
                valueFormatter: (value: any) =>
                    value
                        ? t('page.procediments.form.permisos.grantedAuthority.role')
                        : t('page.procediments.form.permisos.grantedAuthority.user'),
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
                headerName: t('page.procediments.form.permisos.consultaAllowed'),
                field: 'readAllowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.procediments.form.permisos.procesAllowed'),
                field: 'perm4Allowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.procediments.form.permisos.gestioAllowed'),
                field: 'adminAllowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.procediments.form.permisos.notificacioAllowed'),
                field: 'perm5Allowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.procediments.form.permisos.comunicacioAllowed'),
                field: 'perm8Allowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.procediments.form.permisos.comunicacioSirAllowed'),
                field: 'perm7Allowed',
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
            staticFilter={"resourceName:'procedimentResource' and resourceId:" + id}
            formAdditionalData={{
                sidGrantedAuthority: false,
                resourceName: 'procedimentResource',
                resourceId: id,
            }}
            paginationActive
            //density="standard"
            toolbarHideQuickFilter
            inlineEditActive
            //popupEditActive
            popupEditFormDialogResourceTitle={t('page.procediments.form.resourceNames.permis')}
            popupEditFormContent={<ServeiFormTabPermisosFormContent />}
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default ServeiFormTabPermisos;
