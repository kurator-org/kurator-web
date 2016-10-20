package models;

import java.util.Date;

/**
 * Created by lowery on 10/20/16.
 */
public class PackageData {
    public String name;
    public Date lastModified;

    public PackageData(String name, Date lastModified) {
        this.name = name;
        this.lastModified = lastModified;
    }
}
