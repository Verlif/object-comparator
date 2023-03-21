package idea.verlif.comparator.diff;


public class DiffValue implements Cloneable {

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

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", old=" + old +
                ", now=" + now +
                ", type=" + type +
                '}';
    }
}
