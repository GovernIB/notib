import { useTranslation } from 'react-i18next';
import { useMuiContentDialog, useCloseDialogButtons } from 'reactlib';
import NotificacioDetailDialogContent from './NotificacioDetail.tsx';
import RemesesErrorRegistreDialogContent from "./RemesesErrorRegistreDetail.tsx";

export const useNotificacioDetailDialog = (notificacionsEsborrades: boolean) => {

    const { t } = useTranslation();
    const [dialogShow, dialogComponent] = useMuiContentDialog();
    const defaultDialogButtons = useCloseDialogButtons();
    const handleDetailButtonClick = (id: any) => {
        dialogShow(t('page.notificacio.detail.title.notificacio'),
            <NotificacioDetailDialogContent id={id} notificacionsEsborrades={notificacionsEsborrades}/>,
            defaultDialogButtons, { maxWidth: 'lg', fullWidth: true,}
        ).catch(() => null);
    };
    return { dialogComponent, onDetailClick: handleDetailButtonClick };
};

export const useRemesesErrorRegistreDetailDialog = () => {

    const { t } = useTranslation();
    const [dialogShow, dialogComponentErrorRegistre] = useMuiContentDialog();
    const defaultDialogButtons = useCloseDialogButtons();
    const handleDetailButtonClick = (id: any) => {
        dialogShow(t('page.notificacio.detail.title.erroRegistre'),
            <RemesesErrorRegistreDialogContent id={id} />,
            defaultDialogButtons, { maxWidth: 'lg', fullWidth: true,}
        ).catch(() => null);
    };
    return { dialogComponentErrorRegistre, onDetailClickErrorRegistre: handleDetailButtonClick };
}
