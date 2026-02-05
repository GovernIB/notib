import React from 'react';
import {useParams} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import Grid from '@mui/material/Grid';
import {FormPage, MuiForm, MuiFormTabs, MuiFormTabContent, useFormContext} from 'reactlib';
import GridFormField from '../../components/GridFormField';

const PagadorPostalFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const {setSubtitle} = props;
    const {t} = useTranslation();
    const {data} = useFormContext();

    React.useEffect(() => {
        setSubtitle(data?.nom);
    }, [data]);

    const tabs = [t('page.pagador.postal.form.tabs.dades')];

    return (
        <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[1]}>
            <MuiFormTabContent index={0} showOnCreate>
                <Grid container spacing={2}>
                    <GridFormField size={6} name="nom"/>
                    <GridFormField size={6} name="organGestor"/>
                    <GridFormField size={4} name="contracteNum"/>
                    <GridFormField size={4} name="contracteDataVig"/>
                    <GridFormField size={4} name="facturacioClientCodi"/>
                </Grid>
            </MuiFormTabContent>
        </MuiFormTabs>
    );
};

export const PagadorPostalForm: React.FC = () => {
    const {t} = useTranslation();
    const {id} = useParams();
    const [subtitle, setSubtitle] = React.useState<string>();
    return (
        <FormPage>
            <MuiForm
                resourceName="pagadorPostalResource"
                id={id != null ? parseInt(id) : id}
                title={
                    id != null
                        ? t('page.pagador.postal.form.titleUpdate')
                        : t('page.pagador.postal.form.titleCreate')
                }
                toolbarSubtitle={id != null ? subtitle : undefined}
                createLink="./{{id}}"
                //updateLink="../../"
                componentProps={{style: {height: '100%'}}}
                commonFieldComponentProps={{size: 'small'}}>
                <PagadorPostalFormContent setSubtitle={setSubtitle}/>
            </MuiForm>
        </FormPage>
    );
};
export default PagadorPostalForm;
