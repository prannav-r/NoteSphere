package com.notesphere.notesphere.service;

import com.notesphere.notesphere.model.Note;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NoteService {

    private final ConcurrentHashMap<Long, Note> notes = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public List<Note> getAllNotes() {
        return notes.values().stream()
                .sorted(Comparator.comparing(Note::id).reversed())
                .toList();
    }

    public Optional<Note> getNoteById(Long id) {
        return Optional.ofNullable(notes.get(id));
    }

    public Note createNote(String title, String content) {
        LocalDateTime now = LocalDateTime.now();
        Long id = idCounter.incrementAndGet();

        Note note = new Note(id, title, content, now, now);
        notes.put(id, note);

        return note;
    }

    public Optional<Note> updateNote(Long id, String title, String content) {
        return Optional.ofNullable(notes.computeIfPresent(id, (key, existing) ->
                new Note(
                        id,
                        title,
                        content,
                        existing.createdAt(),
                        LocalDateTime.now()
                )
        ));
    }

    public boolean deleteNote(Long id) {
        return notes.remove(id) != null;
    }
}
