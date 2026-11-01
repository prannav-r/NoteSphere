const API = "/api/notes";

const grid = document.getElementById("notesGrid");
const emptyState = document.getElementById("emptyState");
const modal = document.getElementById("noteModal");
const form = document.getElementById("noteForm");
const titleInput = document.getElementById("titleInput");
const contentInput = document.getElementById("contentInput");
const searchInput = document.getElementById("searchInput");

let notes = [];
let editingId = null;

async function request(url, options = {}) {
    const response = await fetch(url, options);

    if (!response.ok) {
        const message = await response.text();
        throw new Error(message || `Request failed: ${response.status}`);
    }

    if (response.status === 204) return null;
    return response.json();
}

function showToast(message) {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.classList.add("show");

    setTimeout(() => toast.classList.remove("show"), 2500);
}

function escapeHTML(value) {
    return String(value).replace(/[&<>"']/g, char => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[char]);
}

function formatDate(date) {
    return new Date(date).toLocaleDateString("en-IN", {
        day: "numeric",
        month: "short",
        year: "numeric"
    });
}

async function loadNotes() {
    try {
        notes = await request(API);
        renderNotes();
    } catch (error) {
        showToast("Unable to load notes. Check the server.");
        console.error(error);
    }
}

function renderNotes() {
    const query = searchInput.value.toLowerCase().trim();

    const filtered = notes.filter(note =>
        note.title.toLowerCase().includes(query) ||
        note.content.toLowerCase().includes(query)
    );

    document.getElementById("noteCount").textContent = notes.length;
    document.getElementById("noteSummary").textContent =
        `${filtered.length} ${filtered.length === 1 ? "note" : "notes"}`;

    grid.innerHTML = filtered.map(note => `
        <article class="note-card">
            <h3>${escapeHTML(note.title)}</h3>
            <p>${escapeHTML(note.content)}</p>
            <div class="note-date">Updated ${formatDate(note.updatedAt)}</div>
            <div class="card-actions">
                <button data-action="edit" data-id="${note.id}">Edit</button>
                <button class="delete-btn" data-action="delete" data-id="${note.id}">Delete</button>
            </div>
        </article>
    `).join("");

    emptyState.classList.toggle("visible", filtered.length === 0);
    grid.style.display = filtered.length === 0 ? "none" : "grid";

    if (notes.length === 0) {
        emptyState.querySelector("h2").textContent = "No notes yet";
        emptyState.querySelector("p").textContent =
            "Start capturing your thoughts and ideas.";
    } else if (filtered.length === 0) {
        emptyState.querySelector("h2").textContent = "No matching notes";
        emptyState.querySelector("p").textContent =
            "Try a different search term.";
    }
}

function openModal(note = null) {
    editingId = note ? note.id : null;
    document.getElementById("modalTitle").textContent =
        note ? "Edit note" : "Create a note";

    document.getElementById("saveBtn").textContent =
        note ? "Update Note" : "Save Note";

    titleInput.value = note ? note.title : "";
    contentInput.value = note ? note.content : "";

    modal.classList.add("open");
    titleInput.focus();
}

function closeModal() {
    modal.classList.remove("open");
    form.reset();
    editingId = null;
}

async function saveNote(event) {
    event.preventDefault();

    const payload = {
        title: titleInput.value.trim(),
        content: contentInput.value.trim()
    };

    if (!payload.title || !payload.content) {
        showToast("Please fill in both fields.");
        return;
    }

    try {
        if (editingId !== null) {
            await request(`${API}/${editingId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            showToast("Note updated successfully!");
        } else {
            await request(API, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
            showToast("Note created successfully!");
        }

        closeModal();
        await loadNotes();
    } catch (error) {
        showToast("Unable to save note.");
        console.error(error);
    }
}

async function deleteNote(id) {
    if (!confirm("Are you sure you want to delete this note?")) return;

    try {
        await request(`${API}/${id}`, { method: "DELETE" });
        showToast("Note deleted successfully!");
        await loadNotes();
    } catch (error) {
        showToast("Unable to delete note.");
        console.error(error);
    }
}

grid.addEventListener("click", event => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;

    const id = Number(button.dataset.id);
    const note = notes.find(item => item.id === id);

    if (button.dataset.action === "edit" && note) {
        openModal(note);
    }

    if (button.dataset.action === "delete") {
        deleteNote(id);
    }
});

form.addEventListener("submit", saveNote);
searchInput.addEventListener("input", renderNotes);

["headerNewBtn", "newNoteBtn", "emptyNewBtn"].forEach(id => {
    document.getElementById(id).addEventListener("click", () => openModal());
});

document.getElementById("closeModalBtn").addEventListener("click", closeModal);
document.getElementById("cancelBtn").addEventListener("click", closeModal);

document.getElementById("allNotesBtn").addEventListener("click", () => {
    searchInput.value = "";
    renderNotes();
});

modal.addEventListener("click", event => {
    if (event.target === modal) closeModal();
});

loadNotes();