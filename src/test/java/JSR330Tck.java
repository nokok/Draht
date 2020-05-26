import junit.framework.Test;
import junit.framework.TestCase;
import net.nokok.draft.DraftModule;
import net.nokok.draft.Injector;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.*;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;

import javax.inject.Named;

public class JSR330Tck extends TestCase {
    @DraftModule
    interface MyModule {

        Convertible getConvertible(Car car);

        Seat getSeat();

        DriversSeat getDriversSeat(@Drivers Seat seat);

        Tire bindTire();

        SpareTire bindSpareTire();

        SpareTire bindTire(@Named("spare") Tire tire);

        V8Engine bindEngine(Engine engine);

        Cupholder bindCupHolder();

        FuelTank bindFuelTank();
    }

    public static Test suite() {
        Car car = Injector.fromModules(MyModule.class).getInstance(Car.class);
        return Tck.testsFor(car, true, true);
    }
}
