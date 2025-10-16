// apimock.js
const APIMock = (() => {
    const blueprintsData = {
        "Alice": [
            { name: "Plano1", points: [{x:10,y:10},{x:50,y:50},{x:80,y:20}] },
            { name: "Plano2", points: [{x:20,y:30},{x:60,y:80}] }
        ],
        "Bob": [
            { name: "Casa", points: [{x:5,y:5},{x:5,y:50},{x:50,y:50},{x:50,y:5},{x:5,y:5}] },
            { name: "Puente", points: [{x:0,y:0},{x:20,y:40},{x:40,y:20}] }
        ]
    };

    function getBlueprintsByAuthor(author, callback) {
        if (blueprintsData[author]) {
            callback(blueprintsData[author]);
        } else {
            callback([]);
        }
    }

    function getBlueprintsByNameAndAuthor(author, bpname, callback) {
        const plans = blueprintsData[author] || [];
        const bp = plans.find(p => p.name === bpname);
        if (bp) callback(bp.points);
        else callback([]);
    }

    return {
        getBlueprintsByAuthor,
        getBlueprintsByNameAndAuthor
    };
})();
