import React from 'react';
import { useTranslation } from 'react-i18next';
import { useFormContext } from 'reactlib';
import PermissionGrid from '../../components/PermissionGrid';

const EntitatFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useFormContext();
    const permissionEntries = [
        {
            headerName: t('page.entitats.form.permisos.usuariAllowed'),
            field: 'perm0Allowed',
        },
        {
            headerName: t('page.entitats.form.permisos.admEntitatAllowed'),
            field: 'perm2Allowed',
        },
        {
            headerName: t('page.entitats.form.permisos.admLecturaAllowed'),
            field: 'permXAllowed',
        },
        {
            headerName: t('page.entitats.form.permisos.aplicacioAllowed'),
            field: 'perm3Allowed',
        },
    ];
    return (
        <PermissionGrid
            resourceName="entitatResource"
            id={id}
            permissionEntries={permissionEntries}
        />
    );
};

export default EntitatFormTabPermisos;
