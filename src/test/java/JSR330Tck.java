import junit.framework.Test;
import junit.framework.TestCase;
import net.nokok.draft.Injector;
import net.nokok.testdata.JSRModule;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;

public class JSR330Tck extends TestCase {

    public static Test suite() {
        Car car = Injector.fromModule(JSRModule.class).getInstance(Car.class);
        return Tck.testsFor(car, false, true);
    }
}
