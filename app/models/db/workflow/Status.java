package models.db.workflow;

import com.avaje.ebean.annotation.EnumValue;

/**
 * Created by lowery on 1/24/2017.
 */
public enum Status {
    @EnumValue("GUEST") SUCCESS,
    @EnumValue("RUNNING") RUNNING,
    @EnumValue("ERROR") ERROR
}
