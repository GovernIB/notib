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
// import ProcedimentFormTabGrups from './ProcedimentFormTabGrups.tsx';
// import ProcedimentFormTabPermisos from './ProcedimentFormTabPermisos';

const ProcedimentFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    React.useEffect(() => {
        setSubtitle(data?.codi + ', ' + data?.nom);
    }, [data]);
    const grupsTabLabel = <Badge badgeContent={data.grupsCount} color="primary">
        {t('page.procediments.form.tabs.grups')}
    </Badge>
    const permisosTabLabel = <Badge badgeContent={data.aclEntryCount} color="primary">
        {t('page.procediments.form.tabs.permisos')}
    </Badge>;
    const tabs = [
        t('page.procediments.form.tabs.dades'),
        { label: grupsTabLabel },
        { label: permisosTabLabel },
    ];
    return <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[1, 2, 4]}>
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
                {/*<MuiFormTabContent index={2}>*/}
                {/*    <ProcedimentFormTabGrups />*/}
                {/*</MuiFormTabContent>*/}
                {/*<MuiFormTabContent index={3}>*/}
                {/*    <ProcedimentFormTabPermisos />*/}
                {/*</MuiFormTabContent>*/}
            </MuiFormTabs>;
}

export const ProcedimentForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const [subtitle, setSubtitle] = React.useState<string>();
    return <FormPage>
        <MuiForm
            resourceName="procedimentResource"
            id={id != null ? parseInt(id) : id}
            title={id != null ? t('page.procediments.form.titleUpdate') : t('page.procediments.form.titleCreate')}
            toolbarSubtitle={id != null ? subtitle : undefined}
            componentProps={{ style: { height: '100%' } }}
            commonFieldComponentProps={{ size: 'small' }}>
            <ProcedimentFormContent setSubtitle={setSubtitle} />
        </MuiForm>
    </FormPage>;
}
export default ProcedimentForm;
