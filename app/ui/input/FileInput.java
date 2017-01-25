package ui.input;

import ui.handlers.FilePathHandler;
import models.db.user.UserUpload;

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