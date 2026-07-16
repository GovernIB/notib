import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import { FormPage, MuiForm, MuiFormTabs, MuiFormTabContent, useFormContext } from 'reactlib';
import PagadorCieFormTabFulles from './PagadorCieFormTabFulles';
import PagadorCieFormTabSobres from './PagadorCieFormTabSobres';
import { useTabParam } from '../../hooks/useSearchParams';
import GridFormField from '../../components/GridFormField';

const PagadorCieFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {

    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    const initialTab = useTabParam();

    React.useEffect(() => {
        setSubtitle(data?.nom);
    }, [data]);

    const fullesTabLabel = (
        <Badge badgeContent={data.fullaCount} color="primary">
            {t('page.pagadorCie.form.tabs.fulles')}
        </Badge>
    );

    const sobresTabLabel = (
        <Badge badgeContent={data.sobreCount} color="primary">
            {t('page.pagadorCie.form.tabs.sobres')}
        </Badge>
    );

    const tabs = [
        t('page.pagadorCie.form.tabs.dades'),
        { label: fullesTabLabel },
        { label: sobresTabLabel },
    ];

    return (
        <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[1, 2]} initialIndex={initialTab}>
            <MuiFormTabContent index={0} showOnCreate>
                <Grid container spacing={2}>
                    <GridFormField size={6} name="nom" />
                    <Grid size={6}></Grid>
                    <GridFormField size={6} name="organGestorEmissor" />
                    <GridFormField size={6} name="organGestorPagador" />
                    <GridFormField size={6} name="apiKey" />
                    <GridFormField size={6} name="contracteDataVig" />
                    <GridFormField size={6} name="cieExtern" />
                </Grid>
            </MuiFormTabContent>
            <MuiFormTabContent index={1}>
                <PagadorCieFormTabFulles />
            </MuiFormTabContent>
            <MuiFormTabContent index={2}>
                <PagadorCieFormTabSobres />
            </MuiFormTabContent>
        </MuiFormTabs>
    );
};

export const PagadorCieForm: React.FC = () => {

    const { t } = useTranslation();
    const { id } = useParams();
    const [subtitle, setSubtitle] = React.useState<string>();
    const isUpdate = id != null;
    return (
        <FormPage>
            <MuiForm
                resourceName="pagadorCieResource"
                id={isUpdate ? Number.parseInt(id) : id}
                title={isUpdate ? t('page.pagadorCie.form.titleUpdate') : t('page.pagadorCie.form.titleCreate')}
                toolbarSubtitle={isUpdate ? subtitle : undefined}
                createLink="./{{id}}"
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}
            >
                <PagadorCieFormContent setSubtitle={setSubtitle} />
            </MuiForm>
        </FormPage>
    );
};

export default PagadorCieForm;
