import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    MuiFilter,
    useFilterApiRef,
    useFormContext,
    springFilterBuilder as filterBuilder,
    FilterApi,
} from 'reactlib';
import PermissionGrid from '../../components/PermissionGrid';
import { Grid, Icon, IconButton } from '@mui/material';
import GridFormField from '../../components/GridFormField';

const ContentFilter: React.FC<{ filterApiRef: React.RefObject<FilterApi> }> = (props) => {
    const { filterApiRef } = props;
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
        filterApiRef.current.clear();
    };
    return (
        <Grid container spacing={2}>
            <GridFormField
                size={1.5}
                name="sidGrantedAuthority"
                label={t('component.PermissionGrid.tipus')}
                type="enum"
                options={sidGrantedAuthorityEnumOptions}
            />
            <GridFormField size={4} name="sidName" />
            <GridFormField
                size={1.5}
                name="perm0Allowed"
                label={t('page.entitats.form.permisos.usuariAllowed')}
            />
            <GridFormField
                size={1.5}
                name="perm2Allowed"
                label={t('page.entitats.form.permisos.admEntitatAllowed')}
            />
            <GridFormField
                size={1.5}
                name="permXAllowed"
                label={t('page.entitats.form.permisos.admLecturaAllowed')}
            />
            <GridFormField
                size={1.2}
                name="perm3Allowed"
                label={t('page.entitats.form.permisos.aplicacioAllowed')}
            />
            <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                <Icon>filter_alt_off</Icon>
            </IconButton>
        </Grid>
    );
};

const EntitatGridFilter: React.FC = () => {
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
            componentProps={{ sx: { mb: 2 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            <ContentFilter filterApiRef={filterApiRef} />
        </MuiFilter>
    );
};

const EntitatFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useFormContext();

    const permissionEntries = [
        {
            headerName: t('page.entitats.form.permisos.usuariAllowed'),
            field: 'perm0Allowed',
        },
        {
            headerName: t('page.entitats.form.permisos.admEntitatAllowed'),
            field: 'perm2Allowed',
        },
        {
            headerName: t('page.entitats.form.permisos.admLecturaAllowed'),
            field: 'permXAllowed',
        },
        {
            headerName: t('page.entitats.form.permisos.aplicacioAllowed'),
            field: 'perm3Allowed',
        },
    ];

    return (
        <PermissionGrid
            resourceName="entitatResource"
            id={id}
            permissionEntries={permissionEntries}
            toolbarAdditionalRow={<EntitatGridFilter />}
        />
    );
};

export default EntitatFormTabPermisos;
