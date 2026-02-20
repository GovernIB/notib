import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { MuiForm, FormField, useFormContext } from 'reactlib';

const NotificacioFormDocumentContent: React.FC = () => {
    const { t } = useTranslation();
    const { data } = useFormContext();
    const sourceFieldNameMapping: any = {
        CSV: 'csv',
        UUID: 'uuid',
        ATTACHED: 'attachment',
    };
    return (
        <Grid container spacing={2}>
            <Grid size={6}>
                <FormField name="source" />
            </Grid>
            <Grid size={6}>
                {data?.source != null && (
                    <FormField
                        name={sourceFieldNameMapping[data.source]}
                        componentProps={
                            data.source === 'ATTACHED'
                                ? {
                                      helperText: t(
                                          'page.notificacio.form.documents.helperText.attachment'
                                      ),
                                  }
                                : undefined
                        }
                        accept={data.source === 'ATTACHED' ? '.zip,.pdf' : undefined}
                        required
                    />
                )}
            </Grid>
            <Grid size={6}>
                <FormField name="origen" />
            </Grid>
            <Grid size={6}>
                <FormField name="validesa" />
            </Grid>
            <Grid size={6}>
                <FormField name="tipoDocumental" />
            </Grid>
            <Grid size={6}>
                <FormField name="modoFirma" />
            </Grid>
            <Grid size={6}>
                <FormField
                    name="normalitzat"
                    componentProps={{
                        helperText: t('page.notificacio.form.documents.helperText.normalitzat'),
                    }}
                />
            </Grid>
        </Grid>
    );
};

const NotificacioFormDocument: React.FC<{
    index: number;
    indexKey: number;
    handleRemove: (indexKey: number) => void;
}> = (props) => {
    const { index, indexKey, handleRemove } = props;
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
    const handleDataChange = (data: any) => {
        const documentsWithData = parentFormData?.documentsInfo?.map((e: any) =>
            e.id === indexKey ? { id: indexKey, ...data } : e
        );
        parentFormApiRef.current?.setFieldValue('documentsInfo', documentsWithData);
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
                    <IconButton onClick={() => handleRemove(indexKey)}>
                        <Icon fontSize="small" title={t('page.notificacio.form.documents.remove')}>
                            delete
                        </Icon>
                    </IconButton>
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
                        <NotificacioFormDocumentContent />
                    </MuiForm>
                </Grid>
            </Grid>
        </Paper>
    );
};

const NotificacioFormDocuments: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef: formApiRef } = useFormContext();
    const documentsInfo = data?.documentsInfo;
    React.useEffect(() => {
        const reset = !documentsInfo?.length;
        if (reset) {
            formApiRef.current?.setFieldValue('documentsInfo', [{ id: new Date().valueOf() }]);
        }
    }, [documentsInfo]);
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
            {documentsInfo?.map((e: any, i: number) => (
                <NotificacioFormDocument
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
                {t('page.notificacio.form.documents.add')}
            </Button>
        </>
    );
};

export default NotificacioFormDocuments;
