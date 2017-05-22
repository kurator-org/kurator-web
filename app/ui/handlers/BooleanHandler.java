/** BooleanHandler.java
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

/**
 * Field handler for boolean values capable of transforming text to an instance of Boolean
 */
public class BooleanHandler implements FieldHandler<Boolean> {

    public Boolean transform(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        } else if (obj instanceof String) {
            return Boolean.parseBoolean((String) obj);
        } else {
            throw new UnsupportedOperationException("Could not transfrom instance of " + obj.getClass());
        }
    }
}
