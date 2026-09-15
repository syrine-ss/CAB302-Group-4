package com.cab302.vic.model;

public class Event {
    private int id;
    private String eventTitle;
    private String eventDescription;
    private String eventDate;
    private String location;
    private int volunteersAmount;
    private int hostId;

    public Event(int id, String eventTitle, String eventDescription,String eventDate,
                 String location, int volunteersAmount, int hostId) {
        this.id = id;
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.location = location;
        this.volunteersAmount = volunteersAmount;
        this.hostId = hostId;
    }

    // Since id is auto-incrementing in table, have an initialisation without id
    public Event(String eventTitle, String eventDescription,String eventDate,
                 String location, int volunteersAmount, int hostId) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.location = location;
        this.volunteersAmount = volunteersAmount;
        this.hostId = hostId;
    }

    // getters
    public int getId() { return id; }
    public String getEventTitle() { return eventTitle; }
    public String getEventDescription() { return eventDescription; }
    public String getEventDate() { return eventDate; }
    public String getLocation() { return location; }
    public int getVolunteersAmount() { return volunteersAmount; }
    public int getHostId() { return hostId; }

    // setters
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setLocation(String location) { this.location = location; }
    public void setVolunteersInt(int volunteersAmount) { this.volunteersAmount = volunteersAmount; }
    public void setHostId(int hostId) { this.hostId = hostId; }

}
