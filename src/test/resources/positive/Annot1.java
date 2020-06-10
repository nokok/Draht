import net.nokok.draft.Module;
import net.nokok.testdata.Service;
import net.nokok.testdata.ServiceImpl;

@Module
interface Annot1 {
    ServiceImpl s(Service s);

    ServiceImpl s2();
}