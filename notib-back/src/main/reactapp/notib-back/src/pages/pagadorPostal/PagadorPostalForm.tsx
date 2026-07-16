import React from 'react';
import {useParams} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import Grid from '@mui/material/Grid';
import {FormPage, MuiForm, useFormContext} from 'reactlib';
import GridFormField from '../../components/GridFormField';

const PagadorPostalFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {

    const { setSubtitle } = props;
    const { data } = useFormContext();

    React.useEffect(() => {
        setSubtitle(data?.nom);
    }, [data]);

    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="nom" />
            <GridFormField size={12} name="organGestor" />
            <GridFormField size={4} name="contracteNum" />
            <GridFormField size={4} name="facturacioClientCodi" />
            <GridFormField size={4} name="contracteDataVig" />
        </Grid>
    );
};

export const PagadorPostalForm: React.FC = () => {

    const { t } = useTranslation();
    const { id } = useParams();
    const [subtitle, setSubtitle] = React.useState<string>();
    const isUpdate = id != null;
    return (
        <FormPage>
            <MuiForm
                resourceName="pagadorPostalResource"
                id={isUpdate ? Number.parseInt(id) : id}
                title={isUpdate ? t('page.pagadorPostal.form.titleUpdate') : t('page.pagadorPostal.form.titleCreate')}
                toolbarSubtitle={isUpdate ? subtitle : undefined}
                createLink="./{{id}}"
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}
            >
                <PagadorPostalFormContent setSubtitle={setSubtitle} />
            </MuiForm>
        </FormPage>
    );
};

export default PagadorPostalForm;

