import * as React from "react";
import {GetPlansQuery} from "../data/queries";
import {useQuery} from "@apollo/client";

export const useGetPlans = () => {
    return useQuery(GetPlansQuery)
}