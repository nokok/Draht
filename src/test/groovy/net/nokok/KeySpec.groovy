package net.nokok


import net.nokok.inject.Key
import spock.lang.Specification

import javax.inject.Named
import java.lang.annotation.Annotation
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class KeySpec extends Specification {

    private TestData testData = new TestData()

    def "simple key"() {
        def key = Key.of(Object.class)

        expect:
        key.rawType == Object.class
        key.type == Object.class
    }

    def "Generic key"() {
        def type = testData.getClass().getDeclaredField("stringList").getGenericType()
        def key = Key.of(type)

        expect:
        key.rawType == List.class
        key.type instanceof ParameterizedType
        key.type.getActualTypeArguments() == [String.class] as Type[]
    }

    def "String key"() {
        def key = Key.of(String.class, "name")

        expect:
        key.type == String.class
        key.qualifiedName == "name"
    }

    def "annotated key"() {
        def field = testData.getClass().getDeclaredField("title")
        List<Class<? extends Annotation>> annotations = field.getAnnotations().collect { it.annotationType() }
        def actual = Key.of(field.getGenericType(), annotations)
        def expected = Key.of(String.class, [Qualified.class])

        expect:
        actual == expected
    }

    def "hasQualifier"() {
        expect:
        Key.hasQualifier(testData.getClass().getDeclaredField("title"))
    }

    def "same named annotation"() {
        def a = testData.getClass().getDeclaredField("a").getDeclaredAnnotation(Named.class)
        def b = testData.getClass().getDeclaredField("b").getDeclaredAnnotation(Named.class)
        def i = testData.getClass().getDeclaredField("i").getDeclaredAnnotation(Named.class)

        expect:
        a != b
        a == i
    }
}
