package util;

import forms.FormDefinition;
import forms.input.FileInput;
import forms.input.TextField;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by lowery on 2/29/2016.
 */
public class YamlFormDefinitionParser {
    public FormDefinition parse(InputStream yamlStream) {
        Yaml yaml = new Yaml(new RefConstructor());
        Map<String, Map> values = (Map<String, Map>) yaml
                .load(yamlStream);
        FormDefinition form = new FormDefinition();

        String title = (String) values.get("view").get("title");
        form.title = title;

        Map<String, Map<String, String>> fields = (Map<String, Map<String, String>>) values.get("view").get("fields");
        for (String fieldName : fields.keySet()) {
            Map<String, String> fieldDef = fields.get(fieldName);

            switch (fieldDef.get("type")) {
                case "file":
                    FileInput fileParam = new FileInput(fieldName, fieldDef.get("label"), false);
                    form.addField(fileParam);
                    break;
                case "text":
                    TextField textParam = new TextField(fieldName, fieldDef.get("label"), "", false);
                    form.addField(textParam);
                    break;
            }
        }

        return form;
    }
}

class RefConstructor extends SafeConstructor {
    public RefConstructor() {
        this.yamlConstructors.put(new Tag("!ref"), new ConstructRef());
    }

    private class ConstructRef extends AbstractConstruct {
        public Object construct(Node node) {
            return null;
        }
    }
}