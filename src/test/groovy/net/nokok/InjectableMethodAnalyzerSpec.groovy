package net.nokok

import net.nokok.draft.InjectableMethod
import net.nokok.draft.analyzer.InjectableMethodAnalyzer
import net.nokok.testdata.InjectRequired
import net.nokok.testdata.Service
import net.nokok.testdata.ServiceImpl
import net.nokok.testdata.inheritance.AnotherA
import net.nokok.testdata.inheritance.AnotherC
import net.nokok.testdata.inheritance.Base
import net.nokok.testdata.inheritance.Derived1
import net.nokok.testdata.inheritance.b.AnotherB
import org.atinject.tck.auto.accessories.SpareTire
import spock.lang.Specification

class InjectableMethodAnalyzerSpec extends Specification {
    def "runAnalyzeForEmptyClass"() {
        def analyzer = InjectableMethodAnalyzer.newAnalyzer(ServiceImpl)
        def injectableMethods = analyzer.runAnalyze()

        expect:
        injectableMethods == []
    }

    def "runAnalyzeForBase"() {
        def analyzer = InjectableMethodAnalyzer.newAnalyzer(Base)
        def injectableMethods = analyzer.runAnalyze()

        expect:
        injectableMethods.size() == 3
        injectableMethods.any { it == new InjectableMethod(Base, Base.getDeclaredMethod("setService", Service)) }
        injectableMethods.any { it == new InjectableMethod(Base, Base.getDeclaredMethod("setServicePrivate", Service)) }
        injectableMethods.any { it == new InjectableMethod(Base, Base.getDeclaredMethod("setServicePackagePrivate", Service)) }
    }

    def "runAnalyzeForDerived1"() {
        def analyzer = InjectableMethodAnalyzer.newAnalyzer(Derived1)
        def injectableMethods = analyzer.runAnalyze()

        expect:
        injectableMethods.size() == 3
        injectableMethods[0] == new InjectableMethod(Base, Base.getDeclaredMethod("setServicePrivate", Service))
        injectableMethods.any { it == new InjectableMethod(Base, Base.getDeclaredMethod("setServicePrivate", Service)) }
        injectableMethods.any { it == new InjectableMethod(Derived1, Derived1.getDeclaredMethod("setService", Service)) }
        injectableMethods.any { it == new InjectableMethod(Derived1, Derived1.getDeclaredMethod("setServicePackagePrivate", Service)) }
    }

    def "runAnalyzerForA"() {
        def analyzer = InjectableMethodAnalyzer.newAnalyzer(AnotherC)
        def injectableMethods = analyzer.runAnalyze()

        expect:
        injectableMethods.size() == 10
        injectableMethods.any { it == new InjectableMethod(AnotherA, AnotherA.getDeclaredMethod("injectPackagePrivate")) }
        injectableMethods.any { it == new InjectableMethod(AnotherA, AnotherA.getDeclaredMethod("injectPackagePrivate1")) }
        injectableMethods.any { it == new InjectableMethod(AnotherA, AnotherA.getDeclaredMethod("injectPrivate1")) }
        injectableMethods.any { it == new InjectableMethod(AnotherA, AnotherA.getDeclaredMethod("injectPrivate2")) }
        injectableMethods.any { it == new InjectableMethod(AnotherA, AnotherA.getDeclaredMethod("injectPackagePrivateOverrideBySameB")) }
        injectableMethods.any { it == new InjectableMethod(AnotherA, AnotherA.getDeclaredMethod("injectPrivate3")) }
        injectableMethods.any { it == new InjectableMethod(AnotherA, AnotherA.getDeclaredMethod("injectPackagePrivate3")) }

        injectableMethods.any { it == new InjectableMethod(AnotherB, AnotherB.getDeclaredMethod("injectProtected")) }
        injectableMethods.any { it == new InjectableMethod(AnotherB, AnotherB.getDeclaredMethod("injectPackagePrivate1")) }
        injectableMethods.any { it == new InjectableMethod(AnotherB, AnotherB.getDeclaredMethod("injectPrivate1")) }
    }
}
