package net.nokok.testdata.inheritance;

import net.nokok.testdata.inheritance.b.AnotherB;

import java.util.logging.Logger;

public class AnotherC extends AnotherB {
    private static final Logger logger = Logger.getLogger(AnotherC.class.getName());

    void injectPackagePrivate2() {
        throw new IllegalStateException();
    }

    @Override
    public void injectPublic() {
        throw new IllegalStateException();
    }
}
