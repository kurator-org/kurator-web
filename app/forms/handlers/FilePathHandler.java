package forms.handlers;

import models.UserUpload;
import play.mvc.Http.MultipartFormData.FilePart;

/**
 * Created by lowery on 3/15/16.
 */
public class FilePathHandler implements FieldHandler<String> {
    public String transform(Object obj) {
        if (obj instanceof UserUpload) {
            UserUpload file = (UserUpload) obj;

            return file.absolutePath;
        } else {
            throw new UnsupportedOperationException("Could not transfrom instance of " + obj.getClass());
        }
    }
}
