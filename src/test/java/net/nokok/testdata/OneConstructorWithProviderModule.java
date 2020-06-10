package net.nokok.testdata;

import net.nokok.draft.Module;

@Module
public interface OneConstructorWithProviderModule {
    ServiceImpl bind(Service s);

    OneConstructorWithProvider get();
}
