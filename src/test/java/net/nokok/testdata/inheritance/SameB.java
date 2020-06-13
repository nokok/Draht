package net.nokok.testdata.inheritance;

import net.nokok.testdata.InjectRequired;

import javax.inject.Inject;
import java.util.logging.Logger;

public class SameB extends SameA {
    private static final Logger logger = Logger.getLogger(SameB.class.getName());

    @InjectRequired
    @Override
    @Inject
    void injectPackagePrivateOverrideBySameB() {
        logger.info("SameB#injectPackagePrivateOverrideBySameB");
    }

    private void injectPrivate3() {
        throw new IllegalStateException();
    }

    @Override
    @Inject
    void injectPackagePrivate2() {
        logger.info("SameB#injectPackagePrivate2");
    }

    @Override
    @Inject
    public void injectPublic() {
        throw new IllegalStateException();
    }
}
