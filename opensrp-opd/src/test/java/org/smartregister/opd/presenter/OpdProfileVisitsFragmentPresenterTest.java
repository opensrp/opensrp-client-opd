package org.smartregister.opd.presenter;

import android.support.v4.util.Pair;

import org.jeasy.rules.api.Facts;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.opd.BaseTest;
import org.smartregister.opd.contract.OpdProfileVisitsFragmentContract;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.pojos.OpdVisitSummary;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-12-02
 */

public class OpdProfileVisitsFragmentPresenterTest extends BaseTest {

    private OpdProfileVisitsFragmentPresenter presenter;

    @Mock
    private OpdProfileVisitsFragmentContract.View view;

    @Before
    public void setUp() throws Exception {
        presenter = Mockito.spy(new OpdProfileVisitsFragmentPresenter(view));
    }

    @Test
    public void onDestroyShouldCallInteractorOnDestroy() {
        boolean isChangingConfiguration = false;
        OpdProfileVisitsFragmentContract.Interactor interactor = Mockito.mock(OpdProfileVisitsFragmentContract.Interactor.class);
        ReflectionHelpers.setField(presenter, "mProfileInteractor", interactor);

        presenter.onDestroy(isChangingConfiguration);

        Mockito.verify(interactor, Mockito.times(1)).onDestroy(Mockito.eq(isChangingConfiguration));
        assertNull(ReflectionHelpers.getField(presenter, "mProfileInteractor"));
    }

    @Test
    public void loadVisitsShouldCallInteractorFetchVisits() {
        String baseEntityId = "98-sd-ewsdf";
        OpdProfileVisitsFragmentContract.Interactor interactor = Mockito.mock(OpdProfileVisitsFragmentContract.Interactor.class);
        ReflectionHelpers.setField(presenter, "mProfileInteractor", interactor);
        ReflectionHelpers.setField(presenter, "currentPageNo", 0);
        final List<OpdVisitSummary> opdVisitSummaries = new ArrayList<>();

        OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback onFinishedCallback = Mockito.mock(OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                OpdProfileVisitsFragmentContract.Presenter.OnVisitsLoadedCallback callback = invocationOnMock.getArgument(2);
                callback.onVisitsLoaded(opdVisitSummaries);
                return null;
            }
        }).when(interactor).fetchVisits(Mockito.eq(baseEntityId), Mockito.eq(0), Mockito.any(OpdProfileVisitsFragmentContract.Presenter.OnVisitsLoadedCallback.class));

        presenter.loadVisits(baseEntityId, onFinishedCallback);

        Mockito.verify(interactor, Mockito.times(1)).fetchVisits(Mockito.eq(baseEntityId), Mockito.eq(0)
                , Mockito.any(OpdProfileVisitsFragmentContract.Presenter.OnVisitsLoadedCallback.class));
        Mockito.verify(presenter, Mockito.times(1)).populateWrapperDataAndFacts(Mockito.eq(opdVisitSummaries), Mockito.any(ArrayList.class));
        Mockito.verify(onFinishedCallback, Mockito.times(1)).onFinished(Mockito.eq(opdVisitSummaries), Mockito.any(ArrayList.class));
    }

    @Test
    public void loadPageCounterShouldCallUpdatePageCounterAndViewMethodsWhenInteractorIsNotNull() {
        String baseEntityId = "98-sd-ewsdf";
        OpdProfileVisitsFragmentContract.Interactor interactor = Mockito.mock(OpdProfileVisitsFragmentContract.Interactor.class);
        ReflectionHelpers.setField(presenter, "mProfileInteractor", interactor);
        ReflectionHelpers.setField(presenter, "currentPageNo", 0);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                OpdProfileVisitsFragmentContract.Interactor.OnFetchVisitsPageCountCallback callback
                        = invocationOnMock.getArgument(1);
                callback.onFetchVisitsPageCount(2);
                return null;
            }
        }).when(interactor).fetchVisitsPageCount(Mockito.eq(baseEntityId), Mockito.any(OpdProfileVisitsFragmentContract.Interactor.OnFetchVisitsPageCountCallback.class));
        Mockito.doReturn("Page %s of %s").when(view).getString(Mockito.anyInt());

        presenter.loadPageCounter(baseEntityId);

        Mockito.verify(interactor, Mockito.times(1)).fetchVisitsPageCount(Mockito.eq(baseEntityId)
                , Mockito.any(OpdProfileVisitsFragmentContract.Interactor.OnFetchVisitsPageCountCallback.class));
        Mockito.verify(view, Mockito.times(1)).showPreviousPageBtn(Mockito.eq(false));
        Mockito.verify(view, Mockito.times(1)).showNextPageBtn(Mockito.eq(true));
        Mockito.verify(view, Mockito.times(1)).showPageCountText(Mockito.eq("Page 1 of 2"));
    }

    @Test
    public void onNextPageClickedShouldCallLoadVisitsWhenCurrentPageIsLessThanCurrentPage() {
        String baseEntityId = "98-sd-ewsdf";
        Mockito.doReturn(baseEntityId).when(view).getClientBaseEntityId();
        ReflectionHelpers.setField(presenter, "currentPageNo", 0);
        ReflectionHelpers.setField(presenter, "totalPages", 2);

        // Mock call to loadVisits
        final List<OpdVisitSummary> opdVisitSummaries = new ArrayList<>();
        final ArrayList<Pair<YamlConfigWrapper, Facts>> items = new ArrayList<>();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback callback = invocationOnMock.getArgument(1);
                callback.onFinished(opdVisitSummaries, items);
                return null;
            }
        }).when(presenter).loadVisits(Mockito.eq(baseEntityId), Mockito.any(OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback.class));

        presenter.onNextPageClicked();

        Mockito.verify(presenter, Mockito.times(1)).loadVisits(Mockito.eq(baseEntityId), Mockito.any(OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback.class));
        Mockito.verify(view, Mockito.times(1)).displayVisits(Mockito.eq(opdVisitSummaries), Mockito.eq(items));
        assertEquals(1, (int) ReflectionHelpers.getField(presenter, "currentPageNo"));
    }

    @Test
    public void onPreviousPageClickedShouldCallLoadWhenVisitsCurrentPageIsGreaterThanZero() {
        String baseEntityId = "98-sd-ewsdf";
        Mockito.doReturn(baseEntityId).when(view).getClientBaseEntityId();
        ReflectionHelpers.setField(presenter, "currentPageNo", 1);

        // Mock call to loadVisits
        final List<OpdVisitSummary> opdVisitSummaries = new ArrayList<>();
        final ArrayList<Pair<YamlConfigWrapper, Facts>> items = new ArrayList<>();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback callback = invocationOnMock.getArgument(1);
                callback.onFinished(opdVisitSummaries, items);
                return null;
            }
        }).when(presenter).loadVisits(Mockito.eq(baseEntityId), Mockito.any(OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback.class));

        presenter.onPreviousPageClicked();

        Mockito.verify(presenter, Mockito.times(1)).loadVisits(Mockito.eq(baseEntityId), Mockito.any(OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback.class));
        Mockito.verify(view, Mockito.times(1)).displayVisits(Mockito.eq(opdVisitSummaries), Mockito.eq(items));
        assertEquals(0, (int) ReflectionHelpers.getField(presenter, "currentPageNo"));
    }

    @Test
    public void getStringShouldReturnNullWhenProfileViewIsNull() {
        view = null;
        assertNull(presenter.getString(923));
    }

    @Test
    public void getStringShouldCallProfileViewGetStringWhenProfileViewIsNotNull() {
        int stringId = 82983;
        presenter.getString(stringId);
        Mockito.verify(view, Mockito.times(1)).getString(Mockito.eq(stringId));
    }
}
