package com.example.carparking.model;

public class SearchResult {
    private String title;
    private String subtitle;
    private SearchResultType type;
    private String id;

    public enum SearchResultType {
        PARKING_LOCATION,
        BUILDING
    }

    public SearchResult() {
    }

    public SearchResult(String id, String title, String subtitle, SearchResultType type) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public SearchResultType getType() {
        return type;
    }

    public void setType(SearchResultType type) {
        this.type = type;
    }
} 