package org.smartregister.opd;

import android.os.Build;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.opd.dao.VisitDao;
import org.smartregister.opd.utils.OpdUtils;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({VisitDao.class, OpdUtils.class})
public abstract class BaseFragmentTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    
    @Before
    public void setUp(){
        PowerMockito.mockStatic(VisitDao.class);
        PowerMockito.mockStatic(OpdUtils.class);
    }

}
