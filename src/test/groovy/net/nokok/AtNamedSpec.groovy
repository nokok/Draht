package net.nokok

import net.nokok.draft.AtNamed
import net.nokok.draft.BindingBuilder
import net.nokok.draft.Module
import spock.lang.Specification

import javax.inject.Named

@Module
interface TestModule1 {

    @Named("foobar")
    String test();
}

class AtNamedSpec extends Specification {
    def "testEquals"() {
        def x = AtNamed.from("N")
        def y = AtNamed.from("N")
        def z = AtNamed.from("N")
        def i = AtNamed.from("aaaa")

        expect:
        x.equals(x)
        !x.equals(null)
        x.equals(y)
        y.equals(x)
        x.equals(z)
        y.equals(z)
        !x.equals(i)
        !i.equals(x)
        !y.equals(i)
        !z.equals(i)
    }

    def "equalsReal"() {
        def expect = AtNamed.from("foobar")
        def actual = new BindingBuilder(TestModule1).getBindings().get(0).annotations[0]

        expect:
        !expect.is(actual)
        expect == actual
    }

    def "testHashCode"() {
        def expect = AtNamed.from("foobar")
        def actual = new BindingBuilder(TestModule1).getBindings().get(0).annotations[0]

        expect:
        expect.hashCode() == actual.hashCode()
    }

    def "testAnnotationType"() {
        def named = AtNamed.from("foobar")

        expect:
        named.annotationType() == Named
    }
}
