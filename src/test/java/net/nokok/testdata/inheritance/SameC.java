package net.nokok.testdata.inheritance;

public class SameC extends SameB {

    @Override
    public void injectPublic() {
        throw new IllegalStateException();
    }
}
