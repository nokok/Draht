package net.nokok.testdata.inheritance.b;

import net.nokok.testdata.InjectRequired;
import net.nokok.testdata.inheritance.AnotherA;

import javax.inject.Inject;
import java.util.logging.Logger;

public class AnotherB extends AnotherA {
    private static final Logger logger = Logger.getLogger(AnotherB.class.getName());

    @InjectRequired
    @Inject
    protected void injectProtected() {
        logger.info("AnotherB#injectProtected");
    }

    void injectPackagePrivate() {
        throw new IllegalStateException();
    }

    @InjectRequired
    @Inject
    void injectPackagePrivate1() {
        logger.info("AnotherB#injectPackagePrivate1");
    }

    @InjectRequired
    @Inject
    private void injectPrivate1() {
        logger.info("AnotherB#injectPrivate1");
    }

    private void injectPrivate2() {
        throw new IllegalStateException();
    }

    void injectPackagePrivate3() {
        throw new IllegalStateException();
    }

    @Inject
    @Override
    public void injectPublic() {
        throw new IllegalStateException();
    }
}
