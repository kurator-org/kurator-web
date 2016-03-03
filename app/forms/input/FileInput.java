package forms.input;

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

    public FileInput(String name, String label, boolean multiple) {
        this.name = name;
        this.label = label;
        this.multiple = multiple;
    }

    @Override
    public void setValue(Object obj) {
        Http.MultipartFormData.FilePart filePart = (Http.MultipartFormData.FilePart) obj;
        fileList.put(filePart.getFilename(), filePart.getFile());
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