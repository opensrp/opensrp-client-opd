package org.smartregister.opd.fragment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.opd.BaseUnitTest;
import org.smartregister.opd.R;
import org.smartregister.opd.utils.OpdConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class OpdProfileVisitsFragmentTest extends BaseUnitTest {

    private FragmentScenario<OpdProfileVisitsFragment> fragmentScenario;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private Context opensrpContext;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        doReturn(opensrpContext).when(opensrpContext).updateApplicationContext(any(android.content.Context.class));
        doReturn(opensrpContext).when(coreLibrary).context();
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(UUID.randomUUID().toString(), new HashMap<>(), "John Doe");
        Bundle bundle = new Bundle();
        bundle.putSerializable(OpdConstants.IntentKey.CLIENT_OBJECT, commonPersonObjectClient);
        FragmentFactory fragmentFactory = new FragmentFactory();
        fragmentScenario = FragmentScenario.launch(OpdProfileVisitsFragment.class, bundle, R.style.AppTheme, fragmentFactory);
    }

    @Test
    public void testShowPageCountTextShouldUpdatePageCounterText() {
        assertNotNull(fragmentScenario);
        fragmentScenario.onFragment(opdProfileVisitsFragment -> {
            TextView pageCounter = WhiteboxImpl.getInternalState(opdProfileVisitsFragment, "pageCounter");
            String pageCounterTemplate = opdProfileVisitsFragment.getString(R.string.current_page_of_total_pages);
            assertEquals(pageCounterTemplate, pageCounter.getText());

            opdProfileVisitsFragment.showPageCountText(String.format(pageCounterTemplate, 1, 1));

            assertEquals(String.format(pageCounterTemplate, 1, 1), pageCounter.getText());
        });
    }

    @Test
    public void testShowNextPageBtnShouldShowNextBtnAndMakeClickable() {
        assertNotNull(fragmentScenario);
        fragmentScenario.onFragment(opdProfileVisitsFragment -> {
            Button nextPageBtn = WhiteboxImpl.getInternalState(opdProfileVisitsFragment, "nextPageBtn");
            assertEquals(View.INVISIBLE, nextPageBtn.getVisibility());

            opdProfileVisitsFragment.showNextPageBtn(true);

            assertEquals(View.VISIBLE, nextPageBtn.getVisibility());
            assertTrue(nextPageBtn.isClickable());
        });
    }

    @Test
    public void testShowPreviousPageBtnShouldShowPreviousBtnAndMakeClickable() {
        assertNotNull(fragmentScenario);
        fragmentScenario.onFragment(opdProfileVisitsFragment -> {
            Button previousPageBtn = WhiteboxImpl.getInternalState(opdProfileVisitsFragment, "previousPageBtn");
            assertEquals(View.INVISIBLE, previousPageBtn.getVisibility());

            opdProfileVisitsFragment.showPreviousPageBtn(true);

            assertEquals(View.VISIBLE, previousPageBtn.getVisibility());
            assertTrue(previousPageBtn.isClickable());
        });
    }

    @Test
    public void testOnActionReceiveShouldInvokeOnResumptionMethod() {
        assertNotNull(fragmentScenario);
        fragmentScenario.onFragment(opdProfileVisitsFragment -> {
            OpdProfileVisitsFragment spyOpdProfileVisitsFragment = spy(opdProfileVisitsFragment);

            doNothing().when(spyOpdProfileVisitsFragment).onResumption();

            spyOpdProfileVisitsFragment.onActionReceive();

            verify(spyOpdProfileVisitsFragment).onResumption();
        });
    }

    @Test
    public void testDisplayVisitsShouldUpdateRecyclerView() {
        assertNotNull(fragmentScenario);
        fragmentScenario.onFragment(opdProfileVisitsFragment -> {
            RecyclerView recyclerView = WhiteboxImpl.getInternalState(opdProfileVisitsFragment, "recyclerView");
            assertNull(recyclerView.getAdapter());
            assertNull(recyclerView.getLayoutManager());

            opdProfileVisitsFragment.displayVisits(new ArrayList<>(), new ArrayList<>());

            assertNotNull(recyclerView);
            assertNotNull(recyclerView.getAdapter());
            assertNotNull(recyclerView.getLayoutManager());
        });
    }
}
