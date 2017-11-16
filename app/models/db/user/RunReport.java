package models.db.user;

/**
 * Created by lowery on 11/16/17.
 */
public class RunReport {
    private long successCount;
    private long errorsCount;
    private long runningCount;

    public RunReport(long successCount, long errorsCount, long runningCount) {
        this.successCount = successCount;
        this.errorsCount = errorsCount;
        this.runningCount = runningCount;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public long getErrorsCount() {
        return errorsCount;
    }

    public long getRunningCount() {
        return runningCount;
    }
}
