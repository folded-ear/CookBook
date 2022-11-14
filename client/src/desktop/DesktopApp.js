import CssBaseline from "@material-ui/core/CssBaseline";
import { ThemeProvider } from "@material-ui/core/styles";
import React from "react";
import useIsNewVersionAvailable from "data/useIsNewVersionAvailable";
import { useIsAuthenticated } from "providers/Profile";
import theme from "../theme";
import NewVersionAvailable from "../views/NewVersionAvailable";
import DesktopHeader from "./DesktopHeader";
import {
    Box,
    Container,
    Paper,
    Typography,
} from "@material-ui/core";
import { Link } from "react-router-dom";

function DesktopApp() {
    const newVersionAvailable = useIsNewVersionAvailable();
    const authenticated = useIsAuthenticated();

    return <ThemeProvider theme={theme}>
        <CssBaseline />
        {authenticated && newVersionAvailable && <NewVersionAvailable />}
        <DesktopHeader
            authenticated={authenticated}
        />
        <Container maxWidth={"sm"}>
            <Box m={2}>
                <Paper>
                    <Box p={2}>
                        <Typography variant={"h3"} component={"h1"}>
                            Brenna&apos;s Food Software
                        </Typography>
                        <Typography variant={"body1"}>
                            <p>
                                Foodinger is now <Link
                                to="https://gobrennas.com">Brenna&apos;s Food
                                Software</Link>. Your recipes and plans are
                                already waiting for you.
                            </p>
                            <p>
                                Update your bookmark, and that&apos;s it.
                            </p>
                            <p>Happy cooking!</p>
                        </Typography>
                    </Box>
                </Paper>
            </Box>
        </Container>
    </ThemeProvider>;
}

export default DesktopApp;
