package idea.verlif.test;

import idea.verlif.comparator.CompareCore;
import idea.verlif.comparator.EqualJudge;
import idea.verlif.comparator.diff.DiffValue;
import idea.verlif.comparator.diff.Different;
import idea.verlif.comparator.diff.Type;
import idea.verlif.mock.data.MockDataCreator;
import idea.verlif.mock.data.config.FieldOption;
import idea.verlif.mock.data.config.MockDataConfig;
import idea.verlif.mock.data.creator.InstanceCreator;
import idea.verlif.mock.data.creator.data.DictDataCreator;
import idea.verlif.mock.data.creator.data.DoubleRandomCreator;
import idea.verlif.mock.data.creator.data.IntegerRandomCreator;
import idea.verlif.mock.data.domain.MockSrc;
import idea.verlif.test.domain.Person;
import idea.verlif.test.domain.Pet;
import idea.verlif.test.domain.Student;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 配置数据生成器
        MockDataCreator creator = new MockDataCreator();
        MockDataConfig config = creator.getConfig();
        config.autoCascade(true).arraySize(5);
        config.acceptFieldOption(FieldOption.ALLOWED_NULL | FieldOption.ALLOWED_CLASS);
        config.instanceCreator(new PetInstanceCreator());
        config.fieldValue(Person::getAge, new IntegerRandomCreator(6, 28));
        config.fieldValue(Person::getNominalAge, new IntegerRandomCreator(-92, -8));
        config.fieldValue(new MyDoubleRandomCreator(20, 100));
        config.fieldValue(new DictDataCreator<>(new String[]{"哈哈", "嘻嘻", "嘿嘿", "呵呵", "桀桀", "吼吼"}));
        // 数据生成
        Person old = creator.mock(Person.class);
        Student now = creator.mock(Student.class);
        // 初始化比较器
        CompareCore compareCore = new CompareCore();
        // 添加自定义List对象比较器
        compareCore.addEqualJudge(List.class, new ListEqualJudge());
        Different different = compareCore.compare(old, now);
        List<DiffValue> changedValues = different.getChangedValues();
        for (DiffValue changedValue : changedValues) {
            printDiff(changedValue);
        }
    }

    private static void printDiff(DiffValue diffValue) {
        StringBuilder stb = new StringBuilder(diffValue.getName());
        switch (diffValue.getType()) {
            case MODIFY_VALUE:
                stb.append(" - 修改 - ").append(diffValue.getOld()).append(" > ").append(diffValue.getNow());
                break;
            case NEW_FIELD:
                stb.append(" - 新增属性 - ").append(diffValue.getNow().getClass().getName());
                break;
            case MODIFY_FILL:
                stb.append(" - 填充 - ").append(diffValue.getNow());
                break;
            case MODIFY_NULL:
                stb.append(" - 置空 - ");
                break;
            case MODIFY_TYPE:
                stb.append(" - 类型转变 - ").append(diffValue.getOld().getClass().getName()).append(" > ").append(diffValue.getNow().getClass().getName());
                break;
            case DROPPED_FIELD:
                stb.append(" - 属性去除");
                break;
            default:
                stb.append(" - 无变化");
                break;
        }
        System.out.println(stb);
    }

    private static final class PetInstanceCreator implements InstanceCreator<Pet> {
        @Override
        public Class<?> matched() {
            return Pet.class;
        }

        @Override
        public Pet newInstance() {
            return new Pet("测试");
        }
    }

    private static final class ListEqualJudge implements EqualJudge<List> {
        @Override
        public Type equals(List old, List now) {
            if (old == now) {
                return null;
            }
            if (old == null) {
                return Type.MODIFY_FILL;
            } else if (now == null) {
                return Type.MODIFY_NULL;
            } else {
                if (old.size() == now.size()) {
                    for (int i = 0; i < old.size(); i++) {
                        Object oldVal = old.get(i);
                        Object nowVal = now.get(i);
                        if (oldVal == nowVal) {
                            continue;
                        }
                        if (oldVal == null || !oldVal.equals(nowVal)) {
                            return Type.MODIFY_VALUE;
                        }
                    }
                }
                return null;
            }
        }
    }

    private static final class MyDoubleRandomCreator extends DoubleRandomCreator {
        public MyDoubleRandomCreator() {
        }

        public MyDoubleRandomCreator(double min, double max) {
            super(min, max);
        }

        @Override
        public Double mock(MockSrc mockSrc, MockDataCreator.Creator creator) {
            Double mock = super.mock(mockSrc, creator);
            return BigDecimal.valueOf(mock).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
    }
}
