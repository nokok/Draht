import net.nokok.draft.Module;
import net.nokok.testdata.Service;
import net.nokok.testdata.ServiceImpl;

import javax.inject.Named;

@Module
interface DuplicateQualifier {
    @Named("A")
    ServiceImpl s(@Named("B") Service s);
}
