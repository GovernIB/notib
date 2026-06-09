import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { MuiForm, FormField, useFormContext } from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { FormDropzoneField } from '../../components/FormDropzoneField';

const NotificacioFormDocumentContent: React.FC<{ enviamentTipus: string }> = (props) => {
    const { enviamentTipus } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();

    const sourceFieldNameMapping: any = {
        CSV: 'csv',
        UUID: 'uuid',
        ATTACHED: 'attachment',
    };
    const accept =
        enviamentTipus === 'SIR'
            ? '.jpg,.jpeg,.odt,.odp,.ods,.odg,.docx,.xlsx,.pptx,.pdf,.png,.rtf,.svg,.tiff,.txt,.xml,.xsig'
            : '.zip,.pdf';

    return (
        <FormDropzoneField
            name={sourceFieldNameMapping[data?.source]}
            accept={accept}
            textValidacio={t(
                'page.notificacio.form.documents.helperText.attachment.' +
                    (enviamentTipus === 'SIR' ? 'sir' : 'noSir')
            )}
        >
            <Grid container spacing={2}>
                <GridFormField size={6} name="source" />
                <Grid size={6}>
                    {data?.source != null && (
                        <FormField
                            name={sourceFieldNameMapping[data.source]}
                            componentProps={
                                data.source === 'ATTACHED'
                                    ? {
                                          helperText: t(
                                              'page.notificacio.form.documents.helperText.attachment.' +
                                                  (enviamentTipus === 'SIR' ? 'sir' : 'noSir')
                                          ),
                                      }
                                    : undefined
                            }
                            accept={data.source === 'ATTACHED' ? accept : undefined}
                            required
                        />
                    )}
                </Grid>
                <GridFormField size={6} name="origen" />
                <GridFormField size={6} name="validesa" />
                <GridFormField size={6} name="tipoDocumental" />
                <GridFormField size={6} name="modoFirma" />
                <GridFormField
                    size={6}
                    name="normalitzat"
                    componentProps={{
                        helperText: t('page.notificacio.form.documents.helperText.normalitzat'),
                    }}
                />
            </Grid>
        </FormDropzoneField>
    );
};

const NotificacioFormDocument: React.FC<{
    index: number;
    indexKey: number;
    handleRemove: (indexKey: number) => void;
    canDelete?: boolean;
}> = (props) => {
    const { index, indexKey, handleRemove, canDelete } = props;
    const { t } = useTranslation();
    const [currentDocumentFieldValidationErrors, setCurrentDocumentFieldValidationErrors] =
        React.useState<any[]>();
    const {
        data: parentFormData,
        fieldErrors: parentFieldErrors,
        apiRef: parentFormApiRef,
    } = useFormContext();

    React.useEffect(() => {
        const errorPrefix = 'documentsInfo[' + index + ']';
        const currentDocumentFieldValidationErrors = parentFieldErrors
            ?.filter((e) => e.field.startsWith(errorPrefix))
            .map((e) => ({ ...e, field: e.field.substring(errorPrefix.length + 1) }));
        setCurrentDocumentFieldValidationErrors(currentDocumentFieldValidationErrors);
    }, [parentFieldErrors]);

    const handleDataChange = (data: any, initial: boolean) => {
        const documentsWithData = parentFormData?.documentsInfo?.map((e: any) =>
            e.id === indexKey ? { id: indexKey, ...data } : e
        );
        parentFormApiRef.current?.setFieldValue('documentsInfo', documentsWithData);
        if (!initial) {
            parentFormApiRef.current?.setModified(true);
        }
    };

    return (
        <Paper sx={{ px: 2, py: 1, mb: 2 }}>
            <Grid container spacing={2}>
                <Grid size={10}>
                    <Typography variant="h6">
                        {t('page.notificacio.form.documents.title')} {index + 1}
                    </Typography>
                </Grid>
                <Grid size={2} sx={{ textAlign: 'right' }}>
                    {canDelete && (
                        <IconButton
                            title={t('page.notificacio.form.documents.remove')}
                            onClick={() => handleRemove(indexKey)}
                            color="error"
                        >
                            <Icon fontSize="small">delete</Icon>
                        </IconButton>
                    )}
                </Grid>
                <Grid size={12}>
                    <MuiForm
                        resourceName="documentResource"
                        onDataChange={handleDataChange}
                        validationErrors={currentDocumentFieldValidationErrors}
                        hiddenToolbar
                        componentProps={{ sx: { mb: 2 } }}
                        commonFieldComponentProps={{ size: 'small' }}
                    >
                        <NotificacioFormDocumentContent
                            enviamentTipus={parentFormData.enviamentTipus}
                        />
                    </MuiForm>
                </Grid>
            </Grid>
        </Paper>
    );
};

export const NotificacioFormDocuments: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef: formApiRef } = useFormContext();
    const documentsInfo = data?.documentsInfo;

    const handleAddClick = () => {
        formApiRef.current?.setFieldValue('documentsInfo', [
            ...(documentsInfo ?? []),
            { id: new Date().valueOf() },
        ]);
    };

    const handleRemoveClick = (indexKey: number) => {
        formApiRef.current?.setFieldValue(
            'documentsInfo',
            documentsInfo.filter((e: any) => e.id !== indexKey)
        );
    };

    return (
        <>
            <Typography variant="h6" sx={{ mt: 3, mb: 2, borderBottom: 1, borderColor: 'divider' }}>
                {t('page.notificacio.form.tabs.documents')}
            </Typography>
            {documentsInfo?.map((document: any, index: number) => (
                <NotificacioFormDocument
                    key={document.id}
                    index={index}
                    indexKey={document.id}
                    handleRemove={handleRemoveClick}
                    canDelete={documentsInfo.length > 1}
                />
            ))}
            {data?.enviamentTipus === 'SIR' && documentsInfo.length < 5 && (
                <Button
                    variant="contained"
                    startIcon={<Icon>add</Icon>}
                    onClick={handleAddClick}
                    size="small"
                >
                    {t('page.notificacio.form.documents.add')}
                </Button>
            )}
        </>
    );
};

export default NotificacioFormDocuments;
