package config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.Logger;
import play.api.Play;
import util.WorkflowPackageVerifier;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

        if (packages == null) {
            // No workflow packages deployed, return empty list
            return workflows;
        }

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

    public void unpack(File zipFile) {
        boolean verified = false;

        char[] keystorePassword = ConfigFactory.defaultApplication().getString("keystore.password").toCharArray();
        String keystoreLocation = ConfigFactory.defaultApplication().getString("keystore.location");

        try {
            WorkflowPackageVerifier verifier = new WorkflowPackageVerifier();
           verified = verifier.checkIntegrity(zipFile, keystoreLocation, keystorePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!verified) {
            throw new RuntimeException("Could not verify package zip file.");
        }

        try {
            int BUFFER = 2048;
            ZipFile zip = new ZipFile(zipFile);

            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

                String currentEntry = entry.getName();
                File destFile = new File(jythonPath, currentEntry);

                // Remove and replace top level package directories
                if(destFile.exists() && entry.isDirectory() && !entry.getName().matches("\\S+/\\S+")){ // top level folder
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

}
