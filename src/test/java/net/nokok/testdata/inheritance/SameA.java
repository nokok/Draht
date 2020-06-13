package net.nokok.testdata.inheritance;

import net.nokok.testdata.InjectRequired;

import javax.inject.Inject;
import java.util.logging.Logger;

public class SameA {
    private static final Logger logger = Logger.getLogger(SameA.class.getName());

    @InjectRequired
    @Inject
    protected void injectProtected() {
        logger.info("SameA#injectProtected");
    }

    @InjectRequired
    @Inject
    void injectPackagePrivate() {
        logger.info("SameA#injectPackagePrivate");
    }

    @InjectRequired
    @Inject
    void injectPackagePrivate1() {
        logger.info("SameA#injectPackagePrivate1");
    }

    @InjectRequired
    @Inject
    private void injectPrivate1() {
        logger.info("SameA#injetPrivate1");
    }

    @InjectRequired
    @Inject
    private void injectPrivate2() {
        logger.info("SameA#injetPrivate2");
    }

    @Inject
    void injectPackagePrivateOverrideBySameB() {
        throw new IllegalStateException();
    }

    @InjectRequired
    @Inject
    private void injectPrivate3() {
        logger.info("SameA#injectPrivate3");
    }

    @Inject
    void injectPackagePrivate2() {
        throw new IllegalStateException();
    }

    @InjectRequired
    @Inject
    void injectPackagePrivate3() {
        logger.info("SameA#injectPackagePrivate3");
    }

    @Inject
    public void injectPublic() {
        throw new IllegalStateException();
    }
}
