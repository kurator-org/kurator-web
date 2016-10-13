package config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.Logger;
import play.libs.F;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 *
 */
public class ConfigManager {
    private static ConfigManager instance;
    private String jythonPath = ConfigFactory.defaultApplication().getString("jython.path");

    private ConfigManager() { /* Singleton */ }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }

        return instance;
    }

    public List<WorkflowConfig> getWorkflowConfigs() {
        List<WorkflowConfig> workflows = new ArrayList<>();

        File file = new File(jythonPath);

        String[] packages = file.list();

        for (String packageName : packages) {
            File packageDir = new File(file.getAbsolutePath() + File.separator + packageName);

            if (packageDir.isDirectory()) {
                File workflowsDir = new File(packageDir.getAbsolutePath() + File.separator + "workflows");
                File confFile = new File(workflowsDir.getAbsolutePath() + File.separator + "workflows.conf");

                if (confFile.exists()) {
                    Config config = ConfigFactory.parseFile(confFile).getConfig("workflows");

                    for (String name : config.root().keySet()) {
                        workflows.add(new WorkflowConfig(workflowsDir, name, config.getConfig(name)));
                    }
                } else {
                    Logger.warn("Invalid package: " + packageName + " does not contain workflows.conf file");
                }
            }

        }

        return workflows;
    }

    public void unpack(String zipFile) {
        try{
            int BUFFER = 2048;
            File file = new File(zipFile);

            ZipFile zip = new ZipFile(file);

            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements())
            {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

                String currentEntry = entry.getName();
                File destFile = new File(jythonPath, currentEntry);

                // Remove and replace top level package directories
                if(entry.isDirectory() && !entry.getName().matches("\\S+/\\S+")){ // top level folder
                    System.out.println(destFile.getAbsolutePath());
                    delete(destFile);
                }

                File destinationParent = destFile.getParentFile();

                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(zip
                            .getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos,
                            BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            }

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    public static void main(String[] args) {
        ConfigManager.getInstance().unpack("/home/lowery/IdeaProjects/kurator-validation/target/kurator-validation-0.5-SNAPSHOT-packages.zip");
    }
}
