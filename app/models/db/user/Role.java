package models.db.user;

import com.avaje.ebean.annotation.EnumValue;

/**
 * Created by lowery on 1/24/2017.
 */
public enum Role {
    @EnumValue("ADMIN") ADMIN,
    @EnumValue("USER") USER,
    @EnumValue("GUEST") GUEST
}
