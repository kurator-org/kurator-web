package models.json;

import models.db.user.Role;

/**
 * Created by lowery on 1/25/2017.
 */
public class UserManagement {
    private String username;
    private boolean active;
    private Role role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
