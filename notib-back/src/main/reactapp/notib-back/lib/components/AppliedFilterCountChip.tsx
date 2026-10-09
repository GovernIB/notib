import React from 'react';
import {Box, Chip, Icon} from '@mui/material';
import {useOptionalFormContext} from '../components/form/FormContext';
import {useTranslation} from "react-i18next";

type AppliedFilterCountChipProps = {
    /**
     * Llista de camps a comptar. Si no s'especifica, es prenen automàticament
     * tots els camps del formulari de filtre (definits a l'artefacte FILTER).
     */
    fields?: string[];
    /**
     * Nombre de filtres aplicats. Si s'especifica, no es llegeix el context
     * del formulari (permet utilitzar-lo fora del filtre, p.ex. al costat del títol).
     */
    count?: number;
    /** Amaga el xip quan no hi ha cap filtre aplicat (per defecte: true) */
    hideWhenEmpty?: boolean;
    size?: 'small' | 'medium';
    sx?: any;
};

/** Determina si un valor de camp compta com a filtre aplicat. */
export const isFilterApplied = (value: any): boolean => {

    if (value == null) {
        return false;
    }
    if (typeof value === 'boolean') {
        return value;
    }
    if (typeof value === 'string') {
        return value.trim() !== '';
    }
    if (value instanceof Date) {
        return !Number.isNaN(value.getTime());
    }
    if (typeof value === 'object') {
        return value.id != null;
    }
    return true;
};

const AppliedFilterCountChip: React.FC<AppliedFilterCountChipProps> =
    ({fields: fieldNames, count: countProp, hideWhenEmpty = true}) => {

        const formContext = useOptionalFormContext();
        const { t } = useTranslation();
        const count = React.useMemo(() => {
            if (countProp != null) {
                return countProp;
            }
            const names = fieldNames ?? formContext?.fields?.map((f: any) => f?.name).filter(Boolean);
            if (!names) {
                return 0;
            }
            return names.reduce(
                (acc: number, name: string) => acc + (isFilterApplied(formContext?.data?.[name]) ? 1 : 0),
                0
            );
        }, [countProp, formContext, fieldNames]);
        if (hideWhenEmpty && count === 0) {
            return null;
        }
        return (
            <Chip
                color="default"
                sx={{ml: 1, height: "20px", backgroundColor: "transparent"}}
                label={
                    <Box component="span" sx={{display: 'inline-flex', alignItems: 'center', gap: 0.5}}>
                        <span>(</span>
                        <Icon sx={{fontSize: '0.8rem'}}>filter_alt</Icon>
                        <span>{count} {t('comu.filtresAplicats')}</span>
                        <span>)</span>
                    </Box>
                }
            />
        );
    };

export default AppliedFilterCountChip;
