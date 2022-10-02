import * as React from "react";
import preval from "preval.macro";
import Divider from "@material-ui/core/Divider";
import Switch from "@material-ui/core/Switch";
import {useGetCurrentUser} from "./hooks/useGetCurrentUser";
import UserActions from "../../data/UserActions";
import useWindowSize from "../../data/useWindowSize";
import useIsDevMode from "../../data/useIsDevMode";
import Dispatcher from "../../data/LibraryStore";

export const UserProfileController = () => {
    const { data, loading, error} =  useGetCurrentUser();

    const dateTimeStamp = preval`module.exports = new Date().toISOString();`;

    const DevMode = () => {
        const windowSize = useWindowSize();
        return <React.Fragment>
            <p>
                Window: {windowSize.width}x{windowSize.height}
            </p>
            <p>
                Build: {dateTimeStamp}
            </p>
        </React.Fragment>;
    };

    const Developer = () => {
        const isDevMode = useIsDevMode();

        const handleDevModeChange = (e) => {
            Dispatcher.dispatch({
                type: UserActions.SET_DEV_MODE,
                enabled: e.target.checked,
            });
        };

        return <React.Fragment>
            <Divider />
            Dev Mode:
            {" "}
            <Switch
                checked={isDevMode}
                onChange={handleDevModeChange}
                color="primary"
            />
            {isDevMode && <DevMode />}
        </React.Fragment>;
    };
    return <div>hello</div>
}

