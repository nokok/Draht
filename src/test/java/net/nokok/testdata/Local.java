package net.nokok.testdata;

import net.nokok.draft.Module;

import javax.inject.Named;

@Module
public interface Local extends Production {
    @Named("DatabaseUrl")
    @Override
    default String databaseUrl() {
        return "jdbc:mysql://localhost:3306/db";
    }
}
