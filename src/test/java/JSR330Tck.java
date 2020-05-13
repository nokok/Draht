import junit.framework.Test;
import junit.framework.TestCase;
import net.nokok.inject.Injector;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.*;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;

import static net.nokok.inject.Injector.syntax.bind;
import static net.nokok.inject.Injector.syntax.register;

public class JSR330Tck extends TestCase {
    public static Test suite() {
        Car car = new Injector(
                bind(Car.class).to(Convertible.class),
                register(Seat.class),
                bind(Seat.class).qualifiedWith(Drivers.class).to(DriversSeat.class),
                register(Tire.class),
                register(SpareTire.class),
                bind(Engine.class).to(V8Engine.class),
                bind(Tire.class).qualifiedWith("spare").to(SpareTire.class),
                register(Cupholder.class),
                register(FuelTank.class)
        ).getInstance(Car.class);
        return Tck.testsFor(car, false, false);
    }
}
