package com.example.carparking.model;

public class SearchResult {
    private String title;
    private String subtitle;
    private SearchResultType type;
    private String id;
    private String placeId;
    private Double latitude;
    private Double longitude;

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

    // Constructor for Places API results
    public SearchResult(String placeId, String title, String subtitle, SearchResultType type, Double latitude, Double longitude) {
        this.placeId = placeId;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
