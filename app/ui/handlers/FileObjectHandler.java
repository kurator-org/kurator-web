package ui.handlers;

import models.db.user.UserUpload;

import java.io.File;

/**
 * ClasspathStreamHandler for transforming web app file objects to appropriate inputs for the actors
 */
public class FileObjectHandler implements FieldHandler<File> {
    public File transform(Object obj) {
        if (obj instanceof UserUpload) {
            UserUpload file = (UserUpload) obj;

            return new File(file.getFileName());
        } else {
            throw new UnsupportedOperationException("Could not transfrom instance of " + obj.getClass());
        }
    }
}
