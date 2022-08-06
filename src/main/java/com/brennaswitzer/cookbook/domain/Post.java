package com.brennaswitzer.cookbook.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Post {
    private final Long id;
    private String text;
    private OffsetDateTime date;

    public Post(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getName() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }
}
