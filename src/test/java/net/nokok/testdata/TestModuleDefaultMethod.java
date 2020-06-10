package net.nokok.testdata;

import net.nokok.draft.Module;

import javax.inject.Named;

@Module
public interface TestModuleDefaultMethod {

    default String registerTitle(@Named("title") String t) {
        return "AppTitle";
    }

}
