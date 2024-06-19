package com.example.blacksuits.DataClass;

import android.net.Uri;

import java.util.Collection;

public class ClientInformation {
    // Attributes
    private String clientUsername;

    private String Uid;
    private String location;

    private String designation;
    private String phoneNumber;
    private GeoLocation geoLocation;

    private String profilePictureSrc;

    private String id;


    private double distanceFromUser;







    // Constructors
    public ClientInformation() {
        // Default constructor
    }

    public ClientInformation(String clientUsername, String Uid, String profilePictureSrc, String notNeeded)
    {
        this.clientUsername = clientUsername;
        this.Uid = Uid;
        this.profilePictureSrc = profilePictureSrc;
    }




    public ClientInformation(String clientUsername,String location, String phoneNumber)
    {
        this.clientUsername = clientUsername;
        this.location = location;
        this.phoneNumber = phoneNumber;
    }

    public ClientInformation(String clientUsername, String location, String phoneNumber, GeoLocation geoLocation, Double distanceFromUser, String designation, String Uid, String src) {
        this.clientUsername = clientUsername;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.geoLocation = geoLocation;
        this.distanceFromUser = distanceFromUser;
        this.designation = designation;
        this.Uid = Uid;
        profilePictureSrc = src;


    }

    // Getter and Setter methods
    public String getClientUsername() {
        return clientUsername;
    }

    public void setUid (String Uid)
    {
        this.Uid = Uid;
    }

    public void setDesignation (String designation)
    {
        this.designation = designation;
    }

    public String getDesignation ()
    {
        return designation;
    }

    public void setProfilePictureSrc(String src)
    {
        profilePictureSrc = src;
    }


    public String getProfilePicture ()
    {
        return profilePictureSrc;
    }







    public String getUid ()
    {
        return Uid;
    }


    public void setClientUsername(String clientUsername) {
        this.clientUsername = clientUsername;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDistanceFromUser(Double distanceFromUser)
    {
        this.distanceFromUser = distanceFromUser;
    }

    public Double getDistanceFromUser ()
    {
        return distanceFromUser;
    }

    public GeoLocation getGeolocation() {
        return geoLocation;
    }

    public void setGeolocation(GeoLocation geolocation) {
        this.geoLocation = geolocation;
    }





}