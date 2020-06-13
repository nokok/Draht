package net.nokok.testdata.inheritance;

import net.nokok.testdata.InjectRequired;

import javax.inject.Inject;
import java.util.logging.Logger;

public class AnotherA {
    private static final Logger logger = Logger.getLogger(AnotherA.class.getName());

    @Inject
    protected void injectProtected() {
        throw new IllegalStateException();
    }

    @InjectRequired
    @Inject
    void injectPackagePrivate() {
        logger.info("AnotherA#injectPackagePrivate");
    }

    @InjectRequired
    @Inject
    void injectPackagePrivate1() {
        logger.info("AnotherA#injectPackagePrivate1");
    }

    @InjectRequired
    @Inject
    private void injectPrivate1() {
        logger.info("AnotherA#injectPrivate1");
    }

    @InjectRequired
    @Inject
    private void injectPrivate2() {
        logger.info("AnotherA#injectPrivate2");
    }

    @InjectRequired
    @Inject
    void injectPackagePrivateOverrideBySameB() {
        logger.info("AnotherA#injectPackagePrivateOverrideBySameB");
    }

    @InjectRequired
    @Inject
    private void injectPrivate3() {
        logger.info("AnotherA#injectPrivate3");
    }

    @Inject
    void injectPackagePrivate2() {
        throw new IllegalStateException();
    }

    @InjectRequired
    @Inject
    void injectPackagePrivate3() {
        logger.info("AnotherA#injectPackagePrivate3");
    }

    @Inject
    public void injectPublic() {
        throw new IllegalStateException();
    }
}
