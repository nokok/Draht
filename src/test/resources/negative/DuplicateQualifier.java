import net.nokok.draft.DraftModule;
import net.nokok.testdata.Service;
import net.nokok.testdata.ServiceImpl;

import javax.inject.Named;

@DraftModule
interface DuplicateQualifier {
    @Named("A")
    ServiceImpl s(@Named("B") Service s);
}
