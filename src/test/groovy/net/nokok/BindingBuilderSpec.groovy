package net.nokok

import net.nokok.draft.BindingBuilder
import net.nokok.draft.AtNamed
import net.nokok.draft.DraftProvider
import net.nokok.draft.SimpleBinding
import spock.lang.Specification

class BindingBuilderSpec extends Specification {

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
        bindings.get(0) == new SimpleBinding([], A, B)
    }

    def "testTestModuleDefaultMethod"() {
        def bindingBuilder = new BindingBuilder(TestModuleDefaultMethod)
        def bindings = bindingBuilder.getBindings()

        expect:
        !bindings.isEmpty()
        bindings.size() == 1
        bindings.get(0) == net.nokok.draft.Binding.withProvider([AtNamed.from("title")], String, String, new DraftProvider<>("AppTitle"))
    }
}
