import React from "react";
import {useTranslation} from "react-i18next";
import {Grid, Icon, IconButton} from "@mui/material";
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import {springFilterBuilder, springFilterBuilder as filterBuilder, useFilterApiContext} from 'reactlib';
import {useNotibContext} from "../../components/NotibContext.ts";
import {formatEndOfDay, formatStartOfDay} from "../../utils/dateUtils.ts";

export const useSpringFilterBuilder = () => {

    const { currentUser } = useNotibContext();
    return (data: any) => {
        return filterBuilder.and(
            filterBuilder.eq('enviamentTipus', `'${data?.enviamentTipus}'`),
            filterBuilder.like('concepte', data?.concepte),
            filterBuilder.eq('estat', `'${data?.estat}'`),
            data?.dataIniciInici && filterBuilder.gte('createdDate', `'${formatStartOfDay(data?.dataIniciInici)}'`),
            data?.dataIniciFi && filterBuilder.lte('createdDate', `'${formatEndOfDay(data?.dataIniciFi)}'`),
            filterBuilder.like('titular', data?.interessat),
            filterBuilder.like('numExpedient', data?.numExpedient),
            filterBuilder.like('notificaIds', data?.identificadorNotifica),
            filterBuilder.eq('organGestor.id', data?.organGestor?.id),
            filterBuilder.eq('procediment.id', data?.procediment?.id),
            filterBuilder.eq('procediment.id', data?.servei?.id),
            filterBuilder.eq('tipusUsuari', `'${data?.tipusUsuari}'`),
            filterBuilder.eq('createdBy', `'${data?.createdBy}'`),
            filterBuilder.like('referencia', data?.referencia),
            filterBuilder.like('registreNums', data?.registreNumeroSortida),
            data?.dataCaducitatInici && filterBuilder.gte('caducitat', `'${formatStartOfDay(data?.dataCaducitatInici)}'`),
            data?.dataCaducitatFi && filterBuilder.lte('caducitat', `'${formatEndOfDay(data?.dataCaducitatFi)}'`),
            data?.nomesLesMeves && filterBuilder.eq('createdBy', `'${currentUser?.codi}'`),
            data?.entregaPostal && filterBuilder.eq('entregaPostal', `'${data?.entregaPostal}'`),
            data?.errorLastCallback && filterBuilder.eq('errorLastCallback', `'${data?.errorLastCallback}'`)
        );
    };
};

const ContentFilter: React.FC<{openByDefault?: boolean, notificacionsEsborrades: boolean, notificacionsErrorRegistre: boolean, notificacionsCallbackError: boolean }> =
                    ({openByDefault, notificacionsEsborrades, notificacionsErrorRegistre, notificacionsCallbackError}) => {

    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const [advancedFilter, setAdvancedFilter] = React.useState(openByDefault ?? false);
    const handleButtonClick = () => filterApiRef.current?.clear();
    const advancedFilterClick = () => setAdvancedFilter(!advancedFilter);

    if (notificacionsEsborrades) {
        return (
            <Grid container spacing={1}>
                <GridFormField size={2} name="enviamentTipus" />
                <GridFormField size={2} name="concepte" />
                <GridFormField size={2} name="createdBy" />
                <GridFormField size={2} name="estat" />
                <GridFormField size={1.75} name="dataIniciInici" />
                <GridFormField size={1.75} name="dataIniciFi" />
                <GridFormField size={2} name="interessat" />
                <GridFormField size={2} name="numExpedient" />
                <GridFormField size={2} name="identificadorNotifica" />
                <GridFormField size={6} name="organGestor" />
                <GridFormField size={5} name="procediment" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'PROCEDIMENT'`))}/>
                <GridFormField size={5} name="servei" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'SERVEI'`))}/>
                <Grid size={0.5} sx={{ textAlign: 'center' }}>
                    <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                        <Icon>filter_alt_off</Icon>
                    </IconButton>
                </Grid>
            </Grid>
        );
    }

    if (notificacionsErrorRegistre) {
        return (
            <Grid container spacing={1}>
                <GridFormField size={3.5} name="procediment" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'PROCEDIMENT'`))}/>
                <GridFormField size={3.5} name="servei" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'SERVEI'`))}/>
                <GridFormField size={4} name="concepte" />
                <GridFormField size={4} name="createdBy" />
                <GridFormField size={2} name="dataIniciInici" />
                <GridFormField size={2} name="dataIniciFi" />
                <Grid size={0.5} sx={{ textAlign: 'center' }}>
                    <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                        <Icon>filter_alt_off</Icon>
                    </IconButton>
                </Grid>
            </Grid>
        );
    }

    if (notificacionsCallbackError) {
        return (
            <Grid container spacing={1}>
                <GridFormField size={3.5} name="procediment" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'PROCEDIMENT'`))}/>
                <GridFormField size={3.5} name="servei" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'SERVEI'`))}/>
                <GridFormField size={4} name="concepte" />
                <GridFormField size={2} name="dataIniciInici" />
                <GridFormField size={2} name="dataIniciFi" />
                <GridFormField size={2} name="estat" />
                <GridFormField size={4} name="createdBy" />
                <Grid size={0.5} sx={{ textAlign: 'center' }}>
                    <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                        <Icon>filter_alt_off</Icon>
                    </IconButton>
                </Grid>
            </Grid>
        );
    }

    return (
        <Grid container spacing={1}>
            <GridFormField size={2} name="enviamentTipus" />
            <GridFormField size={advancedFilter ? 4 : 2.5} name="concepte" />
            <GridFormField size={2.5} name="estat" />
            <GridFormField size={1.75} name="dataIniciInici" />
            <GridFormField size={1.75} name="dataIniciFi" />

            {advancedFilter && (
                <>
                    <GridFormField size={2} name="interessat" />
                    <GridFormField size={2} name="numExpedient" />
                    <GridFormField size={2} name="identificadorNotifica" />
                    <GridFormField size={6} name="organGestor" />
                    <GridFormField
                        size={3.5}
                        name="procediment"
                        filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'PROCEDIMENT'`))}
                    />
                    <GridFormField
                        size={3.5}
                        name="servei"
                        filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'SERVEI'`))}
                    />
                    <GridFormField size={2} name="tipusUsuari" />
                    <GridFormField size={3} name="createdBy" />
                    <GridFormField size={3} name="referencia" />
                    <GridFormField size={2.5} name="registreNumeroSortida" />
                    <GridFormField size={1.75} name="dataCaducitatInici" />
                    <GridFormField size={1.75} name="dataCaducitatFi" />
                    <GridButtonField size={0.5} name="nomesLesMeves" icon={'person'} hiddenLabel />
                    <GridButtonField size={0.5} name="entregaPostal" icon={'email'} hiddenLabel />
                    <GridButtonField size={0.5} name="errorLastCallback" icon={'report_problem'} hiddenLabel/>
                </>
            )}
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={advancedFilterClick} title={t(advancedFilter ? 'comu.tancarFiltreAvançat' : 'comu.obrirFiltreAvançat')}>
                    <Icon sx={{ transform: advancedFilter ? 'rotate(180deg)' : 'none' }}>filter_list</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

export default ContentFilter;
