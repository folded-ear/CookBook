(function() {
    const GATEWAY_PROP = "foodinger_import_bookmarklet_gateway_Ch8LF4wGvsRHN3nM0jFv";
    const CONTAINER_ID = "foodinger-import-bookmarklet-container-Ch8LF4wGvsRHN3nM0jFv";
    const CONTENT_ID = CONTAINER_ID + "-content";
    const camelToDashed = n =>
        n.replace(/([a-z0-9])([A-Z])/g, (match, a, b) =>
            `${a}-${b.toLowerCase()}`);
    const toStyle = rules =>
        Object.keys(rules)
            .map(n => `${(camelToDashed(n))}:${rules[n]}`)
            .join(";");
    const containerStyle = toStyle({
        position: "fixed",
        top: 0,
        right: 0,
        zIndex: 99999,
        backgroundColor: "whitesmoke",
        border: "1px solid #bf360c",
        borderRightWidth: 0,
        borderTopWidth: 0,
        boxShadow: "0 5px 5px #d3b8ae",
        borderBottomLeftRadius: "5px",
        width: "50%",
        paddingBottom: "1em",
    });
    const headerStyle = toStyle({
        fontSize: "2rem",
        fontWeight: "bold",
        padding: "0.2em 0.4em",
        backgroundColor: "#870000",
        color: "#fff",
    });
    const renderStale = $div => {
        $div.innerHTML = `<h1 style="${headerStyle}">Update Cook This!</h1>
        <div style="margin: 0 5%">
            <p>Cook This! needs an update. Delete it, reinstall below, and then
            click it again!</p>
            <iframe width="100%" height="250" src="https://gobrennas.com/profile#cook-this" />
        </div>
        `;
    };
    const render = () => {
        let $div = document.getElementById(CONTAINER_ID);
        if ($div == null) {
            $div = document.createElement("div");
            $div.id = CONTAINER_ID;
            $div.innerHTML = `<div style="position:relative">
                <div id="${CONTENT_ID}"></div>
                <a href="#" onclick="${GATEWAY_PROP}.__close()" style="position:absolute;top:0;right:10px;font-weight:bold;font-size:200%;color:#fff;">×</a>
            </div>`;
            $div.style = containerStyle;
            document.body.append($div);
        }
        renderStale(document.getElementById(CONTENT_ID));
        window[GATEWAY_PROP] = {
            __close: () => {
                $div.parentNode.removeChild($div);
                const $script = document.getElementById(
                    "foodinger-import-bookmarklet");
                $script.parentNode.removeChild($script);
                delete window[GATEWAY_PROP];
            },
        };
    };
    render();
})();
