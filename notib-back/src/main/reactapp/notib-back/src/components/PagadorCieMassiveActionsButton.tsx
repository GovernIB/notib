import React from 'react';
import { useTranslation } from 'react-i18next';
import { useMuiDataGridContext, MuiActionReportButton, useBaseAppContext } from "reactlib";

const PagadorCieBorradoMasivoButton: React.FC<{ refresh: () => void }> = ({ refresh }) => {
    const { t } = useTranslation();
    const { selection } = useMuiDataGridContext();
    const { temporalMessageShow } = useBaseAppContext();
    const ids = selection?.ids ?? new Set();
    return (
        <MuiActionReportButton
            resourceName="pagadorCieResource"
            action="ESBORRAR_MASSIU"
            confirm={true}
            selectedCount={ids.size}
            formAdditionalData={{ ids: Array.from(ids) }}
            title={t('page.pagadorCie.actions.esborrar.button')}
            confirmMessage={t('page.pagadorCie.actions.esborrar.title')}
            buttonIcon="delete"
            onSuccess={() => {
                temporalMessageShow(null, t('page.pagadorCie.actions.esborrar.ok'), 'success');
                refresh();
            }}
            onError={(error: any) => {
                temporalMessageShow(null, error?.message || t('page.pagadorCie.actions.esborrar.error'), 'error');
            }}
            disabled={ids.size === 0 || undefined}
            formDialogDisabled
        />
    );
};

export default PagadorCieBorradoMasivoButton;
