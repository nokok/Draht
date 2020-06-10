package net.nokok.testdata.inheritance;


import net.nokok.draft.Module;

import javax.inject.Named;

@Module
public interface Production {
    @Named("DatabaseUrl")
    default String database() {
        return "db1";
    }
}
