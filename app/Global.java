import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.WorkflowRun;
import org.python.util.install.Installation;
import play.*;

import java.io.*;
import java.security.Permission;
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

  @Override
  public void beforeStart(Application app) {
    Config config = ConfigFactory.defaultApplication();
    System.out.println();

    if (config.hasPath("kurator.autoinstall") && config.getBoolean("kurator.autoinstall")) {
      File packagesDir = new File("packages");

      if (!packagesDir.exists()) {
        System.out.println("Creating packages directory: " + packagesDir.getAbsolutePath());
        packagesDir.mkdir();
      }

      File workspaceDir = new File("workspace");

      if (!workspaceDir.exists()) {
        System.out.println("Creating workspace directory: " + workspaceDir.getAbsolutePath());

        workspaceDir.mkdir();
      }

      File jythonDir = new File("jython");
      if (!jythonDir.exists()) {
        System.out.println("Jython not found, running installer now...");
        jythonDir.mkdir();

        //Before running the external Command
        MySecurityManager secManager = new MySecurityManager();
        System.setSecurityManager(secManager);

        String[] args = {"-s", "-d", "jython"};
        Installation.driverMain(args, null, null);
      }
    }
  }

  class MySecurityManager extends SecurityManager {
    @Override public void checkExit(int status) {
      System.out.println("Installation of kurator-web is complete. Rerun the startup script now to start the server.");
      File pid = new File("RUNNING_PID");
      if (pid.exists()) {
        pid.deleteOnExit();
      }
    }

    @Override
    public void checkPermission(Permission perm) {

    }

    @Override
    public void checkPermission(Permission perm, Object context) {

    }
  }
}