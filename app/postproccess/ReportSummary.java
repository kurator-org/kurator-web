package postproccess;

import org.kurator.akka.data.DQReport.DQReport;
import org.kurator.akka.data.DQReport.Improvement;
import org.kurator.akka.data.DQReport.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lowery on 4/5/16.
 */
public class ReportSummary {
    private List<DQReport> reports;
    private Map<String, Validation> compliantValidations = new HashMap<>();
    private Map<String, Validation> nonCompliantValidations = new HashMap<>();

    private Map<String, Improvement> improvements = new HashMap<>();

    public ReportSummary(List<DQReport> reports) {
        this.reports = reports;

        for (DQReport report : reports) {
            for (Validation validation : report.getValidations()) {
                if (validation.getResult().equals("Compliant")) {
                    compliantValidations.put(validation.getCriterion(), validation);
                } else if (validation.getResult().equals("Not Compliant")) {
                    nonCompliantValidations.put(validation.getCriterion(), validation);
                }
            }

            for (Improvement improvement : report.getImprovements()) {
                if (!improvement.getResult().isEmpty()) {
                    improvements.put(improvement.getEnhancement(), improvement);
                }
            }
        }
    }
}
