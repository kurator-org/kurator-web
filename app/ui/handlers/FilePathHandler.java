/** UserUpload.java
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

import dao.UserDao;
import models.db.user.UserUpload;

import java.io.File;

/**
 * Transform a file input object to its absolute path.
 */
public class FilePathHandler implements FieldHandler<String> {
    private final UserDao userDao = new UserDao();

    public String transform(Object obj) {
        if (obj instanceof File) {
            File file = (File) obj;

            return file.getAbsolutePath();
        } else if (obj instanceof String[]) {
            String[] files = (String[]) obj;
            UserUpload file = userDao.findUserUploadById(Long.parseLong(files[0]));

            System.out.println(file.getAbsolutePath());
            return file.getAbsolutePath();
        } else {
            throw new UnsupportedOperationException("Could not transfrom instance of " + obj.getClass());
        }
    }
}
