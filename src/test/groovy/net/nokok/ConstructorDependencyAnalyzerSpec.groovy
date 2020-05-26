package net.nokok

import net.nokok.draft.Dependencies
import net.nokok.draft.Key
import net.nokok.draft.analyzer.ConstructorDependencyAnalyzer
import spock.lang.Specification

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
        dependencies.keys == [Key.of(Dependency)]
    }
}
