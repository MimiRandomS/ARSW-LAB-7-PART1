# 🏗️ Escuela Colombiana de Ingeniería

## ARSW

### Construcción de un cliente 'grueso' con un API REST, HTML5, Javascript y CSS3 (Parte II)

**Autores:**

* 👨‍💻 *Geronimo Martínez Nuñez*
* 👨‍💻 *Sergio Andrey Silva Rodríguez*

---

## 🧩 Descripción general

Este proyecto implementa un cliente web modular para la **gestión de blueprints (planos)**, consumiendo un API REST desarrollado en Java con Spring Boot.
El cliente permite **consultar, crear, actualizar, dibujar y eliminar** planos de un autor, utilizando un canvas HTML5 y un backend que persiste la información en memoria.

---

## 🚀 Requisitos implementados

### **1. Captura de clicks en el Canvas (PointerEvent)**

Se agregó un **manejador de eventos `pointerdown`** al canvas (`#blueprintCanvas`) en el módulo `app.js`, dentro de la función `setupCanvas()`:

```javascript
canvas.addEventListener("pointerdown", (e) => {
  if (!currentBlueprint.name) return; // No hay plano abierto

  const rect = canvas.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;

  currentBlueprint.points.push({ x, y });
  redrawCurrentBlueprint(ctx);
});
```

📌 **Explicación:**

* Usa `PointerEvent`, compatible con mouse y pantallas táctiles.
* Solo permite dibujar si hay un plano abierto.
* Captura la posición del click en coordenadas relativas al canvas.

---

### **2. Dibujo dinámico de puntos y repintado**

Cada punto capturado se **agrega a la lista del blueprint actual** (`currentBlueprint.points`), y luego se redibuja el plano con:

```javascript
function redrawCurrentBlueprint(ctx) {
  ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
  if (currentBlueprint.points.length > 0) {
    ctx.beginPath();
    ctx.moveTo(currentBlueprint.points[0].x, currentBlueprint.points[0].y);
    currentBlueprint.points.slice(1).forEach((p) => ctx.lineTo(p.x, p.y));
    ctx.stroke();
  }
}
```

📌 **Resultado:**
Cada click agrega un punto y repinta en tiempo real la figura sin necesidad de recargar el canvas completo.

---

### **3. Botón “Save / Update”**

El botón guarda los cambios realizados sobre el plano actual.

* Si el plano ya existe: hace un **PUT** al recurso `/api/blueprints/{author}/{bpname}`.
* Si no existe: hace un **POST** para crearlo.
* Luego actualiza la lista de planos y recalcula el total de puntos.

```javascript
function saveBlueprint() {
  const author = getSelectedAuthor();
  const bpname = currentBlueprint.name;
  if (!author || !bpname) {
    alert("Seleccione un autor y un plano primero.");
    return;
  }

  const blueprintData = { author, name: bpname, points: currentBlueprint.points };

  ActiveAPI.getBlueprintsByNameAndAuthor(author, bpname, (points) => {
    const exists = points.length > 0;
    const request = exists
      ? ActiveAPI.updateBlueprint(author, bpname, blueprintData)
      : ActiveAPI.createBlueprint(blueprintData);

    request
      .then(() => ActiveAPI.getBlueprintsByAuthorPromise(author))
      .then((plans) => updateBlueprintsTable(plans))
      .catch((err) => console.error("Error guardando:", err));
  });
}
```

📌 **Uso correcto de promesas:**
Las operaciones se ejecutan en cadena (`.then()`) garantizando que el listado se actualice **solo cuando el backend haya guardado** los cambios.

---

### **4. Botón “Create new blueprint”**

Este botón permite crear un nuevo plano vacío:

```javascript
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

  alert(`Plano "${name}" creado. Dibuja y luego presiona Save/Update para guardar.`);
}
```

📌 **Comportamiento:**

* Limpia el canvas.
* Crea un objeto `Blueprint` vacío.
* Permite dibujar y luego guardar con el botón **Save/Update**, que internamente hará un **POST** la primera vez.

---

### **5. Botón “DELETE”**

Se implementó la funcionalidad completa en el backend y frontend:

#### 🖥️ **Backend**

Se añadió soporte DELETE en todas las capas:

* `BlueprintAPIController.deleteBlueprint(...)`
* `BlueprintsServices.deleteBlueprint(...)`
* `BlueprintsPersistence.deleteBlueprint(...)`
* `InMemoryBlueprintPersistence.deleteBlueprint(...)`

```java
@DeleteMapping("/blueprints/{author}/{bpname}")
public ResponseEntity<?> deleteBlueprint(@PathVariable String author,
                                         @PathVariable String bpname) {
    try {
        services.deleteBlueprint(author, bpname);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (BlueprintNotFoundException ex) {
        return new ResponseEntity<>("Blueprint not found", HttpStatus.NOT_FOUND);
    }
}
```

#### 🌐 **Frontend**

En `app.js` se añadió:

```javascript
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
```

📌 **Cumple:**

* Elimina el plano del backend.
* Limpia el canvas.
* Refresca la tabla de planos y recalcula puntos.
* Usa **promesas** para asegurar el orden correcto.

---

## 🧮 Criterios de evaluación

### ✅ **1. Funcional**

* [x] La aplicación carga y dibuja correctamente los planos.
* [x] Permite crear, modificar y eliminar planos.
* [x] Se actualiza el listado y el total de puntos dinámicamente.
* [x] Canvas funcional con eventos de tipo `PointerEvent`.

### 🎨 **2. Diseño**

* [x] Los cálculos de puntos totales usan **map/reduce**, sin ciclos explícitos.
* [x] Las operaciones de actualización, creación y borrado usan **promesas** (sin callbacks anidados).
* [x] Código modular y dividido correctamente entre `APIClient`, `app.js` y `apimock.js`.

---

## 🧠 Conclusión

El proyecto cumple con todos los requerimientos funcionales y de diseño establecidos en la guía.
El cliente web es capaz de interactuar completamente con el backend RESTful, manejando operaciones **CRUD** de forma reactiva, usando **promesas, eventos Pointer y repintado dinámico en canvas**.

---

**Autores:**
👨‍💻 *Geronimo Martínez Nuñez*
👨‍💻 *Sergio Andrey Silva Rodríguez*

**Escuela Colombiana de Ingeniería Julio Garavito**
**Procesos de Desarrollo de Software – 2025**

---
