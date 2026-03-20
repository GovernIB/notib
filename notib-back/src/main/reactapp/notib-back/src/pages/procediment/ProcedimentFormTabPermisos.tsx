import React from 'react';
import { useTranslation } from 'react-i18next';
import { useFormContext } from 'reactlib';
import PermissionGrid from '../../components/PermissionGrid';

const ProcedimentFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useFormContext();
    const permissionEntries = [
        {
            headerName: t('page.procediments.form.permisos.consultaAllowed'),
            field: 'readAllowed',
        },
        {
            headerName: t('page.procediments.form.permisos.procesAllowed'),
            field: 'perm4Allowed',
        },
        {
            headerName: t('page.procediments.form.permisos.gestioAllowed'),
            field: 'adminAllowed',
        },
        {
            headerName: t('page.procediments.form.permisos.notificacioAllowed'),
            field: 'perm5Allowed',
        },
        {
            headerName: t('page.procediments.form.permisos.comunicacioAllowed'),
            field: 'perm8Allowed',
        },
        {
            headerName: t('page.procediments.form.permisos.comunicacioSirAllowed'),
            field: 'perm7Allowed',
        },
    ];
    return (
        <PermissionGrid
            resourceName="procedimentResource"
            id={id}
            permissionEntries={permissionEntries}
        />
    );
};

export default ProcedimentFormTabPermisos;
