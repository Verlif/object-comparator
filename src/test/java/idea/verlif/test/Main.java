package idea.verlif.test;

import idea.verlif.comparator.CompareCore;
import idea.verlif.comparator.diff.DiffValue;
import idea.verlif.comparator.diff.Different;
import idea.verlif.test.domain.Person;
import idea.verlif.test.domain.Pet;
import idea.verlif.test.domain.Student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Person old = new Person();
        Student now = new Student();
        now.setBirthday(new Date());
        now.setPet(new Pet("小狗"));
        List<String> list = new ArrayList<>();
        list.add("123");
        now.setNames(list);
        old.setPet(new Pet("小猫"));
        List<String> list2 = new ArrayList<>();
        list2.add("1234");
        old.setNames(list2);
        CompareCore compareCore = new CompareCore();
        Different different = compareCore.compare(old, now);
        List<DiffValue> changedValues = different.getChangedValues();
        System.out.println(Arrays.toString(changedValues.toArray()));
    }
}
