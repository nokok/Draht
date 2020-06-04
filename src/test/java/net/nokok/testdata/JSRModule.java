package net.nokok.testdata;

import net.nokok.draft.DraftModule;
import org.atinject.tck.auto.*;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;

import javax.inject.Named;

@DraftModule
public interface JSRModule {
    Convertible bind(Car car);

    Seat seat();

    DriversSeat bind(@Drivers Seat seat);

    Tire tire();

    SpareTire spareTire();

    V8Engine bind(Engine engine);

    Tire bind(@Named("spare") Tire tire);

    Cupholder cupholder();

    FuelTank fuelTank();

}