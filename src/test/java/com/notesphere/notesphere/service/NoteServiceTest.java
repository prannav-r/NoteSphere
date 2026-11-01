package com.notesphere.notesphere.service;

import com.notesphere.notesphere.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NoteServiceTest {

    private NoteService noteService;

    @BeforeEach
    void setUp() {
        noteService = new NoteService();
    }

    @Test
    void shouldCreateNote() {
        Note note = noteService.createNote("Test Note", "Test Content");

        assertNotNull(note.id());
        assertEquals("Test Note", note.title());
        assertEquals("Test Content", note.content());
    }

    @Test
    void shouldRetrieveAllNotes() {
        noteService.createNote("Note 1", "Content 1");
        noteService.createNote("Note 2", "Content 2");

        assertEquals(2, noteService.getAllNotes().size());
    }

    @Test
    void shouldRetrieveNoteById() {
        Note created = noteService.createNote("Test", "Content");

        Optional<Note> result = noteService.getNoteById(created.id());

        assertTrue(result.isPresent());
        assertEquals("Test", result.get().title());
    }

    @Test
    void shouldUpdateNote() {
        Note created = noteService.createNote("Old Title", "Old Content");

        Optional<Note> updated = noteService.updateNote(
                created.id(), "New Title", "New Content"
        );

        assertTrue(updated.isPresent());
        assertEquals("New Title", updated.get().title());
        assertEquals("New Content", updated.get().content());
    }

    @Test
    void shouldDeleteNote() {
        Note created = noteService.createNote("Test", "Content");

        assertTrue(noteService.deleteNote(created.id()));
        assertFalse(noteService.getNoteById(created.id()).isPresent());
    }

    @Test
    void shouldReturnEmptyForNonexistentNote() {
        assertFalse(noteService.getNoteById(999L).isPresent());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonexistentNote() {
        assertFalse(noteService.deleteNote(999L));
    }
}
