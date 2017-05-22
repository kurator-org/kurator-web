/** ClasspathStreamHandler.java
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
package util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/** A {@link URLStreamHandler} that handles resources on the classpath. */
public class ClasspathStreamHandler extends URLStreamHandler {
    /** The classloader to find resources from. */
    private final ClassLoader classLoader;

    public ClasspathStreamHandler() {
        this.classLoader = getClass().getClassLoader();
    }

    public ClasspathStreamHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        final URL resourceUrl = classLoader.getResource(u.getPath());
        return resourceUrl.openConnection();
    }
}