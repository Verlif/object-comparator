package idea.verlif.comparator;

import idea.verlif.reflection.util.FieldUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CompareObject {

    private final List<String> nameList;
    private final List<Field> fieldList;
    private final List<CompareField> compareFieldList;
    private final List<Object> valueList;

    private final Object target;
    private final Class<?> targetClass;

    private List<Field> comparableFieldList;

    public CompareObject(Object target) {
        this.nameList = new ArrayList<>();
        this.fieldList = new ArrayList<>();
        this.compareFieldList = new ArrayList<>();
        this.valueList = new ArrayList<>();

        this.target = target;
        if (target != null) {
            this.targetClass = target.getClass();
            List<Field> fields = FieldUtil.getAllFields(targetClass);
            try {
                for (Field field : fields) {
                    CompareField compareField = field.getAnnotation(CompareField.class);
                    nameList.add(getFieldName(field, compareField));
                    fieldList.add(field);
                    compareFieldList.add(compareField);
                    valueList.add(FieldUtil.getFieldValue(target, field));
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.targetClass = null;
        }
    }

    private String getFieldName(Field field, CompareField compareField) {
        if (compareField == null || compareField.value().length() == 0) {
            return field.getName();
        } else {
            return compareField.value();
        }
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public List<Field> getComparableFieldList() {
        if (comparableFieldList == null) {
            comparableFieldList = new ArrayList<>();
            for (int i = 0, size = compareFieldList.size(); i < size; i++) {
                CompareField compareField = compareFieldList.get(i);
                if (compareField == null || !compareField.ignored()) {
                    comparableFieldList.add(fieldList.get(i));
                }
            }
        }
        return comparableFieldList;
    }

    public Object getTarget() {
        return target;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getFieldName(Field field) {
        int i = fieldList.indexOf(field);
        if (i == -1) {
            return null;
        }
        return nameList.get(i);
    }

    public Field getField(String name) {
        int i = nameList.indexOf(name);
        if (i == -1) {
            return null;
        }
        return fieldList.get(i);
    }

    public CompareField getCompareField(Field field) {
        int i = fieldList.indexOf(field);
        if (i == -1) {
            return null;
        }
        return compareFieldList.get(i);
    }

    public Object getValue(Field field) {
        int i = fieldList.indexOf(field);
        if (i == -1) {
            return null;
        }
        return valueList.get(i);
    }

    public Object getValue(String name) {
        int i = nameList.indexOf(name);
        if (i == -1) {
            return null;
        }
        return valueList.get(i);
    }
}
