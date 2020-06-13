package net.nokok.testdata.inheritance.b;

import net.nokok.testdata.InjectRequired;
import net.nokok.testdata.Service;
import net.nokok.testdata.inheritance.Base;

import javax.inject.Inject;
import java.util.logging.Logger;

public class Derived2 extends Base {

    private static final Logger logger = Logger.getLogger(Derived2.class.getName());

    @InjectRequired
    @Inject
    void setServicePackagePrivate(Service service) {
        logger.info("Derived2#setServicePackagePrivate injected");
    }
}
