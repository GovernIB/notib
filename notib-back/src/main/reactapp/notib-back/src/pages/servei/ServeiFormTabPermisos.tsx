import React from 'react';
import { useTranslation } from 'react-i18next';
import { useFormContext } from 'reactlib';
import PermissionGrid from '../../components/PermissionGrid';
import { FormFieldDataActionType } from '../../../lib/components/form/FormContext';
import { Grid, Icon, Tooltip } from '@mui/material';
import GridFormField from '../../components/GridFormField';
import PermissionGridSwitch from '../../components/PermissionGridSwitch';
import MailOutlineIcon from '@mui/icons-material/MailOutline';

const PermissionForm: React.FC = () => {
    const { t } = useTranslation();
    const { dataDispatchAction, fields } = useFormContext();

    const doFieldChange = (targetValue: boolean) => {
        const permisos = [
            'adminAllowed',
            'perm4Allowed',
            'adminAllowed',
            'perm5Allowed',
            'perm8Allowed',
            'perm7Allowed',
        ];

        permisos.forEach((nomPermis) => {
            dataDispatchAction({
                type: FormFieldDataActionType.FIELD_CHANGE,
                payload: {
                    fieldName: nomPermis,
                    value: targetValue,
                    field: fields?.find((f) => f.name === nomPermis),
                },
            });
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
                name="selectAll"
                label={'Seleccionar tots'}
                icon={<Icon>toggle_on</Icon>}
                size={12}
                onChange={doFieldChange}
            />
            <Grid size={1} />
            <PermissionGridSwitch
                name="adminAllowed"
                label={t('page.serveis.form.permisos.consultaAllowed')}
                tooltip={t('page.serveis.form.permisos.consultaAllowedTooltip')}
                icon={<Icon>search</Icon>}
                size={11}
            />
            <Grid size={1} />
            <PermissionGridSwitch
                name="perm4Allowed"
                label={t('page.serveis.form.permisos.procesAllowed')}
                tooltip={t('page.serveis.form.permisos.procesAllowedTooltip')}
                icon={<Icon>check_box</Icon>}
                size={11}
            />

            <Grid size={1} />
            <PermissionGridSwitch
                name="adminAllowed"
                label={t('page.serveis.form.permisos.gestioAllowed')}
                tooltip={t('page.serveis.form.permisos.gestioAllowedTooltip')}
                icon={<Icon>settings</Icon>}
                size={11}
            />

            <Grid size={1} />
            <PermissionGridSwitch
                name="perm5Allowed"
                label={t('page.serveis.form.permisos.notificacioAllowed')}
                tooltip={t('page.serveis.form.permisos.notificacioAllowedTooltip')}
                icon={<Icon>gavel</Icon>}
                size={11}
            />

            <Grid size={1} />
            <PermissionGridSwitch
                name="perm8Allowed"
                label={t('page.serveis.form.permisos.comunicacioAllowed')}
                tooltip={t('page.serveis.form.permisos.comunicacioAllowedTooltip')}
                icon={<MailOutlineIcon />}
                size={11}
            />

            <Grid size={1} />
            <PermissionGridSwitch
                name="perm7Allowed"
                label={t('page.serveis.form.permisos.comunicacioSirAllowed')}
                tooltip={t('page.serveis.form.permisos.comunicacioSirAllowedTooltip')}
                icon={<Icon>email</Icon>}
                size={11}
            />
        </Grid>
    );
};
const ServeiFormTabPermisos: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useFormContext();
    const permissionEntries = [
        {
            headerName: t('page.serveis.form.permisos.consultaAllowed'),
            field: 'readAllowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.serveis.form.permisos.consultaAllowedTooltip')}
                    arrow
                    placement="top"
                >
                    <Icon>search</Icon>
                </Tooltip>
            ),
            flex: 0.5,
        },
        {
            headerName: t('page.serveis.form.permisos.procesAllowed'),
            field: 'perm4Allowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.serveis.form.permisos.procesAllowedTooltip')}
                    arrow
                    placement="top"
                >
                    <Icon>check_box</Icon>
                </Tooltip>
            ),
            flex: 0.5,
        },
        {
            headerName: t('page.serveis.form.permisos.gestioAllowed'),
            field: 'adminAllowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.serveis.form.permisos.gestioAllowedTooltip')}
                    arrow
                    placement="top"
                >
                    <Icon>settings</Icon>
                </Tooltip>
            ),
            flex: 0.5,
        },
        {
            headerName: t('page.serveis.form.permisos.notificacioAllowed'),
            field: 'perm5Allowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.serveis.form.permisos.notificacioAllowedTooltip')}
                    arrow
                    placement="top"
                >
                    <Icon>gavel</Icon>
                </Tooltip>
            ),
            flex: 0.5,
        },
        {
            headerName: t('page.serveis.form.permisos.comunicacioAllowed'),
            field: 'perm8Allowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.serveis.form.permisos.comunicacioAllowedTooltip')}
                    arrow
                    placement="top"
                >
                    <MailOutlineIcon />
                </Tooltip>
            ),
            flex: 0.5,
        },
        {
            headerName: t('page.serveis.form.permisos.comunicacioSirAllowed'),
            field: 'perm7Allowed',
            renderHeader: () => (
                <Tooltip
                    title={t('page.serveis.form.permisos.comunicacioSirAllowedTooltip')}
                    arrow
                    placement="top"
                >
                    <Icon>email</Icon>
                </Tooltip>
            ),
            flex: 0.5,
        },
    ];
    return (
        <PermissionGrid
            resourceName="procedimentResource"
            id={id}
            permissionEntries={permissionEntries}
            permissionForm={<PermissionForm />}
        />
    );
};

export default ServeiFormTabPermisos;
