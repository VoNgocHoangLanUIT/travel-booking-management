package com.hotelmanagement.models;

public class Room {
    private final int imageResId;
    private final String title;
    private final String meta;
    private final String price;
    private final String badge;
    private boolean favorite;

    public Room(int imageResId, String title, String meta, String price, String badge, boolean favorite) {
        this.imageResId = imageResId;
        this.title = title;
        this.meta = meta;
        this.price = price;
        this.badge = badge;
        this.favorite = favorite;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getMeta() {
        return meta;
    }

    public String getPrice() {
        return price;
    }

    public String getBadge() {
        return badge;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}