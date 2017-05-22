/** RunResult.java
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
package models.json;

import models.db.workflow.Status;

import java.util.Date;

public class RunResult {
    private long id;
    private String name;
    private String workflow;

    private Date startDate;
    private Date endDate;

    private boolean hasResult;
    private boolean hasOutput;
    private boolean hasErrors;
    private boolean hasReport;

    private Status status;

    public RunResult(long id, String name, String workflow, Date startDate, Date endDate, boolean hasResult, boolean hasOutput,
                     boolean hasErrors, boolean hasReport, Status status) {
        this.id = id;
        this.name = name;
        this.workflow = workflow;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hasResult = hasResult;
        this.hasOutput = hasOutput;
        this.hasErrors = hasErrors;
        this.hasReport = hasReport;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean getHasResult() {
        return hasResult;
    }

    public void setHasResult(boolean hasResult) {
        this.hasResult = hasResult;
    }

    public boolean getHasOutput() {
        return hasOutput;
    }

    public void setHasOutput(boolean hasOutput) {
        this.hasOutput = hasOutput;
    }

    public boolean getHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public boolean getHasReport() {
        return hasReport;
    }

    public void setHasReport(boolean hasReport) {
        this.hasReport = hasReport;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
