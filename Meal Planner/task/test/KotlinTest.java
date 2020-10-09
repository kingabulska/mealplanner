import mealplanner.MainKt;
import org.hyperskill.hstest.stage.StageTest;

public abstract class KotlinTest<T> extends StageTest<T> {

    protected final Class<?> classUnderTest;

    public KotlinTest() {
        super(MainKt.class);
        classUnderTest = MainKt.class;
    }
}