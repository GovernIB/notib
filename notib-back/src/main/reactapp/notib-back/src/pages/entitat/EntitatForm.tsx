import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import {
    FormPage,
    MuiForm,
    FormField,
    MuiFormTabs,
    MuiFormTabContent,
    useFormContext,
} from 'reactlib';
import EntitatFormTabTipusDocs from './EntitatFormTabTipusDocs';
import EntitatFormTabAplicacions from './EntitatFormTabAplicacions';
import EntitatFormTabPermisos from './EntitatFormTabPermisos';

const EntitatFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    React.useEffect(() => {
        setSubtitle(data?.codi + ', ' + data?.nom);
    }, [data]);
    const tipusDocsTabLabel = <Badge badgeContent={data.aplicacioCount} color="primary">
        {t('page.entitats.form.tabs.tipusDocs')}
    </Badge>
    const aplicacionsTabLabel = <Badge badgeContent={data.aplicacioCount} color="primary">
        {t('page.entitats.form.tabs.aplicacions')}
    </Badge>;
    const permisosTabLabel = <Badge badgeContent={data.aclEntryCount} color="primary">
        {t('page.entitats.form.tabs.permisos')}
    </Badge>;
    const tabs = [
        t('page.entitats.form.tabs.dades'),
        t('page.entitats.form.tabs.personalitzar'),
        { label: tipusDocsTabLabel },
        { label: aplicacionsTabLabel },
        { label: permisosTabLabel },
    ];
    return <MuiFormTabs
        tabs={tabs}
        tabIndexesWithGrids={[2, 3, 4]}>
        <MuiFormTabContent index={0} showOnCreate>
            <Grid container spacing={2}>
                <Grid size={4}><FormField name="codi" /></Grid><Grid size={8} />
                <Grid size={4}><FormField name="tipus" /></Grid>
                <Grid size={8}><FormField name="nom" /></Grid>
                <Grid size={6}><FormField name="dir3Codi" /></Grid>
                <Grid size={6}><FormField name="dir3CodiReg" /></Grid>
                <Grid size={3}><FormField name="activa" /></Grid>
                <Grid size={3}><FormField name="ambEntregaDeh" /></Grid>
                <Grid size={3}><FormField name="llibreEntitat" /></Grid>
                <Grid size={3}><FormField name="oficinaEntitat" /></Grid>
                <Grid size={12}><FormField name="apiKey" /></Grid>
                <Grid size={12}><FormField name="descripcio" type="textarea" /></Grid>
            </Grid>
        </MuiFormTabContent>
        <MuiFormTabContent index={1}>
            <Grid container spacing={2}>
                <Grid size={6}><FormField name="colorLletra" type="color" /></Grid>
                <Grid size={6}><FormField name="colorFons" type="color" /></Grid>
            </Grid>
        </MuiFormTabContent>
        <MuiFormTabContent index={2}>
            <EntitatFormTabTipusDocs />
        </MuiFormTabContent>
        <MuiFormTabContent index={3}>
            <EntitatFormTabAplicacions />
        </MuiFormTabContent>
        <MuiFormTabContent index={4}>
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
            resourceName="entitatResource"
            id={id != null ? parseInt(id) : id}
            title={id != null ? t('page.entitats.form.titleUpdate') : t('page.entitats.form.titleCreate')}
            toolbarSubtitle={subtitle}
            componentProps={{ style: { height: '100%' } }}
            commonFieldComponentProps={{ size: 'small' }}>
            <EntitatFormContent setSubtitle={setSubtitle} />
        </MuiForm>
    </FormPage>;
}
export default EntitatForm;
