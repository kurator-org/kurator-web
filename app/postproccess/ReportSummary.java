package postproccess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kurator.akka.data.DQReport.DQReport;
import org.kurator.akka.data.DQReport.Improvement;
import org.kurator.akka.data.DQReport.Validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by lowery on 4/5/16.
 */
public class ReportSummary {
    private List<DQReport> reports;
    private Map<String, List<Validation>> compliant = new HashMap<>();
    private Map<String, List<Validation>> nonCompliant = new HashMap<>();

    private Map<String, List<Improvement>> improvements = new HashMap<>();

    public ReportSummary(InputStream in) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.reports = mapper.readValue(in, new TypeReference<List<DQReport>>(){});

        initSummary();
    }
    public ReportSummary(List<DQReport> reports) {
        this.reports = reports;

        initSummary();
    }

    private void initSummary() {
        for (DQReport report : reports) {
            for (Validation validation : report.getValidations()) {
                String criterion = validation.getCriterion();

                if (validation.getResult() != null && validation.getResult().equals("Compliant")) {
                    if (!compliant.containsKey(criterion)) {
                        compliant.put(criterion, new ArrayList<>());
                    }

                    List<Validation> compliantValidations = compliant.get(criterion);

                    compliantValidations.add(validation);
                } else if (validation.getResult() == null || validation.getResult().equals("Not Compliant")) {
                    if (!nonCompliant.containsKey(criterion)) {
                        nonCompliant.put(criterion, new ArrayList<>());
                    }

                    List<Validation> nonCompliantValidations = nonCompliant.get(criterion);

                    nonCompliantValidations.add(validation);
                }
            }

            for (Improvement improvement : report.getImprovements()) {
                String enhancement = improvement.getEnhancement();

                if (!improvements.containsKey(enhancement)) {
                    improvements.put(enhancement, new ArrayList<>());
                }

                List<Improvement> improvementsList = improvements.get(enhancement);

                //improvementsList.add(improvement);

                if (improvement.getResult() != null && !improvement.getResult().isEmpty()) {
                    improvementsList.add(improvement);
                }
            }
        }
    }

    public int getCompliantCount(String criterion) {
        return compliant.get(criterion).size();
    }

    public int getNonCompliantCount(String criterion) {
        return nonCompliant.get(criterion).size();
    }

    public int getImprovementsCount(String enhancement) {
        return improvements.get(enhancement).size();
    }

    public int getNonCompliantAfterImprovementsCount(String criterion, String enhancement) {
        return nonCompliant.get(criterion).size() - improvements.get(enhancement).size();
    }

    public Set<String> getEnhancements() {
        return improvements.keySet();
    }

    public Set<String> getCompliantCriterion() {
        return compliant.keySet();
    }

    public Set<String> getNonCompliantCriterion() {
        return nonCompliant.keySet();
    }
}
