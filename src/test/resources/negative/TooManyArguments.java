import net.nokok.draft.DraftModule;
import net.nokok.testdata.Service;
import net.nokok.testdata.ServiceImpl;

@DraftModule
interface M {
    ServiceImpl bindService(Service s1, Service s2);
}