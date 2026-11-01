package com.notesphere.notesphere.controller;

import com.notesphere.notesphere.model.Note;
import com.notesphere.notesphere.model.NoteRequest;
import com.notesphere.notesphere.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/notes")
    public List<Note> getAllNotes() {
        return noteService.getAllNotes();
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        return noteService.getNoteById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/notes")
    public ResponseEntity<?> createNote(@RequestBody NoteRequest request) {
        if (!isValid(request)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Title and content must not be empty"));
        }

        Note note = noteService.createNote(
                request.title().trim(),
                request.content().trim()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @PutMapping("/notes/{id}")
    public ResponseEntity<?> updateNote(
            @PathVariable Long id,
            @RequestBody NoteRequest request) {

        if (!isValid(request)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Title and content must not be empty"));
        }

        return noteService.updateNote(
                id,
                request.title().trim(),
                request.content().trim()
        ).<ResponseEntity<?>>map(ResponseEntity::ok)
         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        if (noteService.deleteNote(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        return Map.of("status", "UP", "application", "NoteSphere");
    }

    private boolean isValid(NoteRequest request) {
        return request != null
                && request.title() != null
                && !request.title().isBlank()
                && request.content() != null
                && !request.content().isBlank();
    }
}
