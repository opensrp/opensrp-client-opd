package org.smartregister.opd.fragment;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;

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
import org.smartregister.opd.activity.BaseOpdProfileActivity;
import org.smartregister.opd.utils.OpdConstants;

import java.util.HashMap;
import java.util.UUID;

public class OpdProfileOverviewFragmentTest extends BaseUnitTest {

    private FragmentScenario<OpdProfileOverviewFragment> fragmentScenario;

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
        fragmentScenario = FragmentScenario.launch(OpdProfileOverviewFragment.class, bundle, R.style.AppTheme, fragmentFactory);
    }

    @Test
    public void testShowDiagnoseAndTreatBtnShouldOpenForm() {
        assertNotNull(fragmentScenario);
        fragmentScenario.onFragment(opdProfileOverviewFragment -> {
            OpdProfileOverviewFragment spyOpdProfileOverviewFragment = spy(opdProfileOverviewFragment);
            BaseOpdProfileActivity mockBaseOpdProfileActivity = mock(BaseOpdProfileActivity.class);
            Resources resources = opdProfileOverviewFragment.getResources();
            doReturn(resources).when(mockBaseOpdProfileActivity).getResources();
            doReturn(mockBaseOpdProfileActivity).when(spyOpdProfileOverviewFragment).getActivity();
            try {
                WhiteboxImpl.invokeMethod(spyOpdProfileOverviewFragment, "showDiagnoseAndTreatBtn");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Button checkInDiagnoseAndTreatBtn = ReflectionHelpers.getField(spyOpdProfileOverviewFragment, "checkInDiagnoseAndTreatBtn");
            checkInDiagnoseAndTreatBtn.performClick();
            verify(mockBaseOpdProfileActivity).openDiagnoseAndTreatForm();
        });
    }

    @Test
    public void testShowCheckInBtnShouldOpenForm() {
        assertNotNull(fragmentScenario);
        fragmentScenario.onFragment(opdProfileOverviewFragment -> {
            OpdProfileOverviewFragment spyOpdProfileOverviewFragment = spy(opdProfileOverviewFragment);
            BaseOpdProfileActivity mockBaseOpdProfileActivity = mock(BaseOpdProfileActivity.class);
            Resources resources = opdProfileOverviewFragment.getResources();
            doReturn(resources).when(mockBaseOpdProfileActivity).getResources();
            doReturn(mockBaseOpdProfileActivity).when(spyOpdProfileOverviewFragment).getActivity();
            try {
                WhiteboxImpl.invokeMethod(spyOpdProfileOverviewFragment, "showCheckInBtn");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Button checkInDiagnoseAndTreatBtn = ReflectionHelpers.getField(spyOpdProfileOverviewFragment, "checkInDiagnoseAndTreatBtn");
            checkInDiagnoseAndTreatBtn.performClick();
            verify(mockBaseOpdProfileActivity).openCheckInForm();
        });
    }
}