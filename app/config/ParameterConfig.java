/** ParameterConfig.java
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
package config;

import com.typesafe.config.Config;

import java.util.Collections;
import java.util.Map;

public class ParameterConfig {
    private final Config config;

    private String name;
    private String description;
    private String label;
    private String type;

    private String value;
    private String options;

    protected ParameterConfig(String name, Config config) {
        this.name = name;
        this.config = config;
    }

    public boolean isTyped() {
        return config.hasPath("type");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return config.getString("description");
    }

    public String getLabel() {
        return config.getString("label");
    }

    public String getType() {
        return config.getString("type");
    }

    public String getValue() {
        return config.getString("value");
    }

    public Map<String, Object> getOptions() {
        return Collections.unmodifiableMap(config.getObject("options").unwrapped());
    }
}
