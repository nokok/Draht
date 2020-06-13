package net.nokok.testdata.inheritance;

import net.nokok.testdata.InjectRequired;
import net.nokok.testdata.Service;

import javax.inject.Inject;
import java.util.logging.Logger;

public class Base {

    private static final Logger logger = Logger.getLogger(Base.class.getName());

    @InjectRequired
    @Inject
    public void setService(Service service) {
        logger.info("Base#setService injected");
    }

    @InjectRequired
    @Inject
    private void setServicePrivate(Service service) {
        logger.info("Base#setServicePrivate injected");
    }

    @InjectRequired
    @Inject
    void setServicePackagePrivate(Service service) {
        logger.info("Base#setServicePackagePrivate injected");
    }

    public void noInjectAnnotationPublic(Service service) {
        throw new IllegalStateException("Unexpected Injection Base#noInjectAnnotation");
    }

    void noInjectAnnotationPackagePrivate(Service service) {
        throw new IllegalStateException("Unexpected Injection Base#noInjectAnnotationPackagePrivate");
    }

    protected void noInjectAnnotationProtected(Service service) {
        throw new IllegalStateException("Unexpected Injection Base#noInjectAnnotationProtected");
    }

    private void noInjectAnnotationPrivate(Service service) {
        throw new IllegalStateException("Unexpected Injection Base#noInjectAnnotationPrivate");
    }
}
