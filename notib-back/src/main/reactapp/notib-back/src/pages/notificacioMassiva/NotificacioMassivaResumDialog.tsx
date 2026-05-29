import { useTranslation } from 'react-i18next';
import { useMuiContentDialog, useCloseDialogButtons } from 'reactlib';
import NotificacioMassivaResumDialogContent from './NotificacioMassivaResum.tsx';

export const useNotificacioMassivaResumDialog = () => {
    const { t } = useTranslation();
    const [dialogShow, dialogComponent] = useMuiContentDialog();
    const defaultDialogButtons = useCloseDialogButtons();
    const handleDetailButtonClick = (id: any) => {
        dialogShow(
            t('page.notificacioMassiva.detall.title'),
            <NotificacioMassivaResumDialogContent id={id} />,
            defaultDialogButtons,
            {
                maxWidth: 'lg',
                fullWidth: true,
            }
        ).catch(error => console.log(error));
    };
    return {
        dialogComponent,
        onDetailClick: handleDetailButtonClick,
    };
};
