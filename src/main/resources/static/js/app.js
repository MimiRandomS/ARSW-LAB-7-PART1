// app.js
const BlueprintApp = (() => {
  let currentBlueprint = { name: "", points: [] };
  let selectedAuthor = "";
  let blueprintsList = [];
  const ActiveAPI = typeof APIClient !== "undefined" ? APIClient : APIMock;

  function setSelectedAuthor(author) {
    selectedAuthor = author;
  }

  function getSelectedAuthor() {
    return selectedAuthor;
  }
  function updateBlueprints(author) {
    setSelectedAuthor(author);

    $("#blueprintsTable tbody").empty();
    $("#selectedAuthor").text("");
    $("#totalPoints").text("0");
    $("#selectedBlueprintName").text("");

    ActiveAPI.getBlueprintsByAuthor(author, (plans) => {
      if (plans.length === 0) {
        alert(`No se encontraron planos para el autor "${author}".`);
        return;
      }

      blueprintsList = plans.map((p) => ({
        name: p.name,
        points: p.points.length,
      }));

      blueprintsList.forEach((bp) => {
        $("#blueprintsTable tbody").append(`
                <tr>
                    <td>${bp.name}</td>
                    <td>${bp.points}</td>
                    <td>
                        <button class="btn btn-sm btn-primary openBlueprintBtn" data-name="${bp.name}">
                            Abrir
                        </button>
                    </td>
                </tr>
            `);
      });

      const totalPoints = blueprintsList
        .map((bp) => bp.points)
        .reduce((a, b) => a + b, 0);
      $("#totalPoints").text(totalPoints);

      $("#selectedAuthor").text(author);
    });
  }
  function drawBlueprint(author, blueprintName) {
    ActiveAPI.getBlueprintsByNameAndAuthor(author, blueprintName, (points) => {
      const canvas = document.getElementById("blueprintCanvas");
      const ctx = canvas.getContext("2d");
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      currentBlueprint = { name: blueprintName, points: points.slice() };

      if (points.length > 0) {
        ctx.beginPath();
        ctx.moveTo(points[0].x, points[0].y);
        points.slice(1).forEach((p) => ctx.lineTo(p.x, p.y));
        ctx.stroke();
      }

      $("#selectedBlueprintName").text(blueprintName);
    });
  }

  function setupCanvas() {
    const canvas = document.getElementById("blueprintCanvas");
    const ctx = canvas.getContext("2d");

    canvas.addEventListener("pointerdown", (e) => {
      if (!currentBlueprint.name) return; // no hay plano abierto

      const rect = canvas.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;

      currentBlueprint.points.push({ x, y });
      redrawCurrentBlueprint(ctx);
    });
  }

  function redrawCurrentBlueprint(ctx) {
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    if (currentBlueprint.points.length > 0) {
      ctx.beginPath();
      ctx.moveTo(currentBlueprint.points[0].x, currentBlueprint.points[0].y);
      currentBlueprint.points.slice(1).forEach((p) => ctx.lineTo(p.x, p.y));
      ctx.stroke();
    }
  }

  function updateBlueprintsTable(plans) {
  $("#blueprintsTable tbody").empty();

  blueprintsList = plans.map((p) => ({
    name: p.name,
    points: p.points.length,
  }));

  blueprintsList.forEach((bp) => {
    $("#blueprintsTable tbody").append(`
      <tr>
        <td>${bp.name}</td>
        <td>${bp.points}</td>
        <td>
          <button class="btn btn-sm btn-primary openBlueprintBtn" data-name="${bp.name}">
            Abrir
          </button>
        </td>
      </tr>
    `);
  });

  const totalPoints = blueprintsList
    .map((bp) => bp.points)
    .reduce((a, b) => a + b, 0);
  $("#totalPoints").text(totalPoints);
}


  function saveBlueprint() {
    const author = getSelectedAuthor();
    const bpname = currentBlueprint.name;
    if (!author || !bpname) {
      alert("Seleccione un autor y un plano primero.");
      return;
    }

    const blueprintData = {
      author,
      name: bpname,
      points: currentBlueprint.points,
    };

    ActiveAPI.updateBlueprint(author, bpname, blueprintData)
      .then(() => {
        alert("Blueprint actualizado correctamente.");
        return ActiveAPI.getBlueprintsByAuthorPromise(author);
      })
      .then((plans) => updateBlueprintsTable(plans))
      .catch(() => {
        // Si falla el PUT, intenta POST (nuevo blueprint)
        return ActiveAPI.createBlueprint(blueprintData)
          .then(() => ActiveAPI.getBlueprintsByAuthorPromise(author))
          .then((plans) => updateBlueprintsTable(plans))
          .then(() => alert("Blueprint creado correctamente."));
      });
  }

  function deleteBlueprint() {
    const author = getSelectedAuthor();
    const bpname = currentBlueprint.name;
    if (!author || !bpname) {
      alert("Seleccione un plano primero.");
      return;
    }

    if (!confirm(`¿Seguro que desea eliminar el plano "${bpname}"?`)) return;

    ActiveAPI.deleteBlueprint(author, bpname)
      .then(() => ActiveAPI.getBlueprintsByAuthorPromise(author))
      .then((plans) => {
        updateBlueprintsTable(plans);
        const ctx = document.getElementById("blueprintCanvas").getContext("2d");
        ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
        currentBlueprint = { name: "", points: [] };
        $("#selectedBlueprintName").text("");
      })
      .catch((err) => console.error("Error eliminando:", err));
  }
  $(document).on("click", "#deleteBtn", deleteBlueprint);

  function createNewBlueprint() {
    const author = getSelectedAuthor();
    if (!author) {
      alert("Seleccione un autor primero.");
      return;
    }

    const name = prompt("Ingrese el nombre del nuevo plano:");
    if (!name) return;

    currentBlueprint = { name, points: [] };
    const ctx = document.getElementById("blueprintCanvas").getContext("2d");
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

    alert(
      `Plano "${name}" creado. Dibuja y luego presiona Save/Update para guardar.`
    );
  }
  $(document).on("click", "#newBlueprintBtn", createNewBlueprint);

  $(document).ready(() => {
    setupCanvas();
    $("#getBlueprintsBtn").on("click", () => {
      const author = $("#authorInput").val().trim();
      if (!author) {
        alert("Debe ingresar un nombre de autor.");
        return;
      }
      updateBlueprints(author);
    });

    $(document).on("click", ".openBlueprintBtn", function () {
      const blueprintName = $(this).data("name");
      const author = getSelectedAuthor();
      drawBlueprint(author, blueprintName);
    });
  });

  $(document).on("click", "#saveBtn", saveBlueprint);

  return {
    setSelectedAuthor,
    getSelectedAuthor,
    updateBlueprints,
    drawBlueprint,
  };
})();
