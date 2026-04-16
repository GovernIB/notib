import { useTranslation } from "react-i18next";
import { useCloseDialogButtons, useMuiContentDialog } from "reactlib";
import EnviamentDetailDialogContent from "./EnviamentDetail";


export const useEnviamentDetailDialog = () => {
    const { t } = useTranslation();
    const [dialogShow, dialogComponent] = useMuiContentDialog();
    const defaultDialogButtons = useCloseDialogButtons();
    const handleDetailButtonClick = (id: any) => {
        dialogShow(
            t('page.enviament.detail.title'),
            <EnviamentDetailDialogContent id={id} />,
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
