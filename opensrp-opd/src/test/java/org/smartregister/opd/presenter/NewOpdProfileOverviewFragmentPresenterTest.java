package org.smartregister.opd.presenter;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.fragment.NewOpdProfileOverviewFragment;

@RunWith(RobolectricTestRunner.class)
public class NewOpdProfileOverviewFragmentPresenterTest extends BaseTest {

    @Before
    public void setUp() {
        NewOpdProfileOverviewFragment view = NewOpdProfileOverviewFragment.newInstance(null);
        NewOpdProfileOverviewFragmentPresenter presenter = (NewOpdProfileOverviewFragmentPresenter) Mockito.spy(new NewOpdProfileOverviewFragmentPresenter().with(view));
    }
    
    
}
