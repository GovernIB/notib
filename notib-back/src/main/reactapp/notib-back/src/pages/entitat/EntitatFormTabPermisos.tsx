import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    MuiFilter,
    useFilterApiRef,
    useFormContext,
    springFilterBuilder as filterBuilder,
    FilterApiRef,
    MuiDataGridApiRef,
    useMuiDataGridApiRef,
} from 'reactlib';
import PermissionGrid from '../../components/PermissionGrid';
import { Box, Grid, Icon, IconButton } from '@mui/material';
import GridFormField from '../../components/GridFormField';
import GridToolbarButton from '../../components/GridToolbarButton';
import PermissionGridSwitch from '../../components/PermissionGridSwitch';

const PermissionForm: React.FC = () => {

    const { t } = useTranslation();
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
    return (
        <Grid container spacing={2}>
            <GridFormField
                name="sidGrantedAuthority"
                type="enum"
                label={t('component.PermissionGrid.tipus')}
                options={sidGrantedAuthorityEnumOptions}
                required
                size={3}
            />
            <GridFormField size={9} name="sidName" />
            <PermissionGridSwitch name="perm0Allowed" label={t('page.entitats.form.permisos.usuariAllowed')} size={12}/>
            <PermissionGridSwitch name="perm2Allowed" label={t('page.entitats.form.permisos.admEntitatAllowed')} size={12}/>
            <PermissionGridSwitch name="permXAllowed" label={t('page.entitats.form.permisos.admLecturaAllowed')} size={12}/>
            <PermissionGridSwitch name="perm3Allowed" label={t('page.entitats.form.permisos.aplicacioAllowed')} size={12}/>
        </Grid>
    );
};

const ContentFilter: React.FC<{
    filterApiRef: FilterApiRef;
    gridApiRef: MuiDataGridApiRef;
}> = (props) => {
    const { filterApiRef, gridApiRef } = props;
    const { t } = useTranslation();
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
    const handleButtonClick = () => {
        filterApiRef.current?.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField
                size={1}
                name="sidGrantedAuthority"
                label={t('component.PermissionGrid.tipus')}
                type="enum"
                options={sidGrantedAuthorityEnumOptions}
            />
            <GridFormField size={5} name="sidName" />
            <GridFormField size={1} name="perm0Allowed" label={t('page.entitats.form.permisos.usuariAllowed')}/>
            <GridFormField size={1.2} name="perm2Allowed" label={t('page.entitats.form.permisos.admEntitatAllowed')}/>
            <GridFormField size={1.2} name="permXAllowed" label={t('page.entitats.form.permisos.admLecturaAllowed')}/>
            <GridFormField size={1.2} name="perm3Allowed" label={t('page.entitats.form.permisos.aplicacioAllowed')}/>
            <Grid size={1.4}>
                <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                    <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}><Icon>filter_alt_off</Icon></IconButton>
                    <GridToolbarButton gridApiRef={gridApiRef} />
                </Box>
            </Grid>
        </Grid>
    );
};

const EntitatGridFilter: React.FC<{ gridApiRef: MuiDataGridApiRef }> = (props) => {

    const { gridApiRef } = props;
    const filterApiRef = useFilterApiRef();
    // TODO: Falta aclarar com es pot filtrar de forma correcte a la bbdd,
    // ja que actualment el filtre s'envia al backend però no s'aplica correctament.
    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
            filterBuilder.like('sidName', data.sidName),
            filterBuilder.eq('sidGrantedAuthority', `'${data?.sidGrantedAuthority}'`),
            filterBuilder.eq('perm0Allowed', `'${data?.perm0Allowed}`),
            filterBuilder.eq('perm2Allowed', `'${data?.perm2Allowed}`),
            filterBuilder.eq('permXAllowed', `'${data?.permXAllowed}`),
            filterBuilder.eq('perm3Allowed', `'${data?.perm3Allowed}`)
        );
    };
    return (
        <MuiFilter
            resourceName="aclEntryResource"
            code="FILTER_PERMISOS"
            apiRef={filterApiRef}
            springFilterBuilder={springFilterBuilder}
            componentProps={{ sx: { mb: 2, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} gridApiRef={gridApiRef} />
        </MuiFilter>
    );
};

const EntitatFormTabPermisos: React.FC = () => {

    const { t } = useTranslation();
    const { id } = useFormContext();
    const gridApiRef = useMuiDataGridApiRef();
    const permissionEntries = [
        {
            headerName: t('page.entitats.form.permisos.usuariAllowed'),
            field: 'perm0Allowed',
            type: 'boolean',
        },
        {
            headerName: t('page.entitats.form.permisos.admEntitatAllowed'),
            field: 'perm2Allowed',
            type: 'boolean',
        },
        {
            headerName: t('page.entitats.form.permisos.admLecturaAllowed'),
            field: 'permXAllowed',
            type: 'boolean',
        },
        {
            headerName: t('page.entitats.form.permisos.aplicacioAllowed'),
            field: 'perm3Allowed',
            type: 'boolean',
        },
    ];
    return (
        <PermissionGrid
            apiRef={gridApiRef}
            resourceName="entitatResource"
            id={id}
            permissionEntries={permissionEntries}
            permissionForm={<PermissionForm />}
            toolbarHide
            toolbarAdditionalRow={<EntitatGridFilter gridApiRef={gridApiRef} />}
        />
    );
};

export default EntitatFormTabPermisos;
