import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import { MuiFormTabs, MuiFormTabContent, useFormContext } from 'reactlib';
import ServeiFormTabGrups from './ServeiFormTabGrups';
import ServeiFormTabPermisos from './ServeiFormTabPermisos';
import GridFormField from '../../components/GridFormField';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer';
import { useTabParam } from '../../hooks/useSearchParams';


export const ServeiForm: React.FC = () => {

    const { t } = useTranslation();
    const { data } = useFormContext();
    const initialTab = useTabParam();
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
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
