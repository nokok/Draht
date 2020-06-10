package net.nokok

import net.nokok.draft.Key
import org.atinject.tck.auto.accessories.Cupholder
import spock.lang.Specification

class KeySpec extends Specification {
    def "testSingleton"() {
        def k = Key.of(Cupholder)

        expect:
        k.isSingletonRequired()
    }
}
