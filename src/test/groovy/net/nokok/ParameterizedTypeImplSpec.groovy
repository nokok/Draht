package net.nokok

import net.nokok.draft.ParameterizedTypeImpl
import spock.lang.Specification

import java.lang.reflect.Type

class A {
    class Inner<T> {

    }

    static class StaticInner<T> {

    }
}

class ParameterizedTypeImplSpec extends Specification {
    def "testSimpleClass"() {
        when:
        new ParameterizedTypeImpl(String, [] as Type[])

        then:
        thrown(IllegalArgumentException)
    }

    def "testTypeParameterCount"() {
        when:
        def p = new ParameterizedTypeImpl(List, [String] as Type[])

        then:
        notThrown(IllegalArgumentException)

        expect:
        p.rawType == List
        p.actualTypeArguments == [String] as Type[]
        p.ownerType == null
    }

    def "testTypeParameterCountMismatch"() {
        when:
        new ParameterizedTypeImpl(Map, [String, String, String] as Type[])

        then:
        thrown(IllegalArgumentException)
    }

    def "testNestedClass"() {
        when:
        def p = new ParameterizedTypeImpl(A.Inner, [String] as Type[])

        then:
        notThrown(IllegalArgumentException)

        expect:
        p.rawType == A.Inner
        p.actualTypeArguments == [String] as Type[]
        p.ownerType == A
    }

    def "testStaticNestedClass"() {
        when:
        def p = new ParameterizedTypeImpl(A.StaticInner, [String] as Type[])

        then:
        notThrown(IllegalArgumentException)

        expect:
        p.rawType == A.StaticInner
        p.actualTypeArguments == [String] as Type[]
        p.ownerType == A
    }
}
