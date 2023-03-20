package idea.verlif.comparator;

import java.util.List;

public class Different {

    private List<DiffValue> diffValues;

    public List<DiffValue> getDiffValues() {
        return diffValues;
    }

    public void setDiffValues(List<DiffValue> diffValues) {
        this.diffValues = diffValues;
    }

    public static final class DiffValue {

        private String name;

        private String old;

        private String now;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOld() {
            return old;
        }

        public void setOld(String old) {
            this.old = old;
        }

        public String getNow() {
            return now;
        }

        public void setNow(String now) {
            this.now = now;
        }
    }
}
