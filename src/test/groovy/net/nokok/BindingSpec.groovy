package net.nokok

import net.nokok.draft.Binding
import net.nokok.draft.Key
import net.nokok.testdata.Service
import net.nokok.testdata.ServiceImpl
import spock.lang.Specification

class BindingSpec extends Specification {
    def "testFactoryMethod(of)"() {
        def binding = Binding.of([], Service, ServiceImpl)

        expect:
        !binding.hasProvider()
        binding.annotations == []
        binding.bindFrom == Service
        binding.bindTo == ServiceImpl
        binding.key == Key.of(Service)
    }

    def "testFactoryMethod(withProvider)"() {
        def binding = Binding.withProvider([], Service, ServiceImpl, { it -> new ServiceImpl() })

        expect:
        binding.hasProvider()
        binding.provider != null
        binding.annotations == []
        binding.bindFrom == Service
        binding.bindTo == ServiceImpl
        binding.key == Key.of(Service)
    }
}
