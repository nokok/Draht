package net.nokok.testdata.inheritance;

import net.nokok.testdata.InjectRequired;
import net.nokok.testdata.Service;

import javax.inject.Inject;
import java.util.logging.Logger;

public class Derived1 extends Base {

    private static final Logger logger = Logger.getLogger(Derived1.class.getName());

    @InjectRequired
    @Override
    @Inject
    public void setService(Service service) {
        logger.info("Derived#setService injected");
    }

    @InjectRequired
    @Override
    @Inject
    void setServicePackagePrivate(Service service) {
        logger.info("Derived1#setServicePackagePrivate injected");
    }

}
