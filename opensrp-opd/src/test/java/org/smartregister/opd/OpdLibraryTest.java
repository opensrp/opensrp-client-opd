package org.smartregister.opd;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.repository.Repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-24
 */
@PrepareForTest(OpdLibrary.class)
@RunWith(PowerMockRunner.class)
public class OpdLibraryTest extends BaseTest {

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
}