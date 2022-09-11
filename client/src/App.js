import React from "react";
import "./App.scss";
import DesktopApp from "./desktop/DesktopApp";
import MobileApp from "./mobile/MobileApp";
import { useIsMobile } from "./providers/IsMobile";
import { ApolloClient, InMemoryCache, ApolloProvider, gql } from '@apollo/client';

const client = new ApolloClient({
    uri: 'http://localhost:8080/graphql',
    cache: new InMemoryCache(),
});

const App = () => (<ApolloProvider client={client}>
    {useIsMobile()
        ? <MobileApp />
        : <DesktopApp />
    }
</ApolloProvider>);

export default App;
