package net.nokok.testdata;

import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.Seat;

import javax.inject.Inject;
import javax.inject.Provider;

public class TestData2 {
    private Seat seat;
    private Seat seat2;

    @Inject
    public void setSeat(@Drivers Seat s) {
        this.seat = s;
    }

    public Seat getSeat() {
        return seat;
    }

    @Inject
    public void setSeat(@Drivers Provider<Seat> seat) {
        this.seat2 = seat.get();
    }

    public Seat getSeat2() {
        return seat2;
    }
}