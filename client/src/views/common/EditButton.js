import React from "react";
import PropTypes from "prop-types";
import {IconButton, Tooltip} from "@material-ui/core";
import {Edit} from "@material-ui/icons";

const EditButton = ({onClick}) => {
    return (
        <Tooltip
            title="Edit"
            placement="top"
        >
            <IconButton
                onClick={onClick}
            >
                <Edit/>
            </IconButton>
        </Tooltip>
    );
};

EditButton.propTypes = {
    onClick: PropTypes.func,
};

export default EditButton;
