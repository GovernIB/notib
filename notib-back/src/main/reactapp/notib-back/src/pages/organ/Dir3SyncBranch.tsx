import React from 'react';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';

export type Dir3SyncNode = { codi?: string; nom: string };
export type Dir3SyncNodeColor = 'red' | 'green' | 'yellow';

export type Dir3SyncBranchProps = {
    orientation: 'left' | 'right';
    root: Dir3SyncNode | null;
    rootColor: Dir3SyncNodeColor;
    leaves: { node: Dir3SyncNode | null; color: Dir3SyncNodeColor }[];
};

const muiColor = (color: Dir3SyncNodeColor): 'success' | 'warning' | 'error' =>
    color === 'red' ? 'error' : color === 'yellow' ? 'warning' : 'success';

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
            color={muiColor(color)}
            variant="outlined"
            size="small"
            title={nodeLabel(node)}
            sx={{
                maxWidth: 340,
                '& .MuiChip-label': { overflow: 'hidden', textOverflow: 'ellipsis' },
            }}
        />
    );
};

export const Dir3SyncBranch: React.FC<Dir3SyncBranchProps> = (props) => {
    const { orientation, root, rootColor, leaves } = props;
    const multi = leaves.length > 1;
    return (
        <Box
            sx={{
                display: 'flex',
                alignItems: 'center',
                gap: 1.5,
                py: 0.75,
                flexDirection: orientation === 'left' ? 'row' : 'row-reverse',
            }}
        >
            <Box sx={{ flexShrink: 0 }}>
                <NodeChip node={root} color={rootColor} />
            </Box>
            <ArrowForwardIcon
                fontSize="small"
                sx={{ color: 'text.disabled', flexShrink: 0, transform: orientation === 'right' ? 'scaleX(-1)' : 'none' }}
            />
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: 0.5,
                    pl: orientation === 'left' && multi ? 1.5 : 0,
                    pr: orientation === 'right' && multi ? 1.5 : 0,
                    borderLeft: orientation === 'left' && multi ? '2px solid' : 'none',
                    borderRight: orientation === 'right' && multi ? '2px solid' : 'none',
                    borderColor: 'divider',
                }}
            >
                {leaves.map((leaf, i) => (
                    <Box key={leaf.node?.codi ?? i} sx={{ display: 'flex', alignItems: 'center' }}>
                        <NodeChip node={leaf.node} color={leaf.color} />
                    </Box>
                ))}
            </Box>
        </Box>
    );
};

export default Dir3SyncBranch;
