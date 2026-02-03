import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import { FormPage, MuiForm, MuiFormTabs, MuiFormTabContent, useFormContext } from 'reactlib';
import PagadorPostalTabPermisos from './PagadorPostalTabPermisos.tsx';
import GridFormField from '../../components/GridFormField';

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
                    <GridFormField size={4} name="codi" />
                    <GridFormField size={8} name="nom" />
                    <GridFormField size={4} name="estat" />
                    <GridFormField size={6} name="llibre" />
                    <GridFormField size={6} name="oficina " />
                    <GridFormField size={3} name="activa" />
                    <GridFormField size={3} name="permetreSir" />
                    <GridFormField size={3} name="cieOrgan" />
                    <GridFormField size={3} name="desactivarCie" />
                </Grid>
            </MuiFormTabContent>
            <MuiFormTabContent index={1}>
                <PagadorPostalTabPermisos />
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
