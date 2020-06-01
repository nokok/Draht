import net.nokok.draft.DraftModule;

interface S {

}

class I {

}

@DraftModule
interface M {
    I bind(S s);
}