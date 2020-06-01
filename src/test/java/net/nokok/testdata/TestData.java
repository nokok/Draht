package net.nokok.testdata;

import javax.inject.Named;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@interface Qualified {

}

public class TestData {
    public List<String> stringList = null;

    @Qualified
    public String title = null;

    @Named("a")
    public String a = null;

    @Named("a")
    public Integer i = null;

    @Named("b")
    public String b = null;

}
