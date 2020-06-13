package net.nokok

import net.nokok.draft.internal.TypeHierarchy
import net.nokok.testdata.inheritance.SameB
import net.nokok.testdata.inheritance.SameC
import spock.lang.Specification

class TypeHierarchySpec extends Specification {
    def "testSorted"() {
        def h = new TypeHierarchy([Object, String])

        expect:
        h.topDownOrder() == [Object, String]
        h.bottomUpOrder() == [String, Object]
    }

    def "testMixedHierarchy"() {
        def h = new TypeHierarchy([A, SameC, SameB])

        expect:
        h.topDownOrder() == [A, SameB, SameC]
        h.bottomUpOrder() == [SameC, SameB, A]
    }

    def "subTypes"() {
        def h = new TypeHierarchy([A, SameB, SameC])

        expect:
        h.subTypes(SameB) == [SameC]
        h.subTypes(SameC) == []
    }
}
