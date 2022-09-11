import * as React from "react";

export const PlanItemsList = ({planItems}) => {
    return (<ul>
        {planItems.map(item => <li key={item.name}>{item.name}</li>)}
    </ul>)
}