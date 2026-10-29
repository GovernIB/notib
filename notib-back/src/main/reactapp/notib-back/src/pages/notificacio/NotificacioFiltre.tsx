import React from "react";
import GridFormField, {GridButtonField} from "../../components/GridFormField.tsx";
import useOrganGestorOptionRenderer from "../../components/OrganGestorOptionRenderer.tsx";
import {springFilterBuilder, springFilterBuilder as filterBuilder, useFilterApiContext, useFormContext} from 'reactlib';
import {useNotibContext} from "../../components/NotibContext.ts";
import {formatEndOfDay, formatStartOfDay} from "../../utils/dateUtils.ts";

export const useSpringFilterBuilder = () => {

    const { currentUser } = useNotibContext();
    return (data: any) => {
        let filter = filterBuilder.and(
            filterBuilder.eq('enviamentTipus', `'${data?.enviamentTipus}'`),
            filterBuilder.like('concepte', data?.concepte),
            filterBuilder.eq('estat', `'${data?.estat}'`),
            data?.dataIniciInici && filterBuilder.gte('createdDate', `'${formatStartOfDay(data?.dataIniciInici)}'`),
            data?.dataIniciFi && filterBuilder.lte('createdDate', `'${formatEndOfDay(data?.dataIniciFi)}'`),
            filterBuilder.like('titular', data?.interessat),
            filterBuilder.like('numExpedient', data?.numExpedient),
            filterBuilder.like('notificaIds', data?.identificadorNotifica),
            filterBuilder.eq('organGestor.id', data?.organGestor?.id),
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
        return filter;
    };
};

const ContentFilter: React.FC<{ notificacionsEsborrades: boolean, notificacionsErrorRegistre: boolean, notificacionsCallbackError: boolean }> =
                    ({ notificacionsEsborrades, notificacionsErrorRegistre, notificacionsCallbackError }) => {

    const filterApiRef = useFilterApiContext();
    const organGestorOptionRenderer = useOrganGestorOptionRenderer();
    const { data } = useFormContext();
    const organId = filterApiRef.current?.getData()?.organGestor?.id;
    let procedimentFiltre;
    let serveiFiltre;
    if (organId) {
        procedimentFiltre = springFilterBuilder.and(springFilterBuilder.eq('tipus', `'PROCEDIMENT'`),
                                                    springFilterBuilder.and(springFilterBuilder.eq('organGestor.id', organId)));
        serveiFiltre = springFilterBuilder.and(springFilterBuilder.eq('tipus', `'SERVEI'`),
                                                springFilterBuilder.and(springFilterBuilder.eq('organGestor.id', organId)));
    } else {
        procedimentFiltre = springFilterBuilder.and(springFilterBuilder.eq('tipus', `'PROCEDIMENT'`));
        serveiFiltre = springFilterBuilder.and(springFilterBuilder.eq('tipus', `'SERVEI'`));
    }

    if (notificacionsEsborrades) {
        return (
            <>
                {!data?.advanced && (
                    <>
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="enviamentTipus" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="concepte" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="estat" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="dataIniciInici" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="dataIniciFi" />
                    </>
                )}
                {data?.advanced && (
                    <>
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="enviamentTipus" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="concepte" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="createdBy" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="estat" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataIniciInici" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataIniciFi" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="interessat" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="numExpedient" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="identificadorNotifica" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 6 }} name="organGestor" namedQueries={`PERM_READ`} optionRenderer={organGestorOptionRenderer} />
                        <GridFormField size={{ xs: 12, sm: 6, md: 5 }} name="procediment" filter={procedimentFiltre} />
                        <GridFormField size={{ xs: 12, sm: 6, md: 5 }} name="servei" filter={serveiFiltre} />
                    </>
                )}
            </>
        );
    }

    if (notificacionsErrorRegistre) {
        return (
            <>
                {!data?.advanced && (
                    <>
                        <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="concepte" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="createdBy" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2.25 }} name="dataIniciInici" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2.25 }} name="dataIniciFi" />
                    </>
                )}
                {data?.advanced && (
                    <>
                        <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="procediment" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'PROCEDIMENT'`))} />
                        <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="servei" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'SERVEI'`))} />
                        <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="concepte" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="createdBy" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="dataIniciInici" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="dataIniciFi" />
                    </>
                )}
            </>
        );
    }

    if (notificacionsCallbackError) {
        return (
            <>
                {!data?.advanced && (
                    <>
                        <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="concepte" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="createdBy" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="estat" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataIniciInici" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataIniciFi" />
                    </>
                )}
                {data?.advanced && (
                    <>
                        <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="procediment" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'PROCEDIMENT'`))} />
                        <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="servei" filter={springFilterBuilder.and(springFilterBuilder.eq('tipus', `'SERVEI'`))} />
                        <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="concepte" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2.25 }} name="dataIniciInici" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2.25 }} name="dataIniciFi" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="estat" />
                        <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="createdBy" />
                    </>
                )}
            </>
        );
    }

    return (
        <>
            {!data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="enviamentTipus" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="concepte" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="estat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="dataIniciInici" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="dataIniciFi" />
                    <GridButtonField size={{ xs: 12, sm: 6, md: 0.5 }} name="nomesLesMeves" icon={'person'} hiddenLabel />
                </>
            )}
            {data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="enviamentTipus" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="concepte" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="estat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="dataIniciInici" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="dataIniciFi" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="interessat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="numExpedient" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="identificadorNotifica" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 6 }} name="organGestor" namedQueries={`PERM_READ`} optionRenderer={organGestorOptionRenderer} />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3.5 }} name="procediment" filter={procedimentFiltre} />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3.5 }} name="servei" filter={serveiFiltre} />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="tipusUsuari" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="createdBy" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="referencia" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="registreNumeroSortida" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="dataCaducitatInici" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.75 }} name="dataCaducitatFi" />
                    <GridButtonField size={{ xs: 12, sm: 6, md: 0.5 }} name="nomesLesMeves" icon={'person'} hiddenLabel />
                    <GridButtonField size={{ xs: 12, sm: 6, md: 0.5 }} name="entregaPostal" icon={'email'} hiddenLabel />
                    <GridButtonField size={{ xs: 12, sm: 6, md: 0.5 }} name="errorLastCallback" icon={'report_problem'} hiddenLabel />
                </>
            )}
        </>
    );
};

export default ContentFilter;
