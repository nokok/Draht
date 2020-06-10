package net.nokok.testdata;

import javax.inject.Provider;

public class OneConstructorWithProvider {

    private final Service s;

    public OneConstructorWithProvider(Provider<Service> s) {
        this.s = s.get();
    }

    public Service getService() {
        return s;
    }
}
