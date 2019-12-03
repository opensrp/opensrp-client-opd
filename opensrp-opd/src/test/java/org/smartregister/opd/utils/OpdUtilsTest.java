package org.smartregister.opd.utils;

import android.content.Intent;

import org.jeasy.rules.api.Facts;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.pojo.OpdMetadata;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-17
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(OpdLibrary.class)
public class OpdUtilsTest {

    @Mock
    private OpdLibrary opdLibrary;

    @Mock
    private OpdConfiguration opdConfiguration;

    @Mock
    private OpdMetadata opdMetadata;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fillTemplateShouldReplaceTheBracketedVariableWithCorrectValue() {
        String template = "Gender: {gender}";
        Facts facts = new Facts();
        facts.put("gender", "Male");

        assertEquals("Gender:  Male", OpdUtils.fillTemplate(template, facts));
    }

    @Test
    public void convertStringToDate() {
        Date date = OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, "2019-10-28 18:09:49");
        assertEquals("2019-10-28 18:09:49", OpdUtils.convertDate(date, OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
    }

    @Test
    public void generateNIds() {
        String result = OpdUtils.generateNIds(0);
        assertEquals(result, "");

        String result1 = OpdUtils.generateNIds(1);
        assertEquals(result1.split(",").length, 1);
    }

    @Test
    public void getIntentValue() {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.hasExtra("test")).thenReturn(false);
        assertNull(OpdUtils.getIntentValue(intent, "test"));

        Mockito.when(intent.hasExtra("test")).thenReturn(true);
        Mockito.when(intent.getStringExtra("test")).thenReturn("test");
        assertEquals("test", OpdUtils.getIntentValue(intent, "test"));
    }

    @Test
    public void getIntentValueReturnNull() {
        assertNull(OpdUtils.getIntentValue(null, "test"));
    }

    @Test
    public void metadata() {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getOpdConfiguration()).thenReturn(opdConfiguration);
        PowerMockito.when(opdConfiguration.getOpdMetadata()).thenReturn(opdMetadata);
        assertEquals(opdMetadata, OpdUtils.metadata());
    }

    @Test
    public void testGetClientAge(){
        assertEquals("13", OpdUtils.getClientAge("13y 4m", "y"));
        assertEquals("4m", OpdUtils.getClientAge("4m", "y"));
        assertEquals("5", OpdUtils.getClientAge("5y 4w", "y"));
        assertEquals("3y", OpdUtils.getClientAge("3y", "y"));
        assertEquals("5w 6d", OpdUtils.getClientAge("5w 6d", "y"));
        assertEquals("6d", OpdUtils.getClientAge("6d", "y"));
    }

}