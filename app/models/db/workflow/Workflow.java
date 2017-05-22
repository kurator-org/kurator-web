/** Workflow.java
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
package models.db.workflow;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The workflow metadata
 */
@Entity
public class Workflow extends Model {
    public static Finder<Long, Workflow> find = new Finder<Long,Workflow>(Workflow.class);

    @Id
    private Long id;

    private String name;
    private String title;

    private String yamlFile;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYamlFile() {
        return yamlFile;
    }

    public void setYamlFile(String yamlFile) {
        this.yamlFile = yamlFile;
    }
}
