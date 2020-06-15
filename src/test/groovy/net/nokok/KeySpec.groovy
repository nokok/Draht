package net.nokok

import net.nokok.draft.AtNamed
import net.nokok.draft.Key
import org.atinject.tck.auto.accessories.Cupholder
import spock.lang.Specification

class KeySpec extends Specification {
    def "testSingleton"() {
        def k = Key.of(Cupholder)

        expect:
        k.isSingletonRequired()
    }

    def "testAnnotatedKey"() {
        def k = Key.of("Title", String)

        expect:
        k.annotations == [AtNamed.from("Title")]
        k.keyAsRawType == String
    }
}
