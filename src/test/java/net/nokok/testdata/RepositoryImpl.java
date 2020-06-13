package net.nokok.testdata;

import javax.inject.Inject;
import javax.inject.Named;

public class RepositoryImpl implements Repository {

    private final String url;

    @Inject
    public RepositoryImpl(@Named("DatabaseUrl") String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return this.url;
    }
}
