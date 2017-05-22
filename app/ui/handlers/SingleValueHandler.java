/** SingleValueHandler.java
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
package ui.handlers;

import java.util.List;

/**
 * Handles a single value and returns a string. Treats multiple values as single value lists.
 */
public class SingleValueHandler implements FieldHandler<String> {
    @Override
    public String transform(Object obj) {
        if (obj instanceof List) {
            return ((List<String>) obj).get(0).toString(); // For single value list
        } else if (obj instanceof String[]){
            return ((String[])obj)[0];
        } else {
            return obj.toString();
        }
    }
}
