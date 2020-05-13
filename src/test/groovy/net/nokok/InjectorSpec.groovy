package net.nokok

import net.nokok.inject.Injector
import static net.nokok.inject.Injector.syntax.*;
import spock.lang.Specification

class InjectorSpec extends Specification {
    interface A {}

    static class B implements A {

    }

    def "inject"() {
        def injector = new Injector(
                bind(A).to(B)
        )
        A a = injector.getInstance(A)

        expect:
        a instanceof A
        a instanceof B
    }

    static class C implements A {
        private final B b;

        C(B b) {
            this.b = b;
        }
    }

    def "inject with dependency"() {
        def injector = new Injector(
                bind(A).to(C),
                bind(B).to(B)
        )
        A a = injector.getInstance(A)

        expect:
        a instanceof C
        C c = (C) a
        c.b != null
    }
}
