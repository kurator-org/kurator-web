/** SecurityRole.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models.db.user;

import be.objectify.deadbolt.java.models.Role;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.EnumValue;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SecurityRole extends Model implements Role {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    @Id
    private Long id;

    private String name;

    public static final Finder<Long, SecurityRole> find = new Finder<>(Long.class,
            SecurityRole.class);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static SecurityRole findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }
}