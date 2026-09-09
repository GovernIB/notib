import React from 'react';
import './dir3SyncTree.css';

export type Dir3SyncNode = { codi?: string; nom: string };
export type Dir3SyncNodeColor = 'red' | 'green' | 'yellow';

export type Dir3SyncBranchProps = {
    orientation: 'left' | 'right';
    root: Dir3SyncNode | null;
    rootColor: Dir3SyncNodeColor;
    leaves: { node: Dir3SyncNode | null; color: Dir3SyncNodeColor }[];
};

const colorClass = (color: Dir3SyncNodeColor) =>
    color === 'red' ? 'dir3-bg-red' : color === 'yellow' ? 'dir3-bg-yellow' : 'dir3-bg-green';

const nodeText = (node: Dir3SyncNode | null) => (node == null ? '' : `${node.codi ? node.codi + ' - ' : ''}${node.nom}`);

export const Dir3SyncBranch: React.FC<Dir3SyncBranchProps> = (props) => {
    const { orientation, root, rootColor, leaves } = props;
    const sole = leaves.length === 1;
    return (
        <div className={`dir3-horizontal-${orientation}`}>
            <div className="dir3-wrapper">
                {root == null ? (
                    <span className="dir3-label dir3-root dir3-placeholder" />
                ) : (
                    <span className={`dir3-label dir3-root ${colorClass(rootColor)}`} title={nodeText(root)}>
                        {nodeText(root)}
                    </span>
                )}
                <div className="dir3-branch">
                    {leaves.map((leaf, i) => (
                        <div key={leaf.node?.codi ?? i} className={`dir3-entry ${sole ? 'dir3-sole' : ''}`}>
                            {leaf.node == null ? (
                                <span className="dir3-label dir3-placeholder" />
                            ) : (
                                <span className={`dir3-label ${colorClass(leaf.color)}`} title={nodeText(leaf.node)}>
                                    {nodeText(leaf.node)}
                                </span>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default Dir3SyncBranch;
