import { useTranslation } from 'react-i18next';
import { useMuiContentDialog, useCloseDialogButtons } from 'reactlib';
import NotificacioDetailDialogContent from './NotificacioDetail.tsx';

export const useNotificacioDetailDialog = () => {
    const { t } = useTranslation();
    const [dialogShow, dialogComponent] = useMuiContentDialog();
    const defaultDialogButtons = useCloseDialogButtons();
    const handleDetailButtonClick = (id: any) => {
        dialogShow(
            t('page.notificacio.detail.title.notificacio'),
            <NotificacioDetailDialogContent id={id} />,
            defaultDialogButtons,
            {
                maxWidth: 'lg',
                fullWidth: true,
            }
        ).catch(() => null);
    };
    return {
        dialogComponent,
        onDetailClick: handleDetailButtonClick,
    };
};
