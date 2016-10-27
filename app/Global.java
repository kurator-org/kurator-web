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

    if (config.hasPath("kurator.autoInstall") && config.getBoolean("kurator.autoInstall")) {
      File packagesDir = new File(config.getString("jython.packages"));

      if (!packagesDir.exists()) {
        System.out.println("Creating packages directory: " + packagesDir.getAbsolutePath());

        if (!packagesDir.getParentFile().exists()) {
          System.out.println("Error: Parent directory " + packagesDir.getParent() + " does not exist!");
          System.exit(-1);
        }

        if (!packagesDir.getParentFile().canWrite()) {
          System.out.println("Error: Current user does not have write permissions for directory " + packagesDir.getParent());
          System.exit(-1);
        }

        packagesDir.mkdir();
      }

      File workspaceDir = new File(config.getString("jython.workspace"));

      if (!workspaceDir.exists()) {
        System.out.println("Creating workspace directory: " + workspaceDir.getAbsolutePath());

        if (!workspaceDir.getParentFile().exists()) {
          System.out.println("Error: Parent directory " + workspaceDir.getParent() + " does not exist!");
          System.exit(-1);
        }

        if (!workspaceDir.getParentFile().canWrite()) {
          System.out.println("Error: Current user does not have write permissions in directory " + workspaceDir.getParent());
          System.exit(-1);
        }

        workspaceDir.mkdir();
      }

      File jythonDir = new File(config.getString("jython.home"));
      if (!jythonDir.exists()) {
        System.out.println("Jython not found, running installer now...");
        boolean success = jythonDir.mkdir();

        System.out.println("Creating jython home directory: " + jythonDir.getAbsolutePath());

        if (!jythonDir.getParentFile().exists()) {
          System.out.println("Error: Parent directory " + jythonDir.getParent() + " does not exist!");
          System.exit(-1);
        }

          if (!jythonDir.getParentFile().canWrite()) {
            System.out.println("Error: Current user does not have write permissions in directory " + jythonDir.getParent());
            System.exit(-1);
          }

        //Before running the external Command
        MySecurityManager secManager = new MySecurityManager();
        System.setSecurityManager(secManager);

        String[] args = {"-s", "-d", jythonDir.getAbsolutePath()};
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