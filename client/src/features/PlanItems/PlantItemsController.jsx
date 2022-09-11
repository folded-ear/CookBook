import * as React from "react";
import {useGetPlans} from "./hooks/useGetPlans";
import {PlanItemsList} from "./components/PlanItemsList";
import LoadingIndicator from "../../views/common/LoadingIndicator";

export const PlantItemsController = () => {
    const { loading, error, data } = useGetPlans();

    console.log(data)

    if(loading) {
        return <LoadingIndicator
            primary="Loading task lists..."
        />;
    }

    if(error) {
        return (<div>Oops...something terrible happened.</div>);
    }

    return (<div>
        <PlanItemsList planItems={[{name: "one"},{name: "two"}]} />
    </div>)
}