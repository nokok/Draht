package net.nokok

import net.nokok.draft.Injector
import org.atinject.tck.auto.*
import org.atinject.tck.auto.accessories.Cupholder
import org.atinject.tck.auto.accessories.SpareTire
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Named

class MyJSRLikeSpec extends Specification {

    private Car car
    private Convertible convertible
    private Cupholder cupholder
    private SpareTire spareTire
    private Tire plainTire
    private Engine engine

    interface JSRModule {
        Convertible bind(Car car);

        Seat seat();

        DriversSeat bind(@Drivers Seat seat);

        Tire tire();

        SpareTire spareTire();

        V8Engine bind(Engine engine);

        Tire bind(@Named("spare") Tire tire);

        Cupholder cupholder();

        FuelTank fuelTank();

    }


    def setup() {
        def car = Injector.fromModules(JSRModule).getInstance(Car.class)
        cupholder = car.cupholder
        spareTire = car.spareTire
        plainTire = car.fieldPlainTire
        engine = car.engineProvider.get()
        convertible = (Convertible) car
    }

    def "testInject"() {
        expect:
        car instanceof Convertible
    }

    def "testFieldsInjected"() {
        expect:
        cupholder != null
        spareTire != null
    }

    def "testProviderReturnedValues"() {
        expect:
        engine != null
    }

    def "testMethodWithZeroParametersInjected"() {
        expect:
        car.methodWithZeroParamsInjected
    }

    def "testMethodWithMultipleParametersInjected"() {
        expect:
        car.methodWithMultipleParamsInjected
    }

    def "testNonVoidMethodInjected"() {
        expect:
        car.methodWithNonVoidReturnInjected
    }

    def "testPublicNoArgsConstructorInjected"() {
        expect:
        engine.publicNoArgsConstructorInjected
    }

    def testSubtypeFieldsInjected() {
        expect:
        spareTire.hasSpareTireBeenFieldInjected()
    }

    def testSubtypeMethodsInjected() {
        expect:
        spareTire.hasSpareTireBeenMethodInjected()
    }

    def testSupertypeFieldsInjected() {
        expect:
        spareTire.hasTireBeenFieldInjected()
    }

    def testSupertypeMethodsInjected() {
        expect:
        spareTire.hasTireBeenMethodInjected()
    }

    def testTwiceOverriddenMethodInjectedWhenMiddleLacksAnnotation() {
        expect:
        engine.overriddenTwiceWithOmissionInMiddleInjected
    }

    def testQualifiersNotInheritedFromOverriddenMethod() {
        expect:
        engine.seatA != null
        engine.seatB != null
        engine.tireA != null
        engine.tireB != null
        engine.seatA instanceof DriversSeat
        !engine.seatB instanceof DriversSeat
        engine.tireA instanceof SpareTire
        engine.tireB instanceof Tire
        !engine.qualifiersInheritedFromOverriddenMethod
    }
}
