import React from "react";
import "./App.scss";
import DesktopApp from "./desktop/DesktopApp";
import MobileApp from "./mobile/MobileApp";
import { useIsMobile } from "./providers/IsMobile";
import {ApolloClient, InMemoryCache, ApolloProvider, gql, createHttpLink} from '@apollo/client';

const link = createHttpLink({
    uri: 'http://localhost:8080/graphql',
    credentials: 'include'
});

const client = new ApolloClient({
    cache: new InMemoryCache(),
    link
});

const App = () => (<ApolloProvider client={client}>
    {useIsMobile()
        ? <MobileApp />
        : <DesktopApp />
    }
</ApolloProvider>);

export default App;
