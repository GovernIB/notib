import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import { FormPage, MuiForm, MuiFormTabs, MuiFormTabContent, useFormContext } from 'reactlib';
import ProcedimentFormTabGrups from './ProcedimentFormTabGrups.tsx';
import ProcedimentFormTabPermisos from './ProcedimentFormTabPermisos';
import GridFormField from '../../components/GridFormField.tsx';
import { useTabParam } from '../../hooks/useSearchParams.tsx';

const ProcedimentFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    const initialTab = useTabParam();

    React.useEffect(() => {
        setSubtitle(data?.codi + ', ' + data?.nom);
    }, [data]);

    const grupsTabLabel = (
        <Badge badgeContent={data.grupCount} color="primary">
            {t('page.procediments.form.tabs.grups')}
        </Badge>
    );

    const permisosTabLabel = (
        <Badge badgeContent={data.aclEntryCount} color="primary">
            {t('page.procediments.form.tabs.permisos')}
        </Badge>
    );

    const tabs = [
        t('page.procediments.form.tabs.dades'),
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
                <ProcedimentFormTabGrups />
            </MuiFormTabContent>
            <MuiFormTabContent index={2}>
                <ProcedimentFormTabPermisos />
            </MuiFormTabContent>
        </MuiFormTabs>
    );
};

export const ProcedimentForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const [subtitle, setSubtitle] = React.useState<string>();
    return (
        <FormPage>
            <MuiForm
                resourceName="procedimentResource"
                id={id != null ? parseInt(id) : id}
                additionalData={{ tipus: 'PROCEDIMENT' }}
                title={
                    id != null
                        ? t('page.procediments.form.titleUpdate')
                        : t('page.procediments.form.titleCreate')
                }
                toolbarSubtitle={id != null ? subtitle : undefined}
                componentProps={{ style: { height: '100%' } }}
                commonFieldComponentProps={{ size: 'small' }}
            >
                <ProcedimentFormContent setSubtitle={setSubtitle} />
            </MuiForm>
        </FormPage>
    );
};
export default ProcedimentForm;
