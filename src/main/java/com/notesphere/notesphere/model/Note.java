package com.notesphere.notesphere.model;

import java.time.LocalDateTime;

public record Note(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
