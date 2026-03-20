import React from 'react';
import { useTranslation } from 'react-i18next';
import { useFormContext } from 'reactlib';
import PermissionGrid from '../../components/PermissionGrid';

const ServeiFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useFormContext();
    const permissionEntries = [
        {
            headerName: t('page.serveis.form.permisos.consultaAllowed'),
            field: 'readAllowed',
        },
        {
            headerName: t('page.serveis.form.permisos.procesAllowed'),
            field: 'perm4Allowed',
        },
        {
            headerName: t('page.serveis.form.permisos.gestioAllowed'),
            field: 'adminAllowed',
        },
        {
            headerName: t('page.serveis.form.permisos.notificacioAllowed'),
            field: 'perm5Allowed',
        },
        {
            headerName: t('page.serveis.form.permisos.comunicacioAllowed'),
            field: 'perm8Allowed',
        },
        {
            headerName: t('page.serveis.form.permisos.comunicacioSirAllowed'),
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

export default ServeiFormTabPermisos;
