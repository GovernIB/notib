import React from 'react';
import { useTranslation } from 'react-i18next';
import { useFormContext } from 'reactlib';
import PermissionGrid from '../../components/PermissionGrid';

const OrganFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useFormContext();
    const permissionEntries = [
        {
            headerName: t('page.organs.form.permisos.administrador'),
            field: 'adminAllowed',
        },
        {
            headerName: t('page.organs.form.permisos.consulta'),
            field: 'readAllowed',
        },
        {
            headerName: t('page.organs.form.permisos.processar'),
            field: 'perm1Allowed',
        },
        {
            headerName: t('page.organs.form.permisos.gestio'),
            field: 'perm2Allowed',
        },
        {
            headerName: t('page.organs.form.permisos.comuns'),
            field: 'perm3Allowed',
        },
        {
            headerName: t('page.organs.form.permisos.notificacions'),
            field: 'perm4Allowed',
        },
        {
            headerName: t('page.organs.form.permisos.comunicacions'),
            field: 'perm5Allowed',
        },
        {
            headerName: t('page.organs.form.permisos.sir'),
            field: 'perm6Allowed',
        },
        {
            headerName: t('page.organs.form.permisos.comSenseProc'),
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

export default OrganFormTabPermisos;
