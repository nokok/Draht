package net.nokok

import net.nokok.draft.ParameterizedType
import spock.lang.Specification

import java.lang.reflect.Type

class A {
    class Inner<T> {

    }

    static class StaticInner<T> {

    }
}

class ParameterizedTypeSpec extends Specification {
    def "testSimpleClass"() {
        when:
        new ParameterizedType(String, [] as Type[])

        then:
        thrown(IllegalArgumentException)
    }

    def "testTypeParameterCount"() {
        when:
        def p = new ParameterizedType(List, [String] as Type[])

        then:
        notThrown(IllegalArgumentException)

        expect:
        p.rawType == List
        p.actualTypeArguments == [String] as Type[]
        p.ownerType == null
    }

    def "testTypeParameterCountMismatch"() {
        when:
        new ParameterizedType(Map, [String, String, String] as Type[])

        then:
        thrown(IllegalArgumentException)
    }

    def "testNestedClass"() {
        when:
        def p = new ParameterizedType(A.Inner, [String] as Type[])

        then:
        notThrown(IllegalArgumentException)

        expect:
        p.rawType == A.Inner
        p.actualTypeArguments == [String] as Type[]
        p.ownerType == A
    }

    def "testStaticNestedClass"() {
        when:
        def p = new ParameterizedType(A.StaticInner, [String] as Type[])

        then:
        notThrown(IllegalArgumentException)

        expect:
        p.rawType == A.StaticInner
        p.actualTypeArguments == [String] as Type[]
        p.ownerType == A
    }
}
