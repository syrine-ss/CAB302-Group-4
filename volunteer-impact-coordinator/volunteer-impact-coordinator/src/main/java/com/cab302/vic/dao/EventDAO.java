package com.cab302.vic.dao;

import com.cab302.vic.model.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private final Connection connection;

    public EventDAO() throws SQLException {
        connection = DatabaseManager.getConnection();
    }

    public void insert(Event event) {
        try {
            PreparedStatement insertEvent = connection.prepareStatement(
                    "INSERT INTO events "
                            + "(title, description, event_date, location, volunteers_needed, created_by) "
                            + "VALUES (?, ?, ?, ?, ?, ?)"
            );
            insertEvent.setString(1, event.getEventTitle());
            insertEvent.setString(2, event.getEventDescription());
            insertEvent.setString(3, event.getEventDate());
            insertEvent.setString(4, event.getLocation());
            insertEvent.setInt(5, event.getVolunteersAmount());
            insertEvent.setInt(6, event.getHostId());
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to insert into events table", ex);
        }
    }


    public void update(Event event) {
        try {
            PreparedStatement updateEvent = connection.prepareStatement(
                    "UPDATE events SET "
                            + "title = ?, description = ?, event_date = ?, location = ?, volunteers_needed = ?, created_by = ? "
                            + "WHERE id = ?"
            );
            updateEvent.setString(1, event.getEventTitle());
            updateEvent.setString(2, event.getEventDescription());
            updateEvent.setString(3, event.getEventDate());
            updateEvent.setString(4, event.getLocation());
            updateEvent.setInt(5, event.getVolunteersAmount());
            updateEvent.setInt(6, event.getHostId());
            updateEvent.execute();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to update events table", ex);
        }
    }
    public void delete(int id){
        try {
            PreparedStatement deleteEvent = connection.prepareStatement("DELETE FROM events WHERE id = ?");
            deleteEvent.setInt(1, id);
            deleteEvent.execute();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete event from events table", ex);
        }
    }

    // for now, just have get all events but can add more methods for specific selection query if needed
    public List<Event> getAllEvents() {
        List<Event> allEvents = new ArrayList<>();
        try {
            Statement getAll = connection.createStatement();
            ResultSet rs = getAll.executeQuery("SELECT * FROM events");
            while (rs.next()) {
                allEvents.add(
                        new Event(
                                rs.getInt("id"),
                                rs.getString("title"),
                                rs.getString("description"),
                                rs.getString("event_date"),
                                rs.getString("location"),
                                rs.getInt("volunteers_needed"),
                                rs.getInt("created_by")
                        )
                );
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to retrieved events table", ex);
        }
        return allEvents;
    }

    public Event getById(int id) {
        try {
            PreparedStatement getEvent = connection.prepareStatement("SELECT * FROM events WHERE id = ?");
            getEvent.setInt(1, id);
            ResultSet rs = getEvent.executeQuery();
            if (rs.next()) {
                return new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("event_date"),
                        rs.getString("location"),
                        rs.getInt("volunteers_needed"),
                        rs.getInt("created_by")
                );
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to retrieved events by id", ex);
        }
        return null;
    }
}
