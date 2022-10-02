import React from "react";
import "./App.scss";
import DesktopApp from "./desktop/DesktopApp";
import MobileApp from "./mobile/MobileApp";
import { useIsMobile } from "./providers/IsMobile";
import {ApolloClient, InMemoryCache, ApolloProvider, gql, createHttpLink} from '@apollo/client';

const App = () =>
    useIsMobile()
        ? <MobileApp />
        : <DesktopApp />;

export default App;
