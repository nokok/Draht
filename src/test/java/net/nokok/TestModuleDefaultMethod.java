package net.nokok;

import net.nokok.draft.DraftModule;

import javax.inject.Named;

@DraftModule
public interface TestModuleDefaultMethod {

    default String registerTitle(@Named("title") String t) {
        return "AppTitle";
    }

}
