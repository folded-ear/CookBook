import {gql} from "@apollo/client";

export const GetCurrentUserQuery = gql`
    query GetCurrentUser {
        id
        name
        email
        imageUrl
        provider
    }
`;