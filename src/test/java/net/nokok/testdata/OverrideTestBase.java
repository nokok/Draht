package net.nokok.testdata;

import javax.inject.Inject;

public class OverrideTestBase {
    public boolean injectedAll;
    public boolean injected1;

    @Inject
    void setInjectedAll() {
        this.injectedAll = true;
    }

    @Inject
    void setInjected1() {
        this.injected1 = true;
    }
}
