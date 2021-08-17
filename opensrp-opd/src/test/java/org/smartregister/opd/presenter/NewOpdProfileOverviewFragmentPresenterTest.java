package org.smartregister.opd.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.fragment.NewOpdProfileOverviewFragment;
import org.smartregister.util.GenericInteractor;

@RunWith(RobolectricTestRunner.class)
public class NewOpdProfileOverviewFragmentPresenterTest extends BaseTest {

    private NewOpdProfileOverviewFragmentPresenter presenter;
    
    @Before
    public void setUp() {
        NewOpdProfileOverviewFragment view = NewOpdProfileOverviewFragment.newInstance(null);
        presenter = (NewOpdProfileOverviewFragmentPresenter) Mockito.spy(new NewOpdProfileOverviewFragmentPresenter().with(view));
    }

    @Test
    public void getCallableInteractShouldReturnGenericInteract() {
        Assert.assertEquals(GenericInteractor.class, presenter.getCallableInteractor().getClass());
    }

    @Test
    public void getViewSHouldReturnNewOpdProfileOverviewFragment() {
        Assert.assertEquals(NewOpdProfileOverviewFragment.class, presenter.getView().getClass());
    }
    
    
}
