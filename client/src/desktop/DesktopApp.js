import CssBaseline from "@material-ui/core/CssBaseline";
import { ThemeProvider } from "@material-ui/core/styles";
import React from "react";
import useIsNewVersionAvailable from "data/useIsNewVersionAvailable";
import theme from "../theme";
import NewVersionAvailable from "../views/NewVersionAvailable";
import DesktopHeader from "./DesktopHeader";
import {
    Box,
    Container,
    Paper,
    Typography,
} from "@material-ui/core";

function DesktopApp() {
    const newVersionAvailable = useIsNewVersionAvailable();

    return <ThemeProvider theme={theme}>
        <CssBaseline />
        {newVersionAvailable && <NewVersionAvailable />}
        <DesktopHeader
            authenticated={false}
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
                                Foodinger is now <a
                                href="https://gobrennas.com">Brenna&apos;s Food
                                Software</a>. Your recipes and plans are
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
