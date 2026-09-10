import React from 'react';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { alpha } from '@mui/material/styles';

export type Dir3SyncNode = { codi?: string; nom: string };
export type Dir3SyncNodeColor = 'red' | 'green' | 'orange';

export type Dir3SyncBranchProps = {
    orientation: 'left' | 'right';
    root: Dir3SyncNode | null;
    rootColor: Dir3SyncNodeColor;
    leaves: { node: Dir3SyncNode | null; color: Dir3SyncNodeColor }[];
};

const ARROW_COLUMN_WIDTH = 32;

const muiColor = (color: Dir3SyncNodeColor): 'success' | 'warning' | 'error' =>
    color === 'red' ? 'error' : color === 'orange' ? 'warning' : 'success';

const nodeLabel = (node: Dir3SyncNode | null) => (node == null ? '' : `${node.codi ? node.codi + ' - ' : ''}${node.nom}`);

const NodeChip: React.FC<{ node: Dir3SyncNode | null; color: Dir3SyncNodeColor }> = ({ node, color }) => {
    if (node == null) {
        return (
            <Box
                sx={{
                    width: 22,
                    height: 22,
                    borderRadius: '50%',
                    border: '2px solid',
                    borderColor: 'divider',
                    flexShrink: 0,
                }}
            />
        );
    }
    return (
        <Chip
            label={nodeLabel(node)}
            size="small"
            title={nodeLabel(node)}
            sx={(theme) => {
                const palette = theme.palette[muiColor(color)];
                return {
                    width: '100%',
                    minWidth: 0,
                    justifyContent: 'flex-start',
                    bgcolor: alpha(palette.main, 0.16),
                    color: palette.dark,
                    border: '1px solid',
                    borderColor: alpha(palette.main, 0.35),
                    '& .MuiChip-label': { overflow: 'hidden', textOverflow: 'ellipsis', fontWeight: 'bold' },
                };
            }}
        />
    );
};

export const Dir3SyncBranch: React.FC<Dir3SyncBranchProps> = (props) => {
    const { orientation, root, rootColor, leaves } = props;
    const multi = leaves.length > 1;

    const rootBox = (
        <Box sx={{ display: 'flex', alignItems: 'center', minWidth: 0, overflow: 'hidden' }}>
            <NodeChip node={root} color={rootColor} />
        </Box>
    );

    const leavesBox = (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                gap: 0.5,
                minWidth: 0,
                overflow: 'hidden',
                pl: orientation === 'left' && multi ? 1.5 : 0,
                pr: orientation === 'right' && multi ? 1.5 : 0,
                borderLeft: orientation === 'left' && multi ? '2px solid' : 'none',
                borderRight: orientation === 'right' && multi ? '2px solid' : 'none',
                borderColor: 'divider',
            }}
        >
            {leaves.map((leaf, i) => (
                <Box key={leaf.node?.codi ?? i} sx={{ display: 'flex', alignItems: 'center', minWidth: 0 }}>
                    <NodeChip node={leaf.node} color={leaf.color} />
                </Box>
            ))}
        </Box>
    );

    // Graella amb columnes que no depenen del contingut (minmax(0, 1fr) en lloc d'amplades
    // basades en el text): garanteix que totes les branques de totes les seccions quedin
    // alineades verticalment, i que els xips s'ajustin a l'amplada disponible de la modal.
    return (
        <Box
            sx={{
                display: 'grid',
                gridTemplateColumns: `minmax(0, 1fr) ${ARROW_COLUMN_WIDTH}px minmax(0, 1fr)`,
                alignItems: 'center',
                columnGap: 1.5,
                py: 0.75,
            }}
        >
            {orientation === 'left' ? rootBox : leavesBox}
            <ArrowForwardIcon fontSize="small" sx={{ color: 'text.disabled', justifySelf: 'center' }} />
            {orientation === 'left' ? leavesBox : rootBox}
        </Box>
    );
};

export default Dir3SyncBranch;
