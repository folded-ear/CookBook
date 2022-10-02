import * as React from "react";
import preval from "preval.macro";
import Divider from "@material-ui/core/Divider";
import {useGetCurrentUser} from "./hooks/useGetCurrentUser";
import useWindowSize from "../../data/useWindowSize";
import User from "../../views/user/User";
import {selectCurrentUser} from "./selectors/selectCurrentUser";
import {useAuthToken} from "../../providers/AuthToken";
import {useIsDeveloper} from "../../providers/Profile";
import {Developer} from "./components/Developer";
import {CookThis} from "./components/CookThis";
import LoadingIndicator from "../../views/common/LoadingIndicator";
import {Profile} from "./components/Profile";

export const UserProfileController = () => {
    const { data, loading, error} =  useGetCurrentUser();

    const {currentUser} = selectCurrentUser(data);
    const isDeveloper = useIsDeveloper();

    const isLoadingUserProfile = loading || !currentUser

    if(isLoadingUserProfile) {
        return <LoadingIndicator />
    }

    if(error) {
        return <div>Oops, something went wrong.</div>
    }

    return (
        <div>
            <Profile currentUser={currentUser} />
            <Divider />
            <CookThis />
            <Divider />
            <div>
                <User {...currentUser} />
            </div>
            {isDeveloper && <Developer />}
        </div>
    );
}

