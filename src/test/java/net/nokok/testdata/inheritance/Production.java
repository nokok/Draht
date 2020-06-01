package net.nokok.testdata.inheritance;


import net.nokok.draft.DraftModule;

import javax.inject.Named;

@DraftModule
public interface Production {
    @Named("DatabaseUrl")
    default String database() {
        return "db1";
    }
}
