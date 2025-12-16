import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import {
    GridPage,
    FormPage,
    MuiDataGrid,
    MuiForm,
    FormField,
    MuiFormTabs,
    MuiFormTabContent,
    useFormContext,
} from 'reactlib';

const EntitatFormTabAplicacionsFormContent: React.FC = () => {
    return <Grid container spacing={2}>
        <Grid size={4}><FormField name="usuariCodi" /></Grid><Grid size={8} />
        <Grid size={12}><FormField name="callbackUrl" /></Grid>
        <Grid size={6}><FormField name="activa" /></Grid>
        <Grid size={6}><FormField name="headerCsrf" /></Grid>
        <Grid size={6}><FormField name="horariLaboralInici" /></Grid>
        <Grid size={6}><FormField name="horariLaboralFi" /></Grid>
        <Grid size={6}><FormField name="maxEnviamentsMinutLaboral" /></Grid>
        <Grid size={6}><FormField name="maxEnviamentsMinutNoLaboral" /></Grid>
        <Grid size={6}><FormField name="maxEnviamentsDiaLaboral" /></Grid>
        <Grid size={6}><FormField name="maxEnviamentsDiaNoLaboral" /></Grid>
    </Grid>;
}

const EntitatFormTabAplicacions: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useFormContext();
    const columns = React.useMemo(() => [{
        field: 'usuariCodi',
        sortable: false,
        flex: 1
    }, {
        field: 'callbackUrl',
        sortable: false,
        flex: 4
    }, {
        field: 'activa',
        sortable: false,
        flex: 1
    }], []);
    return <MuiDataGrid
        title=""
        resourceName="aplicacioResource"
        staticFilter={"entitat.id:" + id}
        columns={columns}
        paginationActive
        toolbarHideQuickFilter
        popupEditActive
        popupEditFormDialogResourceTitle={t('page.entitats.form.resourceNames.aplicacio')}
        popupEditFormContent={<EntitatFormTabAplicacionsFormContent />} />;
}

const EntitatFormTabPermisosFormContent: React.FC = () => {
    const { t } = useTranslation();
    const enumOptions = [{
        value: false,
        description: t('page.entitats.form.permisos.grantedAuthority.user')
    }, {
        value: true,
        description: t('page.entitats.form.permisos.grantedAuthority.role')
    }];
    return <Grid container spacing={2}>
        <Grid size={4}><FormField
            name="sidGrantedAuthority"
            label={t('page.entitats.form.permisos.tipus')}
            type="enum"
            options={enumOptions}
            required />
        </Grid>
        <Grid size={8}><FormField name="sidName" /></Grid>
        <Grid size={12}><FormField name="perm0Allowed" label={t('page.entitats.form.permisos.usuariAllowed')} /></Grid>
        <Grid size={12}><FormField name="perm2Allowed" label={t('page.entitats.form.permisos.admEntitatAllowed')} /></Grid>
        <Grid size={12}><FormField name="permXAllowed" label={t('page.entitats.form.permisos.admLecturaAllowed')} /></Grid>
        <Grid size={12}><FormField name="perm3Allowed" label={t('page.entitats.form.permisos.aplicacioAllowed')} /></Grid>
    </Grid>;
}

const EntitatFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useFormContext();
    const columns = React.useMemo(() => [{
        headerName: t('page.entitats.form.permisos.tipus'),
        field: 'sidGrantedAuthority',
        sortable: false,
        flex: 1,
        valueFormatter: (value: any) => value ?
            t('page.entitats.form.permisos.grantedAuthority.role') :
            t('page.entitats.form.permisos.grantedAuthority.user'),
    }, {
        field: 'sidName',
        sortable: false,
        flex: 4
    }, {
        headerName: t('page.entitats.form.permisos.usuariAllowed'),
        field: 'perm0Allowed',
        sortable: false,
        flex: 1
    }, {
        headerName: t('page.entitats.form.permisos.admEntitatAllowed'),
        field: 'perm2Allowed',
        sortable: false,
        flex: 1
    }, {
        headerName: t('page.entitats.form.permisos.admLecturaAllowed'),
        field: 'permXAllowed',
        sortable: false,
        flex: 1
    }, {
        headerName: t('page.entitats.form.permisos.aplicacioAllowed'),
        field: 'perm3Allowed',
        sortable: false,
        flex: 1
    }], [t]);
    return <MuiDataGrid
        title=""
        resourceName="aclEntryResource"
        columns={columns}
        staticFilter={"resourceName:'entitatResource' and resourceId:" + id}
        formAdditionalData={{ resourceName: 'entitatResource', resourceId: id }}
        paginationActive
        toolbarHideQuickFilter
        popupEditActive
        popupEditFormDialogResourceTitle={t('page.entitats.form.resourceNames.permis')}
        popupEditFormContent={<EntitatFormTabPermisosFormContent />} />;
}

const EntitatFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    React.useEffect(() => {
        setSubtitle(data?.codi + ', ' + data?.nom);
    }, [data]);
    const aplicacionsTabLabel = <Badge badgeContent={data.aplicacioCount} color="primary">
        {t('page.entitats.form.tabs.aplicacions')}
    </Badge>;
    const permisosTabLabel = <Badge badgeContent={data.aclEntryCount} color="primary">
        {t('page.entitats.form.tabs.permisos')}
    </Badge>;
    const tabs = [
        t('page.entitats.form.tabs.dades'),
        t('page.entitats.form.tabs.personalitzar'),
        { label: aplicacionsTabLabel },
        { label: permisosTabLabel },
    ];
    return <MuiFormTabs
        tabs={tabs}
        tabIndexesWithGrids={[2, 3]}>
        <MuiFormTabContent index={0} showOnCreate>
            <Grid container spacing={2}>
                <Grid size={12}><FormField name="codi" /></Grid>
                <Grid size={12}><FormField name="nom" /></Grid>
                <Grid size={12}><FormField name="dir3Codi" /></Grid>
                <Grid size={12}><FormField name="activa" /></Grid>
            </Grid>
        </MuiFormTabContent>
        <MuiFormTabContent index={1}>
            <Grid container spacing={2}>
                <Grid size={12}><FormField name="colorLletra" type="color" /></Grid>
                <Grid size={12}><FormField name="colorFons" type="color" /></Grid>
            </Grid>
        </MuiFormTabContent>
        <MuiFormTabContent index={2}>
            <EntitatFormTabAplicacions />
        </MuiFormTabContent>
        <MuiFormTabContent index={3}>
            <EntitatFormTabPermisos />
        </MuiFormTabContent>
    </MuiFormTabs>;
}

export const EntitatForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const [subtitle, setSubtitle] = React.useState<string>();
    return <FormPage>
        <MuiForm
            componentProps={{ style: { height: '100%' } }}
            id={id != null ? parseInt(id) : id}
            title={id != null ? t('page.entitats.form.titleUpdate') : t('page.entitats.form.titleCreate')}
            toolbarSubtitle={subtitle}
            resourceName="entitatResource">
            <EntitatFormContent setSubtitle={setSubtitle} />
        </MuiForm>
    </FormPage>;
}

const Entitats = () => {
    const { t } = useTranslation();
    const columns = [{
        field: 'codi',
        flex: 1,
    }, {
        field: 'nom',
        flex: 4,
    }, {
        field: 'dir3Codi',
        flex: 1,
    },  {
        field: 'activa',
        flex: .6,
    }];
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.entitats.grid.title')}
                resourceName="entitatResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}" />
        </GridPage>
    );
};

export default Entitats;
