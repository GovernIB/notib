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
import PagadorCieFormTabFulles from './PagadorCieFormTabFulles';

const PagadorCieFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    React.useEffect(() => {
        setSubtitle(data?.nom);
    }, [data]);
    const fullesTabLabel = (
        <Badge badgeContent={data.aclEntryCount} color="primary">
            {t('page.pagadorCie.form.tabs.fulles')}
        </Badge>
    );
    const sobresTabLabel = (
        <Badge badgeContent={data.aclEntryCount} color="primary">
            {t('page.pagadorCie.form.tabs.sobres')}
        </Badge>
    );
    const tabs = [
        t('page.pagadorCie.form.tabs.dades'),
        { label: fullesTabLabel },
        { label: sobresTabLabel },
    ];
    return (
        <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[1]}>
            <MuiFormTabContent index={0} showOnCreate>
                <Grid container spacing={2}>
                    <Grid size={6}>
                        <FormField name="nom" />
                    </Grid>
                    <Grid size={6}></Grid>
                    <Grid size={6}>
                        <FormField name="organGestorEmissor" />
                    </Grid>
                    <Grid size={6}>
                        <FormField name="organGestorPagador" />
                    </Grid>
                    <Grid size={6}>
                        <FormField name="apiKey" />
                    </Grid>
                    <Grid size={6}>
                        <FormField name="contracteDataVig" />
                    </Grid>
                    <Grid size={6}>
                        <FormField name="cieExtern" />
                    </Grid>
                </Grid>
            </MuiFormTabContent>
            <MuiFormTabContent index={1}>
                <PagadorCieFormTabFulles />
            </MuiFormTabContent>
            <MuiFormTabContent index={2}>Sobres</MuiFormTabContent>
        </MuiFormTabs>
    );
};

export const PagadorCieForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const [subtitle, setSubtitle] = React.useState<string>();
    return (
        <FormPage>
            <MuiForm
                resourceName="pagadorCieResource"
                id={id != null ? parseInt(id) : id}
                title={
                    id != null
                        ? t('page.pagadorCie.form.titleUpdate')
                        : t('page.pagadorCie.form.titleCreate')
                }
                toolbarSubtitle={id != null ? subtitle : undefined}
                createLink="./{{id}}"
                //updateLink="../../"
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}>
                <PagadorCieFormContent setSubtitle={setSubtitle} />
            </MuiForm>
        </FormPage>
    );
};

export default PagadorCieForm;
