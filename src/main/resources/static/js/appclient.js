// apiclient.js
const APIClient = (() => {
  function getBlueprintsByAuthor(author, callback) {
    $.ajax({
      url: `/api/blueprints/${author}`,
      type: "GET",
      success: function (data) {
        const plans = data.map((bp) => ({
          name: bp.name,
          points: bp.points,
        }));
        callback(plans);
      },
      error: function () {
        callback([]);
      },
    });
  }

  function getBlueprintsByNameAndAuthor(author, bpname, callback) {
    $.ajax({
      url: `/api/blueprints/${author}/${bpname}`,
      type: "GET",
      success: function (bp) {
        callback(bp.points || []);
      },
      error: function () {
        callback([]);
      },
    });
  }

  function updateBlueprint(author, bpname, blueprint) {
    return $.ajax({
      url: `/api/blueprints/${author}/${bpname}`,
      type: "PUT",
      data: JSON.stringify(blueprint),
      contentType: "application/json",
    });
  }

  function getBlueprintsByAuthorPromise(author) {
    return $.ajax({
      url: `/api/blueprints/${author}`,
      type: "GET",
    });
  }

  function createBlueprint(blueprint) {
    return $.ajax({
      url: "/api/blueprints",
      type: "POST",
      data: JSON.stringify(blueprint),
      contentType: "application/json",
    });
  }

  function deleteBlueprint(author, bpname) {
    return $.ajax({
      url: `/api/blueprints/${author}/${bpname}`,
      type: "DELETE",
    });
  }

  return {
    getBlueprintsByAuthor,
    getBlueprintsByNameAndAuthor,
    updateBlueprint,
    getBlueprintsByAuthorPromise,
    createBlueprint,
    deleteBlueprint,
  };
})();
