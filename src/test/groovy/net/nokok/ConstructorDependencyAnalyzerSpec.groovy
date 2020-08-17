package net.nokok

import net.nokok.draft.Dependencies
import net.nokok.draft.Key
import net.nokok.draft.ParameterizedTypeImpl
import net.nokok.draft.analyzer.ConstructorDependencyAnalyzer
import net.nokok.testdata.Dependency
import net.nokok.testdata.EmptyInterface
import net.nokok.testdata.OneConstructor
import net.nokok.testdata.OneConstructorWithDependency
import net.nokok.testdata.OneConstructorWithInject
import net.nokok.testdata.OneConstructorWithTypeParameter
import net.nokok.testdata.TestData1
import org.atinject.tck.auto.Convertible
import spock.lang.Specification

import java.lang.reflect.Type

class ConstructorDependencyAnalyzerSpec extends Specification {
    def "testWithOneConstructor"() {
        def analyzer = new ConstructorDependencyAnalyzer(OneConstructor)
        Dependencies dependencies = analyzer.runAnalyze()

        expect:
        dependencies.isEmpty()
    }

    def "testOneConstructorWithInject"() {
        def analyzer = new ConstructorDependencyAnalyzer(OneConstructorWithInject)
        Dependencies dependencies = analyzer.runAnalyze()

        expect:
        dependencies.isEmpty()
    }

    def "testOneConstructorWithDependency"() {
        def analyzer = new ConstructorDependencyAnalyzer(OneConstructorWithDependency)
        Dependencies dependencies = analyzer.runAnalyze()

        expect:
        dependencies.dependencyKeys == [Key.of(Dependency)]
    }

    def "testGenericType"() {
        def analyzer = new ConstructorDependencyAnalyzer(OneConstructorWithTypeParameter)
        Dependencies dependencies = analyzer.runAnalyze()

        expect:
        dependencies.dependencyKeys == [Key.of(new ParameterizedTypeImpl(List, [String] as Type[]))]
    }

    def "testInterface"() {
        def analyzer = new ConstructorDependencyAnalyzer(EmptyInterface)

        when:
        analyzer.runAnalyze()

        then:
        notThrown(IllegalArgumentException)
    }
}
