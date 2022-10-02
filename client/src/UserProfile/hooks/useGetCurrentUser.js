import { GetCurrentUserQuery } from "../data/queries";
import { useQuery } from "@apollo/client";

export const useGetCurrentUser = () => {
    const { loading, error, data } = useQuery(GetCurrentUserQuery);

    console.log(data)

    return {
        data,
        loading,
        error,
    }
}