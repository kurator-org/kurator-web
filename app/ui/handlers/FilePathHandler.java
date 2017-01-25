package ui.handlers;

import models.db.user.UserUpload;

/**
 * Transform a file input object to its absolute path.
 */
public class FilePathHandler implements FieldHandler<String> {
    public String transform(Object obj) {
        if (obj instanceof UserUpload) {
            UserUpload file = (UserUpload) obj;

            return file.getAbsolutePath();
        } else {
            throw new UnsupportedOperationException("Could not transfrom instance of " + obj.getClass());
        }
    }
}
