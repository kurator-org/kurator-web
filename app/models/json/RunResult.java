package models.json;

import models.db.workflow.Status;

import java.util.Date;

/**
 * Created by lowery on 1/25/2017.
 */
public class RunResult {
    private long id;
    private String workflow;

    private Date startDate;
    private Date endDate;

    private boolean hasResult;
    private boolean hasOutput;
    private boolean hasErrors;
    private boolean hasReport;

    private Status status;

    public RunResult(long id, String workflow, Date startDate, Date endDate, boolean hasResult, boolean hasOutput,
                     boolean hasErrors, boolean hasReport, Status status) {
        this.id = id;
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
