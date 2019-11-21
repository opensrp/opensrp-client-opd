package org.smartregister.opd;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.repository.Repository;

import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-24
 */
@RunWith(RobolectricTestRunner.class)
public class OpdLibraryTest extends BaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void initShouldCreateNewLibraryInstanceWhenInstanceIsNull() {
        assertNull(ReflectionHelpers.getStaticField(OpdLibrary.class, "instance"));

        OpdLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), BuildConfig.VERSION_CODE, 1);

        assertNotNull(ReflectionHelpers.getStaticField(OpdLibrary.class, "instance"));
    }

    @Test
    public void getInstanceShouldThrowIllegalStateException() throws Throwable {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Instance does not exist!!! Call org.smartregister.opd.OpdLibrary"
                + ".init method in the onCreate method of "
                + "your Application class");

        OpdLibrary.getInstance();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(OpdLibrary.class, "instance", null);
    }

    @Test
    public void getOpdRulesEngineHelperShouldReturnNonNull() {
        OpdLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), BuildConfig.VERSION_CODE, 1);

        assertNotNull(OpdLibrary.getInstance().getOpdRulesEngineHelper());
    }

    @Test
    public void getLatestValidCheckInDateShouldReturn1DayFromNow() {
        OpdLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), BuildConfig.VERSION_CODE, 1);

        long timeNow = new Date().getTime();

        Assert.assertEquals(timeNow - 24 * 60 * 60 * 1000, OpdLibrary.getInstance().getLatestValidCheckInDate().getTime(), 100);
    }
/*
    @Test
    public void isPatientInTreatedStateShouldReturnTrueWhenCurrentDateIsBeforeMidnightOfTreatmentDate() throws ParseException {
        OpdLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), BuildConfig.VERSION_CODE, 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT);
        Date date = simpleDateFormat.parse("2018-10-04 20:23:20");

        ShadowSystemClock.setNanoTime(date.getTime() * 1000000);
        Assert.assertTrue(OpdLibrary.getInstance().isPatientInTreatedState("2018-10-04 17:23:20"));
    }

    @Test
    public void isPatientInTreatedStateShouldReturnFalseWhenCurrentDateIsAfterMidnightOfTreatmentDate() throws ParseException {
        OpdLibrary.init(Mockito.mock(Context.class), Mockito.mock(Repository.class), Mockito.mock(OpdConfiguration.class), BuildConfig.VERSION_CODE, 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(OpdDbConstants.DATE_FORMAT);
        Date date = simpleDateFormat.parse("2018-10-05 20:23:20");

        ShadowSystemClock.setNanoTime(date.getTime() * 1000000);
        Assert.assertFalse(OpdLibrary.getInstance().isPatientInTreatedState("2018-10-04 17:23:20"));
    }*/
}