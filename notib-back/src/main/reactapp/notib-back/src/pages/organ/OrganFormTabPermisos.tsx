import React from 'react';
import { useTranslation } from 'react-i18next';
import { useFormContext } from 'reactlib';
import PermissionGrid from '../../components/PermissionGrid';
import { Icon, Tooltip, Grid } from '@mui/material';
import MailOutlineIcon from '@mui/icons-material/MailOutline';
import GridFormField from '../../components/GridFormField';
import PermissionGridSwitch from '../../components/PermissionGridSwitch';

const PermissionForm: React.FC = () => {
    const { t } = useTranslation();
    const { apiRef } = useFormContext();
    const doFieldChange = (targetValue: boolean) => {
        const permisos = [
            'readAllowed',
            'perm1Allowed',
            'perm2Allowed',
            'perm3Allowed',
            'perm4Allowed',
            'perm5Allowed',
            'perm6Allowed',
            'perm7Allowed',
        ];

        permisos.forEach((nomPermis) => {
            apiRef?.current?.setFieldValue?.(nomPermis, targetValue);
        });
    };
    const sidGrantedAuthorityEnumOptions = [
        {
            value: false,
            description: t('component.PermissionGrid.grantedAuthority.user'),
        },
        {
            value: true,
            description: t('component.PermissionGrid.grantedAuthority.role'),
        },
    ];
    return (
        <Grid container spacing={2}>
            <GridFormField
                name="sidGrantedAuthority"
                type="enum"
                label={t('component.PermissionGrid.tipus')}
                options={sidGrantedAuthorityEnumOptions}
                required
                size={3}
            />
            <GridFormField size={9} name="sidName" />
            <PermissionGridSwitch
                name="adminAllowed"
                label={t('page.organs.form.permisos.administrador')}
                tooltip={t('page.organs.form.permisos.administradorTooltip')}
                icon={<Icon>person_add_alt_1</Icon>}
                size={12}
            />
            <PermissionGridSwitch
                name="selectAll"
                label={'Seleccionar tots'}
                icon={<Icon>toggle_on</Icon>}
                size={12}
                onChange={doFieldChange}
            />
            <Grid size={1} />
            <PermissionGridSwitch
                name="readAllowed"
                label={t('page.organs.form.permisos.consulta')}
                tooltip={t('page.organs.form.permisos.consultaTooltip')}
                icon={<Icon>search</Icon>}
                size={11}
            />
            <Grid size={1} />
            <PermissionGridSwitch
                name="perm1Allowed"
                label={t('page.organs.form.permisos.processar')}
                tooltip={t('page.organs.form.permisos.processarTooltip')}
                icon={<Icon>check_box</Icon>}
                size={11}
            />
            <Grid size={1} />
            <PermissionGridSwitch
                name="perm2Allowed"
                label={t('page.organs.form.permisos.gestio')}
                tooltip={t('page.organs.form.permisos.gestioTooltip')}
                icon={<Icon>settings</Icon>}
                size={11}
            />
            <Grid size={1} />
            <PermissionGridSwitch
                name="perm3Allowed"
                label={t('page.organs.form.permisos.comuns')}
                tooltip={t('page.organs.form.permisos.comunsTooltip')}
                icon={<Icon>public</Icon>}
                size={11}
            />
            <Grid size={1} />
            <PermissionGridSwitch
                name="perm4Allowed"
                label={t('page.organs.form.permisos.notificacions')}
                tooltip={t('page.organs.form.permisos.notificacionsTooltip')}
                icon={<Icon>gavel</Icon>}
                size={11}
            />
            <Grid size={1} />
            <PermissionGridSwitch
                name="perm5Allowed"
                label={t('page.organs.form.permisos.comunicacions')}
                tooltip={t('page.organs.form.permisos.comunicacionsTooltip')}
                icon={<MailOutlineIcon />}
                size={11}
            />
            <Grid size={1} />
            <PermissionGridSwitch
                name="perm6Allowed"
                label={t('page.organs.form.permisos.sir')}
                tooltip={t('page.organs.form.permisos.sirTooltip')}
                icon={<Icon>email</Icon>}
                size={11}
            />
            <Grid size={1} />
            <PermissionGridSwitch
                name="perm7Allowed"
                label={t('page.organs.form.permisos.comSenseProc')}
                tooltip={t('page.organs.form.permisos.comSenseProcTooltip')}
                icon={<Icon>send</Icon>}
                size={11}
            />
        </Grid>
    );
};

const OrganFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useFormContext();
    const permissionEntries = [
        {
            headerName: t('page.organs.form.permisos.administrador'),
            field: 'adminAllowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.organs.form.permisos.administradorTooltip')}
                    arrow
                    placement="top"
                >
                    <Icon color="action">person_add_alt_1</Icon>
                </Tooltip>
            ),
            flex: 0.5,
            type: 'boolean',
        },
        {
            headerName: t('page.organs.form.permisos.consulta'),
            field: 'readAllowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.organs.form.permisos.consultaTooltip')}
                    arrow
                    placement="top"
                >
                    <Icon color="action">search</Icon>
                </Tooltip>
            ),
            flex: 0.5,
            type: 'boolean',
        },
        {
            headerName: t('page.organs.form.permisos.processar'),
            field: 'perm1Allowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.organs.form.permisos.processarTooltip')}
                    arrow
                    placement="top"
                >
                    <Icon color="action">check_box</Icon>
                </Tooltip>
            ),
            flex: 0.5,
            type: 'boolean',
        },
        {
            headerName: t('page.organs.form.permisos.gestio'),
            field: 'perm2Allowed',
            renderHeader: () => (
                <Tooltip title={t('page.organs.form.permisos.gestioTooltip')} arrow placement="top">
                    <Icon color="action">settings</Icon>
                </Tooltip>
            ),
            flex: 0.5,
            type: 'boolean',
        },
        {
            headerName: t('page.organs.form.permisos.comuns'),
            field: 'perm3Allowed',
            renderHeader: () => (
                <Tooltip title={t('page.organs.form.permisos.comunsTooltip')} arrow placement="top">
                    <Icon color="action">public</Icon>
                </Tooltip>
            ),
            flex: 0.5,
            type: 'boolean',
        },
        {
            headerName: t('page.organs.form.permisos.notificacions'),
            field: 'perm4Allowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.organs.form.permisos.notificacionsTooltip')}
                    arrow
                    placement="top"
                >
                    <Icon color="action">gavel</Icon>
                </Tooltip>
            ),
            flex: 0.5,
            type: 'boolean',
        },
        {
            headerName: t('page.organs.form.permisos.comunicacions'),
            field: 'perm5Allowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.organs.form.permisos.comunicacionsTooltip')}
                    arrow
                    placement="top"
                >
                    <MailOutlineIcon color="action" />
                </Tooltip>
            ),
            flex: 0.5,
            type: 'boolean',
        },
        {
            headerName: t('page.organs.form.permisos.sir'),
            field: 'perm6Allowed',
            renderHeader: () => (
                <Tooltip title={t('page.organs.form.permisos.sirTooltip')} arrow placement="top">
                    <Icon color="action">email</Icon>
                </Tooltip>
            ),
            flex: 0.5,
            type: 'boolean',
        },
        {
            headerName: t('page.organs.form.permisos.comSenseProc'),
            field: 'perm7Allowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.organs.form.permisos.comSenseProcTooltip')}
                    arrow
                    placement="top"
                >
                    <Icon color="action">send</Icon>
                </Tooltip>
            ),
            flex: 0.5,
            type: 'boolean',
        },
    ];
    return (
        <PermissionGrid
            resourceName="organGestorResource"
            id={id}
            permissionEntries={permissionEntries}
            permissionForm={<PermissionForm />}
        />
    );
};

export default OrganFormTabPermisos;
