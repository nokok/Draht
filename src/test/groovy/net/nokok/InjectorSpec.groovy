package net.nokok

import net.nokok.draft.AtNamed
import net.nokok.draft.Injector
import net.nokok.draft.Key
import net.nokok.draft.LazyDraftProvider
import net.nokok.draft.ParameterizedTypeImpl
import net.nokok.testdata.FinalField
import net.nokok.testdata.JSRModule
import net.nokok.testdata.Local
import net.nokok.testdata.OneConstructorWithProvider
import net.nokok.testdata.OneConstructorWithProviderModule
import net.nokok.testdata.Production
import net.nokok.testdata.Repository
import net.nokok.testdata.Service
import net.nokok.testdata.ServiceImpl
import net.nokok.testdata.TestData2
import org.atinject.tck.auto.DriversSeat
import org.atinject.tck.auto.Engine
import org.atinject.tck.auto.Seat
import org.atinject.tck.auto.Tire
import org.atinject.tck.auto.accessories.Cupholder
import org.atinject.tck.auto.accessories.SpareTire
import spock.lang.Specification

import javax.inject.Provider

class InjectorSpec extends Specification {
    def "testProvider"() {
        def injector = Injector.fromModule(OneConstructorWithProviderModule)

        def p = injector.getInstance(OneConstructorWithProvider)

        expect:
        p instanceof OneConstructorWithProvider
        p.getService() instanceof ServiceImpl
    }

    def "testSingleton"() {
        def injector = Injector.fromModule(JSRModule)
        def cupHolder1 = injector.getInstance(Cupholder)
        def cupHolder2 = injector.getInstance(Cupholder)

        expect:
        cupHolder1 != null
        cupHolder2 != null
        cupHolder1.is(cupHolder2)
    }

    def "testEngine"() {
        def injector = Injector.fromModule(JSRModule)
        def engine = injector.getInstance(Engine)

        expect:
        engine != null
    }

    def "testSingletonProvider"() {
        def injector = Injector.fromModule(JSRModule)
        Provider<Cupholder> p = injector.getInstance(new ParameterizedTypeImpl(Provider, Cupholder))

        expect:
        p instanceof LazyDraftProvider
        p.get().is(p.get())
    }

    def "testQualifiedValue"() {
        def injector = Injector.fromModule(JSRModule)
        def spareTire = injector.getInstance(Key.of([AtNamed.from("spare")], Tire))

        expect:
        spareTire instanceof SpareTire
    }

    def "testOverrideSingleton"() {
        def injector = Injector.fromModule(JSRModule)
        Provider<Seat> driversSeatProvider = injector.getInstance(new ParameterizedTypeImpl(Provider, DriversSeat))

        expect:
        driversSeatProvider.get() instanceof DriversSeat
        !driversSeatProvider.get().is(driversSeatProvider.get())
    }

    def "testMethodInjection"() {
        def injector = Injector.fromModule(JSRModule)
        def t2 = injector.getInstance(TestData2)

        expect:
        t2.getSeat() != null
        t2.getSeat() instanceof DriversSeat
    }

    def "testMethodInjection2"() {
        def injector = Injector.fromModule(JSRModule)
        def t2 = injector.getInstance(TestData2)

        expect:
        t2.getSeat2() != null
        t2.getSeat2() instanceof DriversSeat
    }

    def "testLocal"() {
        def injector = Injector.fromModule(Local)
        def repo = injector.getInstance(Repository)

        expect:
        repo.url.contains("localhost")
    }

    def "testFinalField"() {
        def injector = Injector.fromModule(Production)
        def f = injector.getInstance(FinalField)

        expect:
        f.injected == null
    }

}
