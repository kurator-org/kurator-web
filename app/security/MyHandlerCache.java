/** MyHandlerCache.java
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
package security;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.ExecutionContextProvider;
import be.objectify.deadbolt.java.cache.HandlerCache;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MyHandlerCache implements HandlerCache {
    private final DeadboltHandler defaultHandler;

    @Inject
    public MyHandlerCache(final ExecutionContextProvider ecProvider) {
        defaultHandler = new MyDeadboltHandler(ecProvider);
    }

    @Override
    public DeadboltHandler apply(final String key) {
        return defaultHandler;
    }

    @Override
    public DeadboltHandler get() {
        return defaultHandler;
    }
}