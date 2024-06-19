package com.example.blacksuits.DataClass;

public class GeoLocation {
    private double latitude;
    private double longitude;
    private double distanceFromUser;

    // Constructors
    public GeoLocation(String username, double distanceFromUser)
    {


    }




    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GeoLocation(double latitude, double longitude, double distanceFromUser) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceFromUser = distanceFromUser;
    }

    // Getters and Setters
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDistanceFromUser (double distanceFromUser)
    {
        this.distanceFromUser = distanceFromUser;
    }

    public double getDistanceFromUser()
    {
        return distanceFromUser;
    }



    // toString method for easy printing
    @Override
    public String toString() {
        return "GeoLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
