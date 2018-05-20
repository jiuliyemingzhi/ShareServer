package com.jiuli.shareserver.Server.message;

import java.util.Objects;

public class UserInfo {
    private String name;
    private String uuid;
    private double longitude;
    private double latitude;
    private String image;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setOf(UserInfo newInfo) {
        if (newInfo == null) {
            return;
        }
        setImage(newInfo.image);
        setName(newInfo.name);
        setLatitude(newInfo.latitude);
        setLongitude(newInfo.longitude);
        setUuid(newInfo.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid);
    }
}
