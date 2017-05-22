/** CheckBox.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ui.input;

import ui.handlers.BooleanHandler;

/**
 * The checkbox field abstraction.
 */
public class CheckBox extends BasicField {
    public boolean checked;

    public CheckBox() {
        fieldHandler = new BooleanHandler();
    }

    public void setValue(Object obj) {
        checked = true;
    }

    @Override
    public Object value() {
        return fieldHandler.transform(checked);
    }
}
