package com.example.madcampproject2;

public class User {
    private String name;
    private String email;
    private String password;
    private double latitude;
    private double longitude;
    private boolean isActive;

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public boolean getIsActive() { return isActive; }

    public void setName(String input) { name = input; }
    public void setEmail (String input) { email = input; }
    public void setPassword (String input) { password = input; }
    public void setLatitude (double input) { latitude = input; }
    public void setLongitude (double input) { longitude = input; }
    public void setIsActive (boolean input) { isActive = input; }
}
