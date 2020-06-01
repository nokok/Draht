package net.nokok.testdata;

import net.nokok.draft.DraftModule;

@DraftModule
public interface TestModule {
    ServiceImpl bind(Service a);
}
