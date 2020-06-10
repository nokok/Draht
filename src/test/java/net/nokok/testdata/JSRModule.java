package net.nokok.testdata;

import net.nokok.draft.Module;
import org.atinject.tck.auto.*;
import org.atinject.tck.auto.accessories.SpareTire;

import javax.inject.Named;

@Module
public interface JSRModule {
    Convertible bindCar(Car car);

    DriversSeat bindSeat(@Drivers Seat seat);

    V8Engine bindEngine(Engine engine);

    SpareTire bindTire(@Named("spare") Tire tire);
}