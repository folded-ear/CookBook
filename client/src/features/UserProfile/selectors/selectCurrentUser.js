import * as React from "react";

export const selectCurrentUser = (data) => {
    if(!data) {
        return null
    }
    const {getCurrentUser: currentUser} = data;

    return {
        currentUser
    };
}