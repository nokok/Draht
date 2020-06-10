package net.nokok.testdata;

import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.Seat;

import javax.inject.Inject;
import javax.inject.Provider;

public class TestData1 {
    private final Provider<Seat> seatProvider;

    @Inject
    public TestData1(@Drivers Provider<Seat> seatProvider) {
        this.seatProvider = seatProvider;
    }

    public Seat get() {
        return this.seatProvider.get();
    }
}
