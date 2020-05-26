package net.nokok.draft;

import javax.inject.Named;

public interface AtNamed {
    public static Named from(String name) {
        return new NamedImpl(name);
    }
}
