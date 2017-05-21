package ui.input;

import ui.handlers.SingleValueHandler;

/**
 * Token field
 */
public class TokenField extends BasicField {
    public String value;

    public TokenField() {
        fieldHandler = new SingleValueHandler();
    }

    @Override
    public void setValue(Object obj) {
        this.value = ((String[]) obj)[0];
    }

    @Override
    public Object value() {
        String searchStr = value.replace(",", "|");
        System.out.println("search string" + searchStr);
        return searchStr;
    }
}