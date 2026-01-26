import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormField, MuiDataGrid, useFormContext } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const OrganFormTabPermisosFormContent: React.FC = () => {
    const { t } = useTranslation();
    const enumOptions = [
        {
            value: false,
            description: t('page.organs.form.permisos.grantedAuthority.user'),
        },
        {
            value: true,
            description: t('page.organs.form.permisos.grantedAuthority.role'),
        },
    ];

    return (
        <Grid container spacing={2}>
            <GridFormField
                size={4}
                name="sidGrantedAuthority"
                label={t('page.organs.form.permisos.tipus')}
                type="enum"
                options={enumOptions}
                required
            />
            <GridFormField size={8} name="sidName" />
            <GridFormField
                size={12}
                name="perm0Allowed"
                label={t('page.organs.form.permisos.administrador')}
            />
            <GridFormField
                size={12}
                name="perm2Allowed"
                label={t('page.organs.form.permisos.consulta')}
            />
            <GridFormField
                size={12}
                name="permXAllowed"
                label={t('page.organs.form.permisos.processar')}
            />
            <GridFormField
                size={12}
                name="perm3Allowed"
                label={t('page.organs.form.permisos.gestio')}
            />
            <GridFormField
                size={12}
                name="perm3Allowed"
                label={t('page.organs.form.permisos.comuns')}
            />
            <GridFormField
                size={12}
                name="perm3Allowed"
                label={t('page.organs.form.permisos.notificacions')}
            />
            <GridFormField
                size={12}
                name="perm3Allowed"
                label={t('page.organs.form.permisos.comunicacions')}
            />
            <GridFormField
                size={12}
                name="perm3Allowed"
                label={t('page.organs.form.permisos.sir')}
            />
            <GridFormField
                size={12}
                name="perm3Allowed"
                label={t('page.organs.form.permisos.comSenseProc')}
            />
        </Grid>
    );
};

const OrganFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id, apiRef: formApiRef } = useFormContext();
    const sidGrantedAuthorityEnumOptions = [
        {
            value: false,
            description: t('page.organs.form.permisos.grantedAuthority.user'),
        },
        {
            value: true,
            description: t('page.organs.form.permisos.grantedAuthority.role'),
        },
    ];

    const columns = React.useMemo(
        () => [
            {
                headerName: t('page.organs.form.permisos.tipus'),
                field: 'sidGrantedAuthority',
                sortable: false,
                flex: 1,
                valueFormatter: (value: any) =>
                    value
                        ? t('page.organs.form.permisos.grantedAuthority.role')
                        : t('page.organs.form.permisos.grantedAuthority.user'),
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
                flex: 2,
            },
            {
                headerName: t('page.organs.form.permisos.administrador'),
                field: 'perm0Allowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.organs.form.permisos.consulta'),
                field: 'perm2Allowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.organs.form.permisos.processar'),
                field: 'permXAllowed',
                sortable: false,
                flex: 1,
            },
            {
                headerName: t('page.organs.form.permisos.gestio'),
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
            staticFilter={"resourceName:'organGestorResource' and resourceId:" + id}
            formAdditionalData={{
                sidGrantedAuthority: false,
                resourceName: 'organGestorResource',
                resourceId: id,
            }}
            paginationActive
            //density="standard"
            toolbarHideQuickFilter
            inlineEditActive
            //popupEditActive
            popupEditFormDialogResourceTitle={t('page.organs.form.resourceNames.permis')}
            popupEditFormContent={<OrganFormTabPermisosFormContent />}
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default OrganFormTabPermisos;
