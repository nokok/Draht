package net.nokok

import net.nokok.draft.BindingBuilder
import net.nokok.draft.AtNamed
import net.nokok.draft.DraftProvider
import net.nokok.draft.SimpleBinding
import net.nokok.testdata.OneConstructor
import net.nokok.testdata.inheritance.Local
import net.nokok.testdata.EmptyModule
import net.nokok.testdata.Service
import net.nokok.testdata.ServiceImpl
import net.nokok.testdata.TestModule
import net.nokok.testdata.TestModuleDefaultMethod
import spock.lang.Specification

class BindingBuilderSpec extends Specification {

    def "testInvalidModule"() {
        when:
        new BindingBuilder(module)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Module must be interface"

        where:
        module << [
                String,
                OneConstructor
        ]
    }

    def "testEmptyModule"() {
        def emptyBinding = new BindingBuilder(EmptyModule)

        expect:
        emptyBinding.getBindings().isEmpty()
    }

    def "testTestModule"() {
        def bindingBuilder = new BindingBuilder(TestModule)
        def bindings = bindingBuilder.getBindings()

        expect:
        !bindings.isEmpty()
        bindings.size() == 1
        bindings.get(0) == new SimpleBinding([], Service, ServiceImpl)
    }

    def "testTestModuleDefaultMethodWithQualifier"() {
        def bindingBuilder = new BindingBuilder(TestModuleDefaultMethod)
        def bindings = bindingBuilder.getBindings()

        expect:
        !bindings.isEmpty()
        bindings.size() == 1
        bindings.get(0) == net.nokok.draft.Binding.withProvider([AtNamed.from("title")], String, String, new DraftProvider<>("AppTitle"))
    }

    def "testDefaultMethodWithQualifierAndOverride"() {
        def bindingBuilder = new BindingBuilder(Local)
        def bindings = bindingBuilder.getBindings()

        expect:
        !bindings.isEmpty()
        bindings.size() == 1
        bindings.get(0) == net.nokok.draft.Binding.withProvider([AtNamed.from("DatabaseUrl")], String, String, new DraftProvider<>("localhost"))
    }
}
