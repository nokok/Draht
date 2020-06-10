package net.nokok.testdata;

import javax.inject.Inject;

public class OverrideTestDerived1 extends OverrideTestBase {
    public boolean injected;
    public boolean injected1;

    @Inject
    void setInjectedAll() {
        this.injected = true;
    }


    @Inject
    void setInjected1() {
        this.injected1 = true;
    }
}
