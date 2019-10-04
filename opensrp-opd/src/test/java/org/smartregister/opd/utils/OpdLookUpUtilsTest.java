package org.smartregister.opd.utils;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.opd.BuildConfig;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.pojos.OpdMetadata;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OpdLookUpUtils.class})
public class OpdLookUpUtilsTest {

    @Rule
    ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testLookUpQueryWhenEntityMapIsNull() throws Exception {
        Map<String, String> entityMap = null;
        expectedException.expect(NullPointerException.class);
        Whitebox.invokeMethod(OpdLookUpUtils.class, "lookUpQuery", entityMap, "");
    }

    @Test
    public void testLookUpQueryWhenEntityMapIsNotNull() throws Exception {
        Map<String, String> entityMap = new HashMap<>();
        String result = Whitebox.invokeMethod(OpdLookUpUtils.class, "lookUpQuery", entityMap, "");
        String expected_result = "Select .id as _id , " + OpdDbConstants.Table.Client.RELATIONALID + " , " + OpdDbConstants.Table.Client.OPENSRP_ID +
                " , " + OpdDbConstants.Table.Client.FIRST_NAME + " , " + OpdDbConstants.Table.Client.LAST_NAME + " , " + OpdDbConstants.Table.Client.GENDER + " ," +
                " " + OpdDbConstants.Table.Client.DOB + " , " + OpdDbConstants.Table.Client.BASE_ENTITY_ID + " , " + OpdDbConstants.Table.Client.NATIONAL_ID + " FROM ;";
        Assert.assertEquals(result, expected_result);
    }

    @Test
    public void testGetMainConditionStringWhenEntityMapIsNull() throws Exception {
        Map<String, String> entityMap = null;
        expectedException.expect(NullPointerException.class);
        String result = Whitebox.invokeMethod(OpdLookUpUtils.class, "getMainConditionString", entityMap);
    }

    @Test
    public void testGetMainConditionStringWhenEntityMapIsEmpty() throws Exception {
        Map<String, String> entityMap = new HashMap<>();
        String result = Whitebox.invokeMethod(OpdLookUpUtils.class, "getMainConditionString", entityMap);
        Assert.assertEquals(result, "");
    }

    @Test
    public void testGetMainConditionStringWhenEntityMapIsWithValue() throws Exception {
        String firstName = "first_name";
        String lastName = "last_name";
        String bht_id = "bht_mid";
        String national_id = "national_id";
        Map<String, String> entityMap = new HashMap<>();
        entityMap.put(firstName, "");
        entityMap.put(lastName, "");
        entityMap.put(bht_id, "");
        entityMap.put(national_id, "");
        String result = Whitebox.invokeMethod(OpdLookUpUtils.class, "getMainConditionString", entityMap);
        String actualResult = "national_id Like '%%' AND last_name Like '%%' AND first_name Like '%%' AND bht_mid Like '%%'";
        Assert.assertNotNull(result, actualResult);
    }

    @Test
    public void testClientLookUpWhenContextIsNull() throws Exception {
        Map<String, String> entityLookUp = new HashMap<>();
        List<CommonPersonObject> result = Whitebox.invokeMethod(OpdLookUpUtils.class, "clientLookUp", (Object) null, entityLookUp);
        List<CommonPersonObject> expectedResult = new ArrayList<>();
        Assert.assertEquals(result, expectedResult);
    }

    @Test
    public void testClientLookUpWhenMapIsEmpty() throws Exception {
        Map<String, String> entityLookUp = new HashMap<>();
        List<CommonPersonObject> result = Whitebox.invokeMethod(OpdLookUpUtils.class, "clientLookUp", PowerMockito.mock(Context.class), entityLookUp);
        List<CommonPersonObject> expectedResult = new ArrayList<>();
        Assert.assertEquals(result, expectedResult);
    }

    @Test
    public void testClientLookUpWhenMapIsNotEmptyAndContextIsNotNullWithEmptyTable() throws Exception {
        OpdLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), PowerMockito.mock(OpdConfiguration.class),
                BuildConfig.VERSION_CODE, 1);
        PowerMockito.when(OpdUtils.class, "metadata").thenReturn(new OpdMetadata("",
                "", "", "", "", Class.class, Class.class, false));
        Map<String, String> entityLookUp = new HashMap<>();
        entityLookUp.put("first_name", "");
        List<CommonPersonObject> result = Whitebox.invokeMethod(OpdLookUpUtils.class, "clientLookUp", PowerMockito.mock(Context.class), entityLookUp);
        List<CommonPersonObject> expectedResult = new ArrayList<>();
        Assert.assertEquals(result, expectedResult);
    }

    @Test
    public void testClientLookUpWhenMapIsNotEmptyAndContextIsNotNullWithTable() throws Exception {
        OpdLibrary.init(PowerMockito.mock(Context.class), PowerMockito.mock(Repository.class), PowerMockito.mock(OpdConfiguration.class),
                BuildConfig.VERSION_CODE, 1);
        Map<String, String> entityLookUp = new HashMap<>();
        entityLookUp.put("first_name", "");
        List<CommonPersonObject> result = Whitebox.invokeMethod(OpdLookUpUtils.class, "clientLookUp", PowerMockito.mock(Context.class), entityLookUp);
        List<CommonPersonObject> expectedResult = new ArrayList<>();
        Assert.assertEquals(result, expectedResult);
    }

}
