package forms.input;

import forms.handlers.FileObjectHandler;
import forms.handlers.FilePathHandler;
import models.UserUpload;
import play.mvc.Http.MultipartFormData.FilePart;

/**
 * File input field abstraction class
 */
public class FileInput extends BasicField<UserUpload> {
    public boolean multiple;

    public UserUpload value;

    public FileInput() {
        fieldHandler = new FilePathHandler();
    }

    public Object value() {
        return fieldHandler.transform(value);
    }

    @Override
    public void setValue(UserUpload value) {
        this.value = value;
    }
}