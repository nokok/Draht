import net.nokok.draft.DraftModule;
import net.nokok.testdata.Service;
import net.nokok.testdata.ServiceImpl;

@DraftModule
interface Annot1 {
    ServiceImpl s(Service s);

    ServiceImpl s2();
}