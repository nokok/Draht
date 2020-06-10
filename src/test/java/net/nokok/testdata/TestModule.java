package net.nokok.testdata;

import net.nokok.draft.Module;

@Module
public interface TestModule {
    ServiceImpl bind(Service a);
}
