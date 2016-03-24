package forms.handlers;

import play.mvc.Http.MultipartFormData.FilePart;

/**
 * Created by lowery on 3/15/16.
 */
public class FilePathHandler implements FieldHandler<String> {
    public String transform(Object obj) {
        if (obj instanceof FilePart) {
            FilePart filePart = (FilePart) obj;

            return filePart.getFile().getAbsolutePath();
        } else {
            throw new UnsupportedOperationException("Could not transfrom instance of " + obj.getClass());
        }
    }
}
