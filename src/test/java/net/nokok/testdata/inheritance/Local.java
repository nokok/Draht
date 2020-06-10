package net.nokok.testdata.inheritance;

import net.nokok.draft.Module;

import javax.inject.Named;

@Module
public interface Local extends Production {

    @Named("DatabaseUrl")
    @Override
    default String database() {
        return "localhost";
    }
}
