package org.smartregister.opd.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.fragment.NewOpdProfileVisitsFragment;
import org.smartregister.util.GenericInteractor;

@RunWith(RobolectricTestRunner.class)
public class NewOpdProfileVisitFragmentPresenterTest extends BaseTest {
    
    private NewOpdProfileVisitsFragmentPresenter presenter;

    @Before
    public void setUp() {
        NewOpdProfileVisitsFragment view = NewOpdProfileVisitsFragment.newInstance(null);
        presenter = (NewOpdProfileVisitsFragmentPresenter) Mockito.spy(new NewOpdProfileVisitsFragmentPresenter().with(view));
    }

    @Test
    public void getCallableInteractShouldReturnGenericInteract() {
        Assert.assertEquals(GenericInteractor.class, presenter.getCallableInteractor().getClass());
    }


    @Test
    public void getViewShouldReturnNewOpdProfileVisitsFragment() {
        Assert.assertEquals(NewOpdProfileVisitsFragment.class, presenter.getView().getClass());
    }
    
}
