import net.nokok.draft.Module;
import net.nokok.testdata.Service;
import net.nokok.testdata.ServiceImpl;

@Module
interface M {
    ServiceImpl bindService(Service s1, Service s2);
}