import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Alert from '@mui/material/Alert';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { MuiForm, FormField, useFormContext } from 'reactlib';

const NotificacioFormEnviamentPersonaFormContent: React.FC<{ interessat?: boolean }> = (props) => {
    const { interessat } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    return (
        <Grid container spacing={2}>
            <Grid size={12} sx={{ mb: 1 }}>
                {t(
                    'page.notificacio.form.interessats.' +
                        (interessat ? 'interessat' : 'representant')
                )}
            </Grid>
            <Grid size={6}>
                <FormField name="interessatTipus" />
            </Grid>
            {data.visibleDocumentTipus && (
                <Grid size={6}>
                    <FormField name="documentTipus" />
                </Grid>
            )}
            {data.visibleNif && (
                <Grid size={6}>
                    <FormField
                        name="nif"
                        label={t(
                            'page.notificacio.form.interessats.nifLabel.' + data.interessatTipus
                        )}
                        required={data.requiredNif}
                        debounce
                    />
                </Grid>
            )}
            {data.visibleNom && (
                <Grid size={6}>
                    <FormField name="nom" required={data.requiredNom} debounce />
                </Grid>
            )}
            {data.visibleLlinatge1 && (
                <Grid size={6}>
                    <FormField name="llinatge1" required={data.requiredLlinatge1} debounce />
                </Grid>
            )}
            {data.visibleLlinatge2 && (
                <Grid size={6}>
                    <FormField name="llinatge2" debounce />
                </Grid>
            )}
            {data.visibleRaoSocial && (
                <Grid size={6}>
                    <FormField name="raoSocial" required={data.requiredRaoSocial} debounce />
                </Grid>
            )}
            {data.visibleDir3Codi && (
                <Grid size={6}>
                    <FormField name="dir3Codi" required={data.requiredDir3Codi} debounce />
                </Grid>
            )}
            {data.visibleTelefon && (
                <Grid size={6}>
                    <FormField name="telefon" debounce />
                </Grid>
            )}
            {data.visibleEmail && (
                <Grid size={6}>
                    <FormField name="email" required={data.requiredEmail} debounce />
                </Grid>
            )}
            {data.visibleIncapacitat && (
                <Grid size={6}>
                    <FormField name="incapacitat" debounce />
                </Grid>
            )}
        </Grid>
    );
};

const NotificacioFormEnviamentPersona: React.FC<{
    index?: number;
    indexKey?: number;
    interessat?: boolean;
}> = (props) => {
    const { index, indexKey, interessat } = props;
    const [currentPersonaFieldValidationErrors, setCurrentPersonaFieldValidationErrors] =
        React.useState<any[]>();
    const {
        data: parentFormData,
        fieldErrors: parentFieldErrors,
        apiRef: parentFormApiRef,
    } = useFormContext();
    React.useEffect(() => {
        const errorPrefix = interessat ? 'titularInfo' : 'representantsInfo[' + index + ']';
        const currentPersonaFieldValidationErrors = parentFieldErrors
            ?.filter((e) => e.field.startsWith(errorPrefix))
            .map((e) => ({ ...e, field: e.field.substring(errorPrefix.length + 1) }));
        setCurrentPersonaFieldValidationErrors(currentPersonaFieldValidationErrors);
    }, [parentFieldErrors]);
    const handleDataChange = (data: any) => {
        if (interessat) {
            parentFormApiRef.current?.setFieldValue('titularInfo', data);
        } else {
            if (parentFormData?.representantsInfo != null) {
                const representantsWithData = parentFormData?.representantsInfo?.map((e: any) =>
                    e.id === indexKey ? { id: indexKey, ...data } : e
                );
                parentFormApiRef.current?.setFieldValue('representantsInfo', representantsWithData);
            } else {
                const representantsWithData = [{ id: indexKey, ...data }];
                parentFormApiRef.current?.setFieldValue('representantsInfo', representantsWithData);
            }
        }
    };
    return (
        <MuiForm
            resourceName="personaResource"
            onDataChange={handleDataChange}
            validationErrors={currentPersonaFieldValidationErrors}
            initOnChangeRequest
            hiddenToolbar
            commonFieldComponentProps={{ size: 'small' }}
            componentProps={{ sx: { mb: 3 } }}
        >
            <NotificacioFormEnviamentPersonaFormContent interessat={interessat} />
        </MuiForm>
    );
};

const NotificacioFormEnviament: React.FC<{
    index: number;
    indexKey: number;
    handleRemove: (indexKey: number) => void;
}> = (props) => {
    const { index, indexKey, handleRemove } = props;
    const { t } = useTranslation();
    const [ambRepresentant, setAmbRepresentant] = React.useState<boolean>(false);
    const [currentEnviamentFieldValidationErrors, setCurrentEnviamentFieldValidationErrors] =
        React.useState<any[]>();
    const [currentEnviamentGlobalValidationErrors, setCurrentEnviamentGlobalValidationErrors] =
        React.useState<any[]>();
    const {
        data: parentFormData,
        fieldErrors: parentFieldErrors,
        apiRef: parentFormApiRef,
    } = useFormContext();
    React.useEffect(() => {
        const errorPrefix = 'enviamentsInfo[' + index + ']';
        const currentEnviamentFieldValidationErrors = parentFieldErrors
            ?.filter((e) => e.field.startsWith(errorPrefix) && e.field !== errorPrefix)
            .map((e) => ({ ...e, field: e.field.substring(errorPrefix.length + 1) }));
        setCurrentEnviamentFieldValidationErrors(currentEnviamentFieldValidationErrors);
        const currentEnviamentGlobalValidationErrors = parentFieldErrors
            ?.filter((e) => e.field === errorPrefix)
            .map((e) => ({ ...e, field: e.field.substring(errorPrefix.length + 1) }));
        setCurrentEnviamentGlobalValidationErrors(currentEnviamentGlobalValidationErrors);
    }, [parentFieldErrors]);
    const handleDataChange = (data: any) => {
        const enviamentsWithData = parentFormData?.enviamentsInfo?.map((e: any) =>
            e.id === indexKey ? { id: indexKey, ...data } : e
        );
        parentFormApiRef.current?.setFieldValue('enviamentsInfo', enviamentsWithData);
    };
    return (
        <Paper sx={{ px: 2, py: 1, mb: 2 }}>
            <Grid container spacing={2}>
                <Grid size={10}>
                    <Typography variant="h6">
                        {t('page.notificacio.form.enviaments.title')} {index + 1}
                    </Typography>
                </Grid>
                <Grid size={2} sx={{ textAlign: 'right' }}>
                    <IconButton onClick={() => handleRemove(indexKey)}>
                        <Icon fontSize="small" title={t('page.notificacio.form.enviaments.remove')}>
                            delete
                        </Icon>
                    </IconButton>
                </Grid>
                {currentEnviamentGlobalValidationErrors?.length ? (
                    <Grid size={12}>
                        <Alert severity="error">
                            {currentEnviamentGlobalValidationErrors.map((e) => (
                                <>
                                    {e.message}
                                    <br />
                                </>
                            ))}
                        </Alert>
                    </Grid>
                ) : null}
                <Grid size={12}>
                    <MuiForm
                        resourceName="notificacioEnviamentResource"
                        onDataChange={handleDataChange}
                        validationErrors={currentEnviamentFieldValidationErrors}
                        hiddenToolbar
                        componentProps={{ sx: { mb: 2 } }}
                        commonFieldComponentProps={{ size: 'small' }}
                    >
                        <Grid container>
                            <Grid size={12}>
                                <FormField name="serveiTipus" />
                            </Grid>
                            <Grid size={12} sx={{ mt: 1 }}>
                                <NotificacioFormEnviamentPersona interessat />
                                {ambRepresentant && (
                                    <NotificacioFormEnviamentPersona
                                        index={0}
                                        indexKey={indexKey}
                                    />
                                )}
                                <Button
                                    variant="contained"
                                    startIcon={<Icon>{ambRepresentant ? 'remove' : 'add'}</Icon>}
                                    onClick={() => setAmbRepresentant((r) => !r)}
                                    size="small"
                                >
                                    {ambRepresentant
                                        ? t('page.notificacio.form.interessats.remove')
                                        : t('page.notificacio.form.interessats.add')}
                                </Button>
                            </Grid>
                        </Grid>
                    </MuiForm>
                </Grid>
            </Grid>
        </Paper>
    );
};

const NotificacioFormEnviaments: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef: formApiRef } = useFormContext();
    const enviamentsInfo = data?.enviamentsInfo;
    React.useEffect(() => {
        const reset = !enviamentsInfo?.length;
        if (reset) {
            formApiRef.current?.setFieldValue('enviamentsInfo', [{ id: new Date().valueOf() }]);
        }
    }, [enviamentsInfo]);
    const handleAddClick = () => {
        formApiRef.current?.setFieldValue('enviamentsInfo', [
            ...(enviamentsInfo ?? []),
            { id: new Date().valueOf() },
        ]);
    };
    const handleRemoveClick = (indexKey: number) => {
        formApiRef.current?.setFieldValue(
            'enviamentsInfo',
            enviamentsInfo.filter((e: any) => e.id !== indexKey)
        );
    };
    return (
        <>
            <Typography variant="h6" sx={{ mt: 3, mb: 2, borderBottom: 1, borderColor: 'divider' }}>
                {t('page.notificacio.form.tabs.enviaments')}
            </Typography>
            {enviamentsInfo?.map((e: any, i: number) => (
                <NotificacioFormEnviament
                    key={e.id}
                    index={i}
                    indexKey={e.id}
                    handleRemove={handleRemoveClick}
                />
            ))}
            <Button
                variant="contained"
                startIcon={<Icon>add</Icon>}
                onClick={handleAddClick}
                size="small"
            >
                {t('page.notificacio.form.enviaments.add')}
            </Button>
        </>
    );
};

export default NotificacioFormEnviaments;
