import models.WorkflowRun;
import play.*;

import java.util.Date;
import java.util.List;

public class Global extends GlobalSettings {

  @Override
  public void onStart(Application app) {
    Logger.info("Application has started");

    // Clean up stalled workflows

    List<WorkflowRun> stalled = WorkflowRun.find.where().eq("status", WorkflowRun.STATUS_RUNNING).findList();

    for (WorkflowRun run : stalled) {
      run.endTime = new Date();
      run.status = WorkflowRun.STATUS_ERROR;
      run.save();
    }
  }  
  
  @Override
  public void onStop(Application app) {
    Logger.info("Application shutdown...");
  }  
    
}