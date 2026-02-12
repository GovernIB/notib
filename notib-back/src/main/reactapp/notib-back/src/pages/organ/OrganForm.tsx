import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import {
    FormPage,
    FormField,
    MuiForm,
    MuiFormTabs,
    MuiFormTabContent,
    useFormContext,
} from 'reactlib';
import OrganFormTabPermisos from './OrganFormTabPermisos';

const OrganFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    React.useEffect(() => {
        setSubtitle(data?.codi + ', ' + data?.nom);
    }, [data]);
    const permisosTabLabel = (
        <Badge badgeContent={data.aclEntryCount} color="primary">
            {t('page.organs.form.tabs.permisos')}
        </Badge>
    );
    const tabs = [t('page.entitats.form.tabs.dades'), { label: permisosTabLabel }];
    return (
        <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[1]}>
            <MuiFormTabContent index={0} showOnCreate>
                <Grid container spacing={2}>
                    <Grid size={4}>
                        <FormField size={4} name="codi" />
                    </Grid>
                    <Grid size={8}>
                        <FormField size={4} name="nom" />
                    </Grid>
                    <Grid size={4}>
                        <FormField size={4} name="estat" />
                    </Grid>
                    <Grid size={6}>
                        <FormField size={4} name="llibre" />
                    </Grid>
                    <Grid size={6}>
                        <FormField size={4} name="oficina" />
                    </Grid>
                    <Grid size={3}>
                        <FormField size={4} name="activa" />
                    </Grid>
                    <Grid size={3}>
                        <FormField size={4} name="permetreSir" />
                    </Grid>
                    <Grid size={3}>
                        <FormField size={4} name="cieOrgan" />
                    </Grid>
                    <Grid size={3}>
                        <FormField size={4} name="desactivarCie" />
                    </Grid>
                </Grid>
            </MuiFormTabContent>
            <MuiFormTabContent index={1}>
                <OrganFormTabPermisos />
            </MuiFormTabContent>
        </MuiFormTabs>
    );
};

export const OrganForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const [subtitle, setSubtitle] = React.useState<string>();
    return (
        <FormPage>
            <MuiForm
                resourceName="organGestorResource"
                id={id != null ? parseInt(id) : id}
                title={
                    id != null
                        ? t('page.organs.form.titleUpdate')
                        : t('page.organs.form.titleCreate')
                }
                toolbarSubtitle={id != null ? subtitle : undefined}
                createLink="./{{id}}"
                //updateLink="../../"
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}>
                <OrganFormContent setSubtitle={setSubtitle} />
            </MuiForm>
        </FormPage>
    );
};

export default OrganForm;
