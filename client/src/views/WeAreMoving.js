import React from "react";
import Banner from "views/common/Banner";

const NewVersionAvailable = () => {
    const [ hidden, setHidden ] = React.useState(false);
    return hidden ? null : <Banner
        severity="info"
        onClose={() => setHidden(true)}
    >
        Foodinger is becoming Brenna&apos;s Food Software! Your data are safe.
        More details to come...
    </Banner>;
};

export default NewVersionAvailable;
