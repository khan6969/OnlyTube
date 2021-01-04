package com.example.onlytube;

public class IconList {
    private String Icon_name;
    private int category_image;

    public IconList() {
    }

    public IconList(String icon_name, int category_image) {
        Icon_name = icon_name;
        this.category_image = category_image;
    }

    public String getIcon_name() {
        return Icon_name;
    }

    public void setIcon_name(String icon_name) {
        Icon_name = icon_name;
    }

    public int getCategory_image() {
        return category_image;
    }

    public void setCategory_image(int category_image) {
        this.category_image = category_image;
    }
}
