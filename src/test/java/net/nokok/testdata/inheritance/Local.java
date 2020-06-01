package net.nokok.testdata.inheritance;

import net.nokok.draft.DraftModule;

import javax.inject.Named;

@DraftModule
public interface Local extends Production {

    @Named("DatabaseUrl")
    @Override
    default String database() {
        return "localhost";
    }
}
