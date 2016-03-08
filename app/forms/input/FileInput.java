package forms.input;

import models.UserUpload;
import play.mvc.Http;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 2/29/2016.
 */
public class FileInput extends BasicField {
    public boolean multiple;

    public Map<String, File> fileList = new HashMap<>();

    @Override
    public void setValue(Object obj) {
        UserUpload userUpload = null;

        if (obj instanceof UserUpload) {
            userUpload = (UserUpload) obj;
        } else if (obj instanceof String[]){
            // Uploaded file id
            userUpload = UserUpload.find.byId(Long.parseLong(((String[])obj)[0]));
        }

        fileList.put(userUpload.fileName, new File(userUpload.absolutePath));
    }

    @Override
    public String getValue() {
        // TODO: add support for list of files if multiple file upload is enabled
        return fileList.values().toArray(new File[1])[0].getAbsolutePath();
    }

    @Override
    public String toString() {
        return "FileInput{" +
                "files=" + fileList.keySet() +
                '}';
    }
}
