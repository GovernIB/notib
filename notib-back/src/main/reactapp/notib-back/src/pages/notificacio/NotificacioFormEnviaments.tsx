import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Alert from '@mui/material/Alert';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {MuiForm, useFormApiRef, useFormContext, useResourceApiService} from 'reactlib';
import Dir3SearchInput from '../../components/Dir3SearchInput';
import GridFormField from '../../components/GridFormField';

const NotificacioFormEnviamentPersonaFormContent: React.FC<{ interessat?: boolean }> = (props) => {
    const { interessat } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    return (
        <Grid container spacing={2}>
            <Grid size={12} sx={{ mb: 1 }}>
                {t('page.notificacio.form.interessats.' + (interessat ? 'interessat' : 'representant'))}
            </Grid>
            <GridFormField size={6} name="interessatTipus" />
            {data.visibleDocumentTipus && <GridFormField size={6} name="documentTipus" />}
            {data.visibleNif && (
                <GridFormField
                    size={6}
                    name="nif"
                    label={t('page.notificacio.form.interessats.nifLabel.' + data?.interessatTipus)}
                    required={data?.requiredNif}
                />
            )}
            {data.visibleNom && <GridFormField size={6} name="nom" required={data.requiredNom} />}
            {data.visibleLlinatge1 && (<GridFormField size={6} name="llinatge1" required={data.requiredLlinatge1} />)}
            {data.visibleLlinatge2 && <GridFormField size={6} name="llinatge2" />}
            {data.visibleRaoSocial && (<GridFormField size={6} name="raoSocial" required={data.requiredRaoSocial} />)}
            {data.visibleDir3Codi && (<GridFormField size={6} name="dir3Codi" required={data.requiredDir3Codi} />)}
            {data.visibleTelefon && <GridFormField size={6} name="telefon" />}
            {data.visibleEmail && (<GridFormField size={6} name="email" required={data.requiredEmail} />)}
            {data.visibleIncapacitat && <GridFormField size={6} name="incapacitat" />}
        </Grid>
    );
};

const NotificacioFormEnviamentPersona: React.FC<{ index?: number; indexKey?: number; interessat?: boolean; }> = (props) => {

    const { index, indexKey, interessat } = props;
    const [currentPersonaFieldValidationErrors, setCurrentPersonaFieldValidationErrors] = React.useState<any[]>();
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

const   NotificacioFormEnviamentEntregaPostal: React.FC<{ forceEntregaPostalActiva: boolean; }> = ({ forceEntregaPostalActiva }) => {

    const [currentPersonaFieldValidationErrors, setCurrentPersonaFieldValidationErrors] = React.useState<any[]>();
    const { fieldErrors: parentFieldErrors, apiRef: parentFormApiRef,} = useFormContext();
    const { t } = useTranslation();
    const formApiRef = useFormApiRef();
    const { data } = useFormContext()
    let entregaPostalActiva = data.entregaPostalActiva;
    React.useEffect(() => {
        if (forceEntregaPostalActiva) {
            formApiRef.current?.setFieldValue('entregaPostalActiva', true);
        }
    }, [forceEntregaPostalActiva, formApiRef]);
    React.useEffect(() => {
        const errorPrefix = 'entregaPostalInfo';
        const currentPersonaFieldValidationErrors = parentFieldErrors
            ?.filter((e) => e.field.startsWith(errorPrefix))
            .map((e) => ({ ...e, field: e.field.substring(errorPrefix.length + 1) }));
        setCurrentPersonaFieldValidationErrors(currentPersonaFieldValidationErrors);
    }, [parentFieldErrors]);

    const handleDataChange = (data: any) => {
        parentFormApiRef.current?.setFieldValue('entregaPostalInfo', data);
    };
    return (forceEntregaPostalActiva &&
        <>
            <GridFormField size={12} name="entregaPostalActiva"/>
            {entregaPostalActiva &&
                <>
                    <Grid size={12}>
                        <Typography variant="h6" sx={{mt: 3, mb: 2, borderBottom: 1, borderColor: 'divider'}}>
                            {t('page.notificacio.form.tabs.metodesEnviament')}
                        </Typography>
                    </Grid>

                    <Grid size={12}>
                <MuiForm
                    resourceName="entregaPostalResource"
                    onDataChange={handleDataChange}
                    validationErrors={currentPersonaFieldValidationErrors}
                    initOnChangeRequest
                    hiddenToolbar
                    commonFieldComponentProps={{size: 'small'}}
                    componentProps={{sx: {mb: 3}}}
                >
                    <EntregaPostalFields />
                </MuiForm>
                    </Grid>
                </>
            }
        </>
    );
};

const EntregaPostalFields = () => {

    const { data } = useFormContext();
    const senseNormalitzar = data?.domiciliConcretTipus === 'SENSE_NORMALITZAR';

    return (
        <Grid container spacing={2}  sx={{
            width: '100%',
            minWidth: 0,
        }}>
            <GridFormField size={4} name="domiciliConcretTipus" />
            <Grid size={6}></Grid>
            {senseNormalitzar ? (
                <>
                    <GridFormField size={12} name="linea1" />
                    <GridFormField size={12} name="linea2" />
                    <GridFormField size={12} name="codiPostalNorm" />
                </>
            ) : (
                <>
                    <GridFormField size={4} name="viaTipus" />
                    <GridFormField size={8} name="viaNom" />
                    <GridFormField size={4} name="apartatCorreus" />
                    <GridFormField size={4} name="numeroCasa" />
                    <GridFormField size={4} name="puntKm" />
                    <GridFormField size={4} name="portal" />
                    <GridFormField size={4} name="escala" />
                    <GridFormField size={4} name="planta" />
                    <GridFormField size={4} name="porta" />
                    <GridFormField size={4} name="bloc" />
                    <GridFormField size={4} name="codiPostal" />
                    <GridFormField size={6} name="paisCodi" />
                    <GridFormField size={6} name="provincia" />
                    <GridFormField size={6} name="municipiCodi" />
                    <GridFormField size={6} name="poblacio" />
                    <GridFormField size={12} name="complement" />
                </>
            )}
        </Grid>
    );
};

const NotificacioFormEnviament: React.FC<{ index: number; indexKey: number; handleRemove: (indexKey: number) => void; canDelete?: boolean; }> = (props) => {

    const { index, indexKey, handleRemove, canDelete } = props;
    const { t } = useTranslation();
    const [titularInitialized, setTitularInitialized] = React.useState<boolean>(false);
    const [ambRepresentant, setAmbRepresentant] = React.useState<boolean>(false);
    const [currentEnviamentFieldValidationErrors, setCurrentEnviamentFieldValidationErrors] = React.useState<any[]>();
    const [currentEnviamentGlobalValidationErrors, setCurrentEnviamentGlobalValidationErrors] = React.useState<any[]>();
    const {data: parentFormData, fieldErrors: parentFieldErrors, apiRef: parentFormApiRef,} = useFormContext();
    const {isReady: apiIsReady, getOne: apiGetOne,} = useResourceApiService('procedimentResource');

    const [forceEntregaPostalActiva, setForceEntregaPostalActiva] = React.useState(false);

    React.useEffect(() => {

        const procediment = parentFormData?.procediment;
        if (!apiIsReady || !procediment?.id) {
            setForceEntregaPostalActiva(false);
            return;
        }
        apiGetOne(procediment.id).then((resposta: any) => {
            setForceEntregaPostalActiva(Boolean(true));
            // setForceEntregaPostalActiva(Boolean(resposta?.entregaPostalActiva));
        }).catch((error: any) => {
            console.error(error);
            setForceEntregaPostalActiva(false);
        });
    }, [apiIsReady, apiGetOne, parentFormData?.procediment?.id]);

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

    const handleDataChange = (data: any, initial: boolean) => {
        const enviamentsWithData = parentFormData?.enviamentsInfo?.map((e: any) =>
            e.id === indexKey ? { id: indexKey, ...data } : e
        );
        parentFormApiRef.current?.setFieldValue('enviamentsInfo', enviamentsWithData);
        if (!initial) {
            if (titularInitialized) {
                parentFormApiRef.current?.setModified(true);
            }
            setTitularInitialized(data.titularInfo != null);
        }
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
                    {canDelete && (
                        <IconButton
                            title={t('page.notificacio.form.enviaments.remove')}
                            onClick={() => handleRemove(indexKey)}
                            color="error"
                        >
                            <Icon fontSize="small">delete</Icon>
                        </IconButton>
                    )}
                </Grid>
                {currentEnviamentGlobalValidationErrors?.length ? (
                    <Grid size={12}>
                        <Alert severity="error">
                            {currentEnviamentGlobalValidationErrors.map((e, i) => (
                                <React.Fragment key={i}>
                                    {e.message}
                                    <br />
                                </React.Fragment>
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
                        <Grid container spacing={2}>
                            <GridFormField size={12} name="serveiTipus" />
                            {parentFormData?.enviamentTipus === 'SIR' && (
                                <Grid size={12}>
                                    <Dir3SearchInput name="sirTitularDir3Codi" required />
                                </Grid>
                            )}
                            {parentFormData?.enviamentTipus !== 'SIR' && (
                                <Grid size={12} sx={{ mt: 1 }}>
                                    <NotificacioFormEnviamentPersona interessat />
                                    {ambRepresentant && (<NotificacioFormEnviamentPersona index={0} indexKey={indexKey}/>)}
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
                            )}
                            <GridFormField size={12} name="ambEntregaDeh" />
                           <NotificacioFormEnviamentEntregaPostal forceEntregaPostalActiva={forceEntregaPostalActiva}/>

                        </Grid>
                    </MuiForm>
                </Grid>
            </Grid>
        </Paper>
    );
};

export const NotificacioFormEnviaments: React.FC = () => {

    const { t } = useTranslation();
    const { data, apiRef: formApiRef } = useFormContext();
    const enviamentsInfo = data?.enviamentsInfo;

    const handleAddClick = () => {
        formApiRef.current?.setFieldValue('enviamentsInfo', [...(enviamentsInfo ?? []), { id: new Date().valueOf() },]);
    };
    const handleRemoveClick = (indexKey: number) => {
        formApiRef.current?.setFieldValue('enviamentsInfo', enviamentsInfo.filter((e: any) => e.id !== indexKey));
    };

    return (
        <>
            <Typography variant="h6" sx={{ mt: 3, mb: 2, borderBottom: 1, borderColor: 'divider' }}>
                {t('page.notificacio.form.tabs.enviaments')}
            </Typography>
            {enviamentsInfo?.map((enviament: any, index: number) => (
                <NotificacioFormEnviament
                    key={enviament.id}
                    index={index}
                    indexKey={enviament.id}
                    handleRemove={handleRemoveClick}
                    canDelete={enviamentsInfo.length > 1}
                />
            ))}
            {data?.enviamentTipus !== 'SIR' && (
                <Button
                    variant="contained"
                    startIcon={<Icon>add</Icon>}
                    onClick={handleAddClick}
                    size="small"
                >
                    {t('page.notificacio.form.enviaments.add')}
                </Button>
            )}
        </>
    );
};

export default NotificacioFormEnviaments;
