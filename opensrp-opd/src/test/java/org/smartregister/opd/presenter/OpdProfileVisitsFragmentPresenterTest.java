package org.smartregister.opd.presenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

import androidx.core.util.Pair;

import org.jeasy.rules.api.Facts;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.opd.BaseUnitTest;
import org.smartregister.opd.R;
import org.smartregister.opd.contract.OpdProfileVisitsFragmentContract;
import org.smartregister.opd.domain.YamlConfig;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.pojo.OpdVisitSummary;
import org.smartregister.opd.pojo.OpdVisitSummaryResultModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-12-02
 */

public class OpdProfileVisitsFragmentPresenterTest extends BaseUnitTest {

    private OpdProfileVisitsFragmentPresenter presenter;

    @Mock
    private OpdProfileVisitsFragmentContract.View view;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = Mockito.spy(new OpdProfileVisitsFragmentPresenter(view));
    }

    @Test
    public void onDestroyShouldCallInteractorOnDestroy() {
        boolean isChangingConfiguration = false;
        OpdProfileVisitsFragmentContract.Interactor interactor = mock(OpdProfileVisitsFragmentContract.Interactor.class);
        ReflectionHelpers.setField(presenter, "mProfileInteractor", interactor);

        presenter.onDestroy(isChangingConfiguration);

        Mockito.verify(interactor, Mockito.times(1)).onDestroy(Mockito.eq(isChangingConfiguration));
        assertNull(ReflectionHelpers.getField(presenter, "mProfileInteractor"));
    }

    @Test
    public void loadVisitsShouldCallInteractorFetchVisits() {
        String baseEntityId = "98-sd-ewsdf";
        OpdProfileVisitsFragmentContract.Interactor interactor = mock(OpdProfileVisitsFragmentContract.Interactor.class);
        ReflectionHelpers.setField(presenter, "mProfileInteractor", interactor);
        ReflectionHelpers.setField(presenter, "currentPageNo", 0);
        final List<OpdVisitSummary> opdVisitSummaries = new ArrayList<>();

        OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback onFinishedCallback = mock(OpdProfileVisitsFragmentContract.Presenter.OnFinishedCallback.class);
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
        OpdProfileVisitsFragmentContract.Interactor interactor = mock(OpdProfileVisitsFragmentContract.Interactor.class);
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

    @Test
    public void testGenerateTestTextShouldGenerateTextCorrectly() {
        HashMap<String, List<OpdVisitSummaryResultModel.Test>> tests = new HashMap<>();
        List<OpdVisitSummaryResultModel.Test> hepatitisBTests = new ArrayList<>();
        OpdVisitSummaryResultModel.Test test = new OpdVisitSummaryResultModel.Test();
        test.setType("Hepatitis B");
        test.setName("status");
        test.setResult("Negative");
        hepatitisBTests.add(test);

        List<OpdVisitSummaryResultModel.Test> hepatitisCTests = new ArrayList<>();
        OpdVisitSummaryResultModel.Test test2 = new OpdVisitSummaryResultModel.Test();
        test2.setType("Hepatitis C");
        test2.setName("status");
        test2.setResult("Negative");
        hepatitisCTests.add(test2);

        tests.put("Hepatitis B", hepatitisBTests);
        tests.put("Hepatitis C", hepatitisCTests);
        OpdProfileVisitsFragmentContract.View view = mock(OpdProfileVisitsFragmentContract.View.class);
        Mockito.when(view.getString(R.string.single_test_result_visit_preview_summary))
                .thenReturn("%s%s");
        Mockito.when(view.getString(R.string.single_test_visit_preview_summary))
                .thenReturn("<![CDATA[<b><font color=\\'black\\'>%s</font><br/></b>]]>");
        OpdProfileVisitsFragmentPresenter profileVisitsFragmentPresenter = new OpdProfileVisitsFragmentPresenter(view);

        String result = profileVisitsFragmentPresenter.generateTestText(tests);
        String expected = "<![CDATA[<b><font color=\\'black\\'>Hepatitis C</font><br/></b>]]>negative<br/><br/><![CDATA[<b><font color=\\'black\\'>Hepatitis B</font><br/></b>]]>negative<br/><br/>";
        assertEquals(expected, result);
    }

    @Test
    public void testPopulateWrapperDataAndFactsShouldPopulateDataWrapper() throws Exception {
        OpdVisitSummaryResultModel.Treatment treatment = new OpdVisitSummaryResultModel.Treatment();
        treatment.setDosage("500mg");
        treatment.setDuration("20 days");
        treatment.setMedicine("Cetrizen");
        treatment.setFrequency("1 x 1");
        OpdVisitSummary opdVisitSummary = new OpdVisitSummary();
        opdVisitSummary.setVisitDate(new Date());
        opdVisitSummary.setDiagnosis("No Diagnosis");
        opdVisitSummary.setDiagnosisType("referred");
        opdVisitSummary.setIsDiagnosisSame("no");
        opdVisitSummary.setTreatmentType("referred");
        opdVisitSummary.setTreatmentTypeSpecify("other");
        opdVisitSummary.addTreatment(treatment);

        ArrayList<Pair<YamlConfigWrapper, Facts>> items = new ArrayList<>();

        YamlConfigItem yamlConfigItem = new YamlConfigItem();
        yamlConfigItem.setHtml(true);
        yamlConfigItem.setTemplate("{diagnosis_same_label}: {diagnosis_same}");
        yamlConfigItem.setIsMultiWidget(false);

        YamlConfig yamlConfig = new YamlConfig();
        yamlConfig.setGroup("Group A");
        yamlConfig.setTestResults("none");
        yamlConfig.setFields(Collections.singletonList(yamlConfigItem));

        Mockito.doReturn(getString(R.string.single_medicine_visit_preview_summary)).when(presenter).getString(eq(R.string.single_medicine_visit_preview_summary));
        Mockito.doReturn(getString(R.string.dose_or_duration_html)).when(presenter).getString(eq(R.string.dose_or_duration_html));
        Mockito.doReturn(getString(R.string.medication_frequency)).when(presenter).getString(eq(R.string.medication_frequency));
        Mockito.doReturn(getString(R.string.medication_duration)).when(presenter).getString(eq(R.string.medication_duration));

        Mockito.doReturn(Collections.singletonList(yamlConfig)).when(presenter).getVisitRowRuleObjects();

        presenter.populateWrapperDataAndFacts(Collections.singletonList(opdVisitSummary), items);

        assertEquals(1, items.size());
        assertEquals(yamlConfig.getGroup(), items.get(0).first.getGroup());
        assertEquals(opdVisitSummary.getDiagnosisType(), items.get(0).second.get("diagnosis_type"));
        assertEquals(opdVisitSummary.getTreatmentType(), items.get(0).second.get("treatment_type"));
        assertEquals(opdVisitSummary.getTreatmentTypeSpecify(), items.get(0).second.get("treatment_type_specify"));
        assertEquals(String.format("<b><font color='black'>%s</font><br/></b><font color='#7f7f7f'>Duration: %s Frequency: %s</font>", treatment.getMedicine(), treatment.getDuration(), treatment.getFrequency()), items.get(0).second.get("treatment"));
    }
}
