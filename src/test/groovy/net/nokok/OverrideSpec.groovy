package net.nokok

import net.nokok.draft.Injector
import net.nokok.testdata.inheritance.SameC
import spock.lang.Specification

class OverrideSpec extends Specification {

    def "testOverride"() {
        expect:
        Injector.newInstance().getInstance(SameC) != null
    }
}
