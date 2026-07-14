import {
    gridDetailPanelExpandedRowIdsSelector,
    gridDetailPanelExpandedRowsContentCacheSelector,
    GridRenderCellParams,
    useGridApiContext,
    useGridSelector
} from "@mui/x-data-grid-pro";
import React from "react";
import {Icon, IconButton} from "@mui/material";


type CustomDetailPanelToggleProps = Pick<GridRenderCellParams, 'id' | 'value'> & { msgMostrar: string; msgOcultar: string; };


const CustomDetailPanelToggle = ({id, value, msgMostrar, msgOcultar}: CustomDetailPanelToggleProps) => {

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
            title={isExpanded ? msgOcultar : msgMostrar}
            aria-label={isExpanded ? msgOcultar : msgMostrar}
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
