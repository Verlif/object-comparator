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
                    .filter(diffValue -> diffValue.type != Type.NO)
                    .collect(Collectors.toList());
        }
        return changedValues;
    }

    public void addDiffValue(DiffValue diffValue) {
        if (prefix == null) {
            diffValues.add(diffValue);
        } else {
            diffValue = diffValue.clone();
            diffValue.setName(prefix + diffValue.getName());
            diffValues.add(diffValue);
        }
    }

    public void addDifferent(Different different) {
        for (DiffValue diffValue : different.diffValues) {
            addDiffValue(diffValue);
        }
    }

    public static final class DiffValue implements Cloneable {

        private String name;

        private Object old;

        private Object now;

        private Type type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getOld() {
            return old;
        }

        public void setOld(Object old) {
            this.old = old;
        }

        public Object getNow() {
            return now;
        }

        public void setNow(Object now) {
            this.now = now;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        @Override
        public DiffValue clone() {
            DiffValue diffValue = new DiffValue();
            diffValue.name = this.name;
            diffValue.type = this.type;
            diffValue.old = this.old;
            diffValue.now = this.now;
            return diffValue;
        }
    }
}
