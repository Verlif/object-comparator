package idea.verlif.comparator.diff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Different {

    private final String prefix;
    private final List<DiffValue> diffValues;

    private List<DiffValue> changedValues;

    public Different() {
        this(null);
    }

    public Different(String prefix) {
        this.prefix = prefix;
        this.diffValues = new ArrayList<>();
    }

    public List<DiffValue> getDiffValues() {
        return diffValues;
    }

    public List<DiffValue> getChangedValues() {
        if (changedValues == null) {
            changedValues = diffValues.stream()
                    .filter(diffValue -> diffValue.getType() != Type.NO)
                    .collect(Collectors.toList());
        }
        return changedValues;
    }

    public void addDiffValue(DiffValue diffValue) {
        if (prefix == null) {
            diffValues.add(diffValue);
        } else {
            diffValue = diffValue.copy();
            diffValue.setName(prefix + diffValue.getName());
            diffValues.add(diffValue);
        }
    }

    public void addDifferent(Different different) {
        for (DiffValue diffValue : different.diffValues) {
            addDiffValue(diffValue);
        }
    }

}
