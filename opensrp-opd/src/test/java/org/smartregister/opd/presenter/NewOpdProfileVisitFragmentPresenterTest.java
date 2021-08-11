package org.smartregister.opd.presenter;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.fragment.NewOpdProfileVisitsFragment;
import org.smartregister.util.JsonFormUtils;

import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class NewOpdProfileVisitFragmentPresenterTest extends BaseTest {
    
    private NewOpdProfileVisitsFragmentPresenter presenter;
    private NewOpdProfileVisitsFragment view;

    @Before
    public void setUp() {
        view  = NewOpdProfileVisitsFragment.newInstance(null);
        presenter = (NewOpdProfileVisitsFragmentPresenter) Mockito.spy(new NewOpdProfileVisitsFragmentPresenter().with(view));
    }
    
    
}
