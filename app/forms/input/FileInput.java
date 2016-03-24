package forms.input;

import play.mvc.Http.MultipartFormData.FilePart;

/**
 * Created by lowery on 2/29/2016.
 */
public class FileInput extends BasicField<FilePart> {
    public boolean multiple;

    public FilePart filePart;


    public Object getValue() {
        return fieldHandler.transform(filePart);
    }

    @Override
    public void setValue(FilePart value) {
        this.filePart = value;
    }
}