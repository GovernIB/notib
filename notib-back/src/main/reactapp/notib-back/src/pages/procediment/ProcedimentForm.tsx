import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Badge from '@mui/material/Badge';
import { useFormContext } from 'reactlib';
import ProcedimentFormTabGrups from './ProcedimentFormTabGrups';
import ProcedimentFormTabPermisos from './ProcedimentFormTabPermisos';
import GridFormField from '../../components/GridFormField';
import useOrganGestorOptionRenderer from '../../components/OrganGestorOptionRenderer';
import { useTabParam } from '../../hooks/useSearchParams';
import { FormTabs, FormTabContent } from '../../components/FormTabs';

export const ProcedimentForm: React.FC = () => {

    const { t } = useTranslation();
    const { data } = useFormContext();
    const initialTab = useTabParam();
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
    const grupsTabLabel = (<Badge badgeContent={data.grupCount} color="primary">{t('page.procediments.form.tabs.grups')}</Badge>);
    const permisosTabLabel = (<Badge badgeContent={data.aclEntryCount} color="primary">{t('page.procediments.form.tabs.permisos')}</Badge>);
    const showGrupsTab = !!data?.agrupar;
    const tabs = [
        t('page.procediments.form.tabs.dades'),
        ...(showGrupsTab ? [{ label: grupsTabLabel }] : []),
        { label: permisosTabLabel },
    ];
    const grupsTabIndex = 1;
    const permisosTabIndex = showGrupsTab ? 2 : 1;
    const tabIndexesWithGrids = showGrupsTab ? [grupsTabIndex, permisosTabIndex] : [permisosTabIndex];
    return (
        <FormTabs tabs={tabs} tabIndexesWithGrids={tabIndexesWithGrids} initialIndex={initialTab}>
            <FormTabContent index={0} showOnCreate>
                <Grid container spacing={2}>

                    <GridFormField size={3} name="tipus" hidden componentProps={{type: "hidden", value: data?.tipus?? 'PROCEDIMENT'}} sx={{ display: 'none'}}/>
                    <GridFormField size={3} name="codi" />
                    <GridFormField size={9} name="nom" />
                    <GridFormField size={6} name="retard" />
                    <GridFormField size={6} name="caducitat" componentProps={{ helperText: 'En dies naturals' }}/>
                    <GridFormField size={3} name="comu" />
                    {!data?.comu && <GridFormField size={9} name="organGestor" optionRenderer={organGestorOptionRenderer} />}
                    {!data?.fieldEntregaCieHidden && !data?.comu && (
                        <>
                            <GridFormField size={3} name="entregaCieActiva" />
                            {data?.entregaCieActiva && (
                                <>
                                    <GridFormField size={4} name="entregaCiePagadorPostal" />
                                    <GridFormField size={5} name="entregaCiePagadorCie" />
                                    {/*<Grid size={1} />*/}
                                </>
                            )}
                        </>
                    )}
                    <GridFormField size={2} name="agrupar" />
                    <GridFormField size={3} name="requireDirectPermission" />
                    <GridFormField size={2} name="manual" />
                </Grid>
            </FormTabContent>
            {showGrupsTab && (
                <FormTabContent index={grupsTabIndex} grid>
                    <ProcedimentFormTabGrups />
                </FormTabContent>
            )}
            <FormTabContent index={permisosTabIndex} grid>
                <ProcedimentFormTabPermisos />
            </FormTabContent>
        </FormTabs>
    );
};
