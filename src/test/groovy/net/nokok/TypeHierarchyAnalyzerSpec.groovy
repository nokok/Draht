package net.nokok

import net.nokok.draft.analyzer.TypeHierarchyAnalyzer
import net.nokok.draft.internal.TypeHierarchy
import net.nokok.testdata.inheritance.Base
import net.nokok.testdata.inheritance.Derived1
import spock.lang.Specification

class TypeHierarchyAnalyzerSpec extends Specification {
    def "runAnalyze"() {
        def analyzer = new TypeHierarchyAnalyzer(clazz)

        expect:
        analyzer.runAnalyze() == expected

        where:
        clazz     || expected
        String    || new TypeHierarchy([String])
        Derived1  || new TypeHierarchy([Base, Derived1])
        ArrayList || new TypeHierarchy([AbstractCollection, AbstractList, ArrayList])
    }
}
