import BaseAxios from "axios";
import PropTypes from "prop-types";
import React, {
    createContext,
    useContext,
    useEffect,
    useState,
} from "react";
import {
    API_BASE_URL,
    LOCAL_STORAGE_ACCESS_TOKEN,
} from "../constants";
import GTag from "../GTag";
import LoadObject from "../util/LoadObject";

// global side effect to ensure cookies are passed
BaseAxios.defaults.withCredentials = true;

const ProfileLOContext = createContext(undefined);

export function ProfileProvider({ children }) {
    const [ profileLO, setProfileLO ] = useState(undefined);
    useEffect(() => {
        if (profileLO) {
            if (profileLO.hasValue()) return;
            if (!profileLO.isDone()) return;
            if (profileLO.hasError()) return;
        }
        fetch(`${API_BASE_URL}/graphql`, {
            "credentials": "include",
            "method": "POST",
            "mode": "cors",
            "headers": {
                "content-type": "application/json",
            },
            "body": JSON.stringify({
                "query": `{
                  me {
                    id,
                    email,
                    name,
                    imageUrl,
                    roles,
                  }
                }`,
            }),
        })
            .then(r => r.json())
            .then(r => {
                if (r.data) {
                    // success!
                    const me = r.data.me;
                    me.id = parseInt(me.id);
                    GTag("set", {
                        uid: me.id,
                    });
                    setProfileLO(lo =>
                        lo.setValue(me).done());
                } else {
                    // bummer
                    let msg = "Unknown Error";
                    if (r.error) { // transport-ish
                        msg = r.error;
                    } else if (r.errors && r.errors[0]) { // GraphQL
                        msg = r.errors[0].message;
                    }
                    throw new Error(msg);
                }
            })
            .catch(error => {
                setProfileLO(lo =>
                    lo.setError(error).done());
            });
        setProfileLO(lo =>
            lo ? lo.loading() : LoadObject.loading());
    }, [ profileLO ]);
    return <ProfileLOContext.Provider value={profileLO}>
        {children}
    </ProfileLOContext.Provider>;
}

ProfileProvider.propTypes = {
    children: PropTypes.node,
};

export const useProfileLO = () =>
    useContext(ProfileLOContext);

const ProfileState = {
    AUTHENTICATED: "AUTHENTICATED",
    INITIALIZING: "INITIALIZING",
    PENDING: "PENDING",
    ANONYMOUS: "ANONYMOUS",
    ERR_NO_TOKEN: "ERR_NO_TOKEN",
    ERROR: "ERROR",
};

export const useProfileState = () => {
    const lo = useProfileLO();
    if (!lo) return ProfileState.INITIALIZING;
    if (lo.hasValue()) return ProfileState.AUTHENTICATED;
    if (!lo.isDone()) return ProfileState.PENDING;
    if (lo.hasError()) {
        const message = lo.getErrorEnforcing().message;
        return ProfileState.hasOwnProperty(message)
            ? message
            : ProfileState.ERROR;
    }
    return ProfileState.ANONYMOUS;
};

export const useIsProfileInitializing = () =>
    useProfileState() === ProfileState.INITIALIZING;

export const useIsProfilePending = () =>
    useProfileState() === ProfileState.PENDING;

export const useIsAuthenticated = () =>
    useProfileState() === ProfileState.AUTHENTICATED;

export const useProfile = () =>
    useProfileLO().getValueEnforcing();

export const useProfileId = () =>
    useProfile().id;

export const useIsDeveloper = () => {
    const lo = useProfileLO();
    if (!lo.hasValue()) return false;
    if (!lo.isDone()) return false; // in-flight means "gotta wait"
    const profile = lo.getValueEnforcing();
    return profile.roles && profile.roles.indexOf("DEVELOPER") >= 0;
};

const logoutHandler = () => {
    localStorage.removeItem(LOCAL_STORAGE_ACCESS_TOKEN);
    // we need the server to close out too
    window.location = API_BASE_URL + "/oauth2/logout";
};

export const useLogoutHandler = () =>
    logoutHandler;
