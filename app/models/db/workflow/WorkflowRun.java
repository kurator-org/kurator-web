/** WorkflowRun.java
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
import models.db.user.User;
import models.db.user.UserGroup;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * The model object that represents a single run of a workflow.
 */
@Entity
public class WorkflowRun extends Model {
    public static Finder<Long, WorkflowRun> find = new Finder<Long,WorkflowRun>(WorkflowRun.class);

    @Id
    private Long id;

    private String name;

    @ManyToOne
    private Workflow workflow;

    private Date startTime;
    private Date endTime;

    private Status status;

    @OneToOne(cascade=CascadeType.ALL)
    private WorkflowResult result;

    @ManyToOne
    private User owner;

    // sharing
    @ManyToMany
    private List<UserGroup> groups;

    @ManyToMany
    private List<User> users;

    private Date sharedOn;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public WorkflowResult getResult() {
        return result;
    }

    public void setResult(WorkflowResult result) {
        this.result = result;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<UserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Date getSharedOn() {
        return sharedOn;
    }

    public void setSharedOn(Date sharedOn) {
        this.sharedOn = sharedOn;
    }
}
