package net.nokok


import com.google.testing.compile.JavaFileObjects
import net.nokok.draft.analyzer.ModuleAnalyzer
import spock.lang.Specification

import java.nio.file.Paths

import static com.google.common.truth.Truth.assert_
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource

class ModuleAnalyzerSpec extends Specification {
    def "positive1"() {
        def moduleAnalyzer = new ModuleAnalyzer()

        expect:
        assert_().about(javaSource())
                .that(JavaFileObjects.forResource(Paths.get("positive", "Annot1.java").toString()))
                .processedWith(moduleAnalyzer)
                .compilesWithoutError()
    }

    def "testEmptyModule"() {
        def moduleAnalyzer = new ModuleAnalyzer()
        def java = JavaFileObjects.forResource(Paths.get("negative", "EmptyModule.java").toString())

        expect:
        assert_().about(javaSource())
                .that(java)
                .processedWith(moduleAnalyzer)
                .compilesWithoutError().withWarningContaining("No bindings found").in(java).onLine(4)
    }

    def "testTooManyArguments"() {
        def moduleAnalyzer = new ModuleAnalyzer()
        def java = JavaFileObjects.forResource(Paths.get("negative", "TooManyArguments.java").toString())

        expect:
        assert_().about(javaSource())
                .that(java)
                .processedWith(moduleAnalyzer)
                .failsToCompile().withErrorContaining("Too many arguments").in(java).onLine(7)
    }

    def "testIncompatibleBinding"() {
        def moduleAnalyzer = new ModuleAnalyzer()
        def java = JavaFileObjects.forResource(Paths.get("negative", "IncompatibleBindings.java").toString())

        expect:
        assert_().about(javaSource())
                .that(java)
                .processedWith(moduleAnalyzer)
                .failsToCompile().withErrorContaining("Incompatible binding types").in(java).onLine(13)
    }
}
