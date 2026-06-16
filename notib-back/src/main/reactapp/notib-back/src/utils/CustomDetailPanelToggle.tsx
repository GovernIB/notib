import {
    gridDetailPanelExpandedRowIdsSelector,
    gridDetailPanelExpandedRowsContentCacheSelector,
    GridRenderCellParams,
    useGridApiContext,
    useGridSelector
} from "@mui/x-data-grid-pro";
import {useTranslation} from "react-i18next";
import React from "react";
import {Icon, IconButton} from "@mui/material";

const CustomDetailPanelToggle = (props: Pick<GridRenderCellParams, 'id' | 'value'>) => {
    const { id } = props;
    const { t } = useTranslation();
    const apiRef = useGridApiContext();
    const contentCache = useGridSelector(apiRef, gridDetailPanelExpandedRowsContentCacheSelector);
    const hasDetail = React.isValidElement(contentCache[id]);
    const expandedRowIds = useGridSelector(apiRef, gridDetailPanelExpandedRowIdsSelector);
    const isExpanded = expandedRowIds.has(id);

    return (
        <IconButton
            size="small"
            tabIndex={-1}
            disabled={!hasDetail}
            title={isExpanded ? t('page.notificacio.grid.column.ocultar') : t('page.notificacio.grid.column.mostrar')}
            aria-label={isExpanded ? t('page.notificacio.grid.column.ocultar') : t('page.notificacio.grid.column.mostrar')}
        >
            <Icon
                sx={(theme) => ({
                    transform: `rotateZ(${isExpanded ? 180 : 0}deg)`,
                    transition: theme.transitions.create('transform', {
                        duration: theme.transitions.duration.shortest,
                    }),
                })}
                fontSize="inherit"
            >
                expand_more
            </Icon>
        </IconButton>
    );
};

export default CustomDetailPanelToggle
