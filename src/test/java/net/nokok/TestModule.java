package net.nokok;

import net.nokok.draft.DraftModule;

import javax.inject.Named;
import java.time.format.DateTimeFormatter;

interface A {

}

class B implements A {

}

@DraftModule
public interface TestModule {
    B bind(A a);
}
