import React from "react";
import {MuiDataGridApiRef} from "reactlib";
import Button from "@mui/material/Button";
import {Icon} from "@mui/material";

const AddButton: React.FC<{ gridApiRef: MuiDataGridApiRef, text: string, tooltip: string  }> = ({ gridApiRef, text, tooltip }) => {

    return (
        <Button title={tooltip} onClick={() => gridApiRef.current?.triggerCreate()}>
            <Icon fontSize='small'>add</Icon>{text}
        </Button>
    );
};

export default AddButton;
