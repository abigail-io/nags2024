package com.example.donna_app;

public class Note {
    private String description;
    private boolean important;
    private boolean todo;
    private boolean idea;
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        String title = null;
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setIdea(boolean idea) {
        this.idea = idea;
    }

    public boolean isIdea() {
        boolean idea = false;
        return idea;
    }

    public void setTodo(boolean todo) {
        this.todo = todo;
    }

    public boolean isTodo() {
        return todo;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public boolean isImportant() {
        return important;
    }
}
