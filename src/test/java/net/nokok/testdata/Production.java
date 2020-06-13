package net.nokok.testdata;

import net.nokok.draft.Module;

import javax.inject.Named;

@Module
public interface Production {
    RepositoryImpl bind(Repository s);

    @Named("DatabaseUrl")
    default String databaseUrl() {
        return "jdbc:mysql://prod-db:3306/db";
    }
}