import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import { FormPage, MuiForm, MuiFormTabs, MuiFormTabContent, useFormContext } from 'reactlib';
import OrganFormTabPermisos from './OrganFormTabPermisos';
import GridFormField from '../../components/GridFormField';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer';
import { useTabParam } from '../../hooks/useSearchParams';
import ScreenFrame from '../../components/ScreenFrame';
import FormScreen from '../../components/FormScreen';
import FormBackButton from '../../components/FormBackButton';
import { FORM_NO_TITLE } from '../../utils/formScreenTitle';
import { icons } from '../../utils/icons';

export const OrganFormContent: React.FC<{ setSubtitle?: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    const initialTab = useTabParam();
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();

    React.useEffect(() => {
        setSubtitle?.(data?.codi + ', ' + data?.nom);
    }, [data]);

    const permisosTabLabel = (
        <Badge badgeContent={data.aclEntryCount} color="primary">
            {t('page.organs.form.tabs.permisos')}
        </Badge>
    );

    const tabs = [t('page.organs.form.tabs.dades'), { label: permisosTabLabel }];

    return (
        <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[1]} initialIndex={initialTab}>
            <MuiFormTabContent index={0} showOnCreate>
                <Grid container spacing={2}>
                    <GridFormField size={12} name="pare" optionRenderer={organGestorOptionRenderer} />
                    <GridFormField size={4} name="codi" />
                    <GridFormField size={8} name="nom" />
                    <GridFormField size={4} name="estat" />
                    <GridFormField size={6} name="llibre" />
                    <GridFormField size={6} name="oficina" />
                    <GridFormField size={2} name="permetreSir" />
                    <GridFormField size={2} name="entregaCieActiva" />
                    {data?.entregaCieActiva && (
                        <>
                            <GridFormField size={3} name="entregaCiePagadorPostal" />
                            <GridFormField size={3} name="entregaCiePagadorCie" />
                            <GridFormField size={4} name="sobrescriureCieOrganEmisor" />
                        </>
                    )}
                    <GridFormField size={4} name="entregaCieDesactivada" />
                </Grid>
            </MuiFormTabContent>
            <MuiFormTabContent index={1} style={{ height: '500px' }}>
                <OrganFormTabPermisos />
            </MuiFormTabContent>
        </MuiFormTabs>
    );
};

export const OrganForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const [subtitle, setSubtitle] = React.useState<string>();
    const titol = id != null ? t('page.organs.form.titleUpdate') : t('page.organs.form.titleCreate');
    return (
        <FormPage>
            <ScreenFrame
                icon={icons.organ}
                title={titol}
                subtitle={id != null ? subtitle : undefined}
                beforeTitle={<FormBackButton />}>
                <FormScreen>
                    <MuiForm
                        resourceName="organGestorResource"
                        id={id != null ? parseInt(id) : id}
                        title={FORM_NO_TITLE}
                        hiddenBackButton
                        createLink="./{{id}}"
                        //updateLink="../../"
                        componentProps={{ style: { height: '100%' } }}
                        commonFieldComponentProps={{ size: 'small' }}
                    >
                        <OrganFormContent setSubtitle={setSubtitle} />
                    </MuiForm>
                </FormScreen>
            </ScreenFrame>
        </FormPage>
    );
};

export default OrganForm;
