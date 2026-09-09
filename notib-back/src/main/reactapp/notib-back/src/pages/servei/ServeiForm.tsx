import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import { FormPage, MuiForm, MuiFormTabs, MuiFormTabContent, useFormContext } from 'reactlib';
import ServeiFormTabGrups from './ServeiFormTabGrups';
import ServeiFormTabPermisos from './ServeiFormTabPermisos';
import GridFormField from '../../components/GridFormField';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer';
import { useTabParam } from '../../hooks/useSearchParams';

const ServeiFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    const initialTab = useTabParam();
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
    React.useEffect(() => {
        setSubtitle(data?.codi + ', ' + data?.nom);
    }, [data]);
    const grupsTabLabel = (
        <Badge badgeContent={data.grupCount} color="primary">
            {t('page.serveis.form.tabs.grups')}
        </Badge>
    );
    const permisosTabLabel = (
        <Badge badgeContent={data.aclEntryCount} color="primary">
            {t('page.serveis.form.tabs.permisos')}
        </Badge>
    );
    const tabs = [
        t('page.serveis.form.tabs.dades'),
        { label: grupsTabLabel },
        { label: permisosTabLabel },
    ];
    return (
        <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[1, 2]} initialIndex={initialTab}>
            <MuiFormTabContent index={0} showOnCreate>
                <Grid container spacing={2}>
                    <GridFormField size={3} name="codi" />
                    <GridFormField size={9} name="nom" />
                    <GridFormField size={6} name="retard" />
                    <GridFormField
                        size={6}
                        name="caducitat"
                        componentProps={{ helperText: 'En dies naturals' }}
                    />
                    <GridFormField
                        size={9}
                        name="organGestor"
                        disabled={data?.fieldOrganGestorDisabled}
                        optionRenderer={organGestorOptionRenderer}
                    />
                    <GridFormField size={3} name="comu" />
                    {!data?.fieldEntregaCieHidden && (
                        <>
                            <GridFormField size={2} name="entregaCieActiva" />
                            {data?.entregaCieActiva && (
                                <>
                                    <GridFormField size={4} name="entregaCiePagadorPostal" />
                                    <GridFormField size={4} name="entregaCiePagadorCie" />
                                    <Grid size={2} />
                                </>
                            )}
                        </>
                    )}
                    <GridFormField size={2} name="agrupar" />
                    <GridFormField size={3} name="requireDirectPermission" />
                    <GridFormField size={2} name="manual" />
                </Grid>
            </MuiFormTabContent>
            <MuiFormTabContent index={1}>
                <ServeiFormTabGrups />
            </MuiFormTabContent>
            <MuiFormTabContent index={2}>
                <ServeiFormTabPermisos />
            </MuiFormTabContent>
        </MuiFormTabs>
    );
};

export const ServeiForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const [subtitle, setSubtitle] = React.useState<string>();
    return (
        <FormPage>
            <MuiForm
                resourceName="procedimentResource"
                id={id != null ? parseInt(id) : id}
                additionalData={{ tipus: 'SERVEI' }}
                title={
                    id != null
                        ? t('page.serveis.form.titleUpdate')
                        : t('page.serveis.form.titleCreate')
                }
                toolbarSubtitle={id != null ? subtitle : undefined}
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}
            >
                <ServeiFormContent setSubtitle={setSubtitle} />
            </MuiForm>
        </FormPage>
    );
};
export default ServeiForm;
