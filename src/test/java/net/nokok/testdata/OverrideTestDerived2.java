package net.nokok.testdata;

import javax.inject.Inject;

public class OverrideTestDerived2 extends OverrideTestDerived1 {
    public boolean injected;
    public boolean injected1;

    @Inject
    void setInjectedAll() {
        this.injected = true;
    }

    void setInjected1() {
        this.injected1 = true;
    }

    public void derived2Only() {

    }
}
