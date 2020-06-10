import net.nokok.draft.Module;

interface S {

}

class I {

}

@Module
interface M {
    I bind(S s);
}