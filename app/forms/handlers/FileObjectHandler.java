package forms.handlers;

import models.UserUpload;
import play.mvc.Http;
import views.html.helper.FieldConstructor;

import java.io.File;

/**
 * Created by lowery on 3/24/16.
 */
public class FileObjectHandler implements FieldHandler<File> {

    @Override
    public File transform(Object obj) {
        if (obj instanceof UserUpload) {
            UserUpload file = (UserUpload) obj;

            return new File(file.fileName);
        } else {
            throw new UnsupportedOperationException("Could not transfrom instance of " + obj.getClass());
        }
    }
}
