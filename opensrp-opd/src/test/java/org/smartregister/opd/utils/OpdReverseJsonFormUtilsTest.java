package org.smartregister.opd.utils;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Photo;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.activity.BaseOpdFormActivity;
import org.smartregister.opd.activity.BaseOpdProfileActivity;
import org.smartregister.opd.configuration.OpdConfiguration;
import org.smartregister.opd.pojo.OpdMetadata;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.util.JsonFormUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({OpdLibrary.class, LocationHelper.class, CoreLibrary.class, ImageUtils.class})
public class OpdReverseJsonFormUtilsTest {

    @Mock
    private OpdLibrary opdLibrary;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private Context opensrpContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private OpdConfiguration opdConfiguration;

    @Test
    public void prepareJsonEditOpdRegistrationForm() throws JSONException {
        PowerMockito.mockStatic(OpdLibrary.class);
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(ImageUtils.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        PowerMockito.when(opensrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn("lombwe");
        PowerMockito.when(ImageUtils.profilePhotoByClientID("base-entity-id", OpdImageUtils.getProfileImageResourceIdentifier())).thenReturn(Mockito.mock(Photo.class));
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        PowerMockito.when(OpdLibrary.getInstance()).thenReturn(opdLibrary);
        PowerMockito.when(opdLibrary.getOpdConfiguration()).thenReturn(opdConfiguration);

        OpdMetadata opdMetadata = new OpdMetadata(OpdConstants.JSON_FORM_KEY.NAME, OpdDbConstants.KEY.TABLE,
                OpdConstants.EventType.OPD_REGISTRATION, OpdConstants.EventType.UPDATE_OPD_REGISTRATION,
                OpdConstants.CONFIG, BaseOpdFormActivity.class, BaseOpdProfileActivity.class, true);

        PowerMockito.when(opdConfiguration.getOpdMetadata()).thenReturn(opdMetadata);

        Map<String, String> detailsMap = new HashMap<>();
        detailsMap.put(OpdJsonFormUtils.OPENSRP_ID, "123");
        detailsMap.put(OpdConstants.KEY.ID, "base-entity-id");
        detailsMap.put(OpdConstants.JSON_FORM_KEY.REMINDERS, "true");
        detailsMap.put(OpdConstants.JSON_FORM_KEY.DOB_UNKNOWN, "true");
        detailsMap.put(OpdConstants.JSON_FORM_KEY.AGE, "20");
        detailsMap.put(OpdConstants.JSON_FORM_KEY.FIRST_NAME, "tella");
        detailsMap.put(OpdConstants.JSON_FORM_KEY.LAST_NAME, "man");
        detailsMap.put(OpdConstants.JSON_FORM_KEY.GENDER, "Male");
        detailsMap.put(OpdConstants.JSON_FORM_KEY.DOB, "");
        detailsMap.put(OpdConstants.JSON_FORM_KEY.HOME_ADDRESS, "Mbogeni");


        FormUtils formUtils = Mockito.mock(FormUtils.class);
        String opdRegistrationForm = "{\n" +
                "  \"count\": \"1\",\n" +
                "  \"encounter_type\": \"Opd Registration\",\n" +
                "  \"entity_id\": \"\",\n" +
                "  \"relational_id\": \"\",\n" +
                "  \"metadata\": {\n" +
                "    \"start\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"start\",\n" +
                "      \"openmrs_entity_id\": \"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "    },\n" +
                "    \"end\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"end\",\n" +
                "      \"openmrs_entity_id\": \"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "    },\n" +
                "    \"today\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"encounter\",\n" +
                "      \"openmrs_entity_id\": \"encounter_date\"\n" +
                "    },\n" +
                "    \"deviceid\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"deviceid\",\n" +
                "      \"openmrs_entity_id\": \"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "    },\n" +
                "    \"subscriberid\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"subscriberid\",\n" +
                "      \"openmrs_entity_id\": \"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "    },\n" +
                "    \"simserial\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"simserial\",\n" +
                "      \"openmrs_entity_id\": \"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "    },\n" +
                "    \"phonenumber\": {\n" +
                "      \"openmrs_entity_parent\": \"\",\n" +
                "      \"openmrs_entity\": \"concept\",\n" +
                "      \"openmrs_data_type\": \"phonenumber\",\n" +
                "      \"openmrs_entity_id\": \"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "    },\n" +
                "    \"encounter_location\": \"\",\n" +
                "    \"look_up\": {\n" +
                "      \"entity_id\": \"\",\n" +
                "      \"value\": \"\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"step1\": {\n" +
                "    \"title\": \"OPD Registration\",\n" +
                "    \"fields\": [\n" +
                "      {\n" +
                "        \"key\": \"photo\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"type\": \"choose_image\",\n" +
                "\n" +
                "        \"uploadButtonText\": \"Take a photo of the person\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"OPENSRP_ID\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person_identifier\",\n" +
                "        \"openmrs_entity_id\": \"zeir_id\",\n" +
                "        \"type\": \"barcode\",\n" +
                "        \"barcode_type\": \"qrcode\",\n" +
                "        \"hint\": \"MER ID\",\n" +
                "        \"value\": \"0\",\n" +
                "        \"scanButtonText\": \"Scan QR Code\",\n" +
                "        \"v_numeric\": {\n" +
                "          \"value\": \"true\",\n" +
                "          \"err\": \"Please enter a valid MER ID\"\n" +
                "        },\n" +
                "        \"v_required\": {\n" +
                "          \"value\": \"true\",\n" +
                "          \"err\": \"Please enter the MER ID\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"national_id\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person_attribute\",\n" +
                "        \"openmrs_entity_id\": \"national_id\",\n" +
                "        \"type\": \"edit_text\",\n" +
                "        \"hint\": \"National ID\",\n" +
                "        \"edit_type\": \"name\",\n" +
                "        \"look_up\": true,\n" +
                "        \"v_required\": {\n" +
                "          \"value\": false,\n" +
                "          \"err\": \"Please enter the patient's national id\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"bht_mid\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person_attribute\",\n" +
                "        \"openmrs_entity_id\": \"bht_mid\",\n" +
                "        \"type\": \"barcode\",\n" +
                "        \"barcode_type\": \"qrcode\",\n" +
                "        \"hint\": \"BHT ID\",\n" +
                "        \"value\": \"0\",\n" +
                "        \"scanButtonText\": \"Scan Barcode\",\n" +
                "        \"look_up\": true,\n" +
                "        \"v_numeric\": {\n" +
                "          \"value\": \"true\",\n" +
                "          \"err\": \"Please enter a valid BHT ID\"\n" +
                "        },\n" +
                "        \"v_required\": {\n" +
                "          \"value\": false,\n" +
                "          \"err\": \"Please enter a valid BHT ID\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"opd_reg_number\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person_attribute\",\n" +
                "        \"openmrs_entity_id\": \"annual_serial_number\",\n" +
                "        \"type\": \"edit_text\",\n" +
                "        \"hint\": \"Annual Serial Number\",\n" +
                "        \"edit_type\": \"name\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"first_name\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person\",\n" +
                "        \"openmrs_entity_id\": \"first_name\",\n" +
                "        \"type\": \"edit_text\",\n" +
                "        \"hint\": \"First name\",\n" +
                "        \"edit_type\": \"name\",\n" +
                "        \"look_up\" : \"true\",\n" +
                "        \"entity_id\" :\"\",\n" +
                "        \"v_regex\": {\n" +
                "          \"value\": \"[A-Za-z\\\\s\\\\.\\\\-]*\",\n" +
                "          \"err\": \"Please enter a valid name\"\n" +
                "        },\n" +
                "        \"v_required\": {\n" +
                "          \"value\": true,\n" +
                "          \"err\": \"Please enter a first name\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"last_name\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person\",\n" +
                "        \"openmrs_entity_id\": \"last_name\",\n" +
                "        \"type\": \"edit_text\",\n" +
                "        \"hint\": \"Last name\",\n" +
                "        \"edit_type\": \"name\",\n" +
                "        \"look_up\" : \"true\",\n" +
                "        \"v_required\": {\n" +
                "          \"value\": true,\n" +
                "          \"err\": \"Please enter the last name\"\n" +
                "        },\n" +
                "        \"v_regex\": {\n" +
                "          \"value\": \"[A-Za-z\\\\s\\\\.\\\\-]*\",\n" +
                "          \"err\": \"Please enter a valid name\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"dob_entered\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person\",\n" +
                "        \"openmrs_entity_id\": \"birthdate\",\n" +
                "        \"type\": \"date_picker\",\n" +
                "        \"hint\": \"Date of birth (DOB)\",\n" +
                "        \"expanded\": false,\n" +
                "        \"duration\": {\n" +
                "          \"label\": \"Age\"\n" +
                "        },\n" +
                "        \"max_date\": \"today\",\n" +
                "        \"v_required\": {\n" +
                "          \"value\": \"true\",\n" +
                "          \"err\": \"Please enter the date of birth\"\n" +
                "        },\n" +
                "        \"relevance\": {\n" +
                "        \"step1:dob_unknown\": {\n" +
                "          \"type\": \"string\",\n" +
                "          \"ex\": \"equalTo(., \\\"false\\\")\"\n" +
                "        }\n" +
                "      }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"dob_unknown\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person\",\n" +
                "        \"openmrs_entity_id\": \"dob_unknown\",\n" +
                "        \"type\": \"check_box\",\n" +
                "        \"hint\": \"DOB unknown checkbox\",\n" +
                "        \"label\": \"\",\n" +
                "        \"options\": [\n" +
                "          {\n" +
                "            \"key\" : \"isDobUnknown\",\n" +
                "            \"text\": \"DOB unknown?\",\n" +
                "            \"value\": \"false\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"age_entered\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person_attribute\",\n" +
                "        \"openmrs_entity_id\": \"age\",\n" +
                "        \"type\": \"edit_text\",\n" +
                "        \"hint\": \"Age\",\n" +
                "        \"v_numeric\": {\n" +
                "          \"value\": \"true\",\n" +
                "          \"err\": \"Enter a valid Age\"\n" +
                "        },\n" +
                "        \"relevance\": {\n" +
                "          \"step1:dob_unknown\": {\n" +
                "            \"type\": \"string\",\n" +
                "            \"ex\": \"equalTo(., \\\"true\\\")\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"age_calculated\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"type\": \"hidden\",\n" +
                "        \"value\": \"\",\n" +
                "        \"calculation\": {\n" +
                "          \"rules-engine\": {\n" +
                "            \"ex-rules\": {\n" +
                "              \"rules-file\": \"opd/registration_calculation_rules.yml\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"age\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"type\": \"hidden\",\n" +
                "        \"value\": \"\",\n" +
                "        \"calculation\": {\n" +
                "          \"rules-engine\": {\n" +
                "            \"ex-rules\": {\n" +
                "              \"rules-file\": \"opd/registration_calculation_rules.yml\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"dob_calculated\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"type\": \"hidden\",\n" +
                "        \"value\": \"\",\n" +
                "        \"calculation\": {\n" +
                "          \"rules-engine\": {\n" +
                "            \"ex-rules\": {\n" +
                "              \"rules-file\": \"opd/registration_calculation_rules.yml\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"dob\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"type\": \"hidden\",\n" +
                "        \"value\": \"\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"Sex\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person\",\n" +
                "        \"openmrs_entity_id\": \"gender\",\n" +
                "        \"type\": \"spinner\",\n" +
                "        \"hint\": \"Gender\",\n" +
                "        \"values\": [\n" +
                "          \"Male\",\n" +
                "          \"Female\"\n" +
                "        ],\n" +
                "        \"v_required\": {\n" +
                "          \"value\": \"true\",\n" +
                "          \"err\": \"Please enter the Gender\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"home_address\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"\",\n" +
                "        \"openmrs_entity_id\": \"\",\n" +
                "        \"openmrs_data_type\": \"text\",\n" +
                "        \"type\": \"tree\",\n" +
                "        \"tree\": [],\n" +
                "        \"value\": \"Lombwa Outreach\",\n" +
                "        \"hint\": \"Address/Location\",\n" +
                "        \"v_required\": {\n" +
                "          \"value\": true,\n" +
                "          \"err\": \"Please enter the Home Address\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"phone_number\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person_attribute\",\n" +
                "        \"openmrs_entity_id\": \"phone_number\",\n" +
                "        \"type\": \"edit_text\",\n" +
                "        \"hint\": \"Phone number\",\n" +
                "        \"v_regex\": {\n" +
                "          \"value\": \"([0][0-9]{9})|\\\\s*\",\n" +
                "          \"err\": \"Number must begin with 0 and must be a total of 10 digits in length\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"key\": \"reminders\",\n" +
                "        \"openmrs_entity_parent\": \"\",\n" +
                "        \"openmrs_entity\": \"person_attribute\",\n" +
                "        \"openmrs_entity_id\": \"reminders\",\n" +
                "        \"openmrs_data_type\": \"select one\",\n" +
                "        \"type\": \"check_box\",\n" +
                "        \"options\": [\n" +
                "          {\n" +
                "            \"key\" : \"isEnrolledInSmsMessages\",\n" +
                "            \"text\": \"Enroll in Sms Messages\",\n" +
                "            \"value\": \"false\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONObject opdRegistrationFormJsonObject = new JSONObject(opdRegistrationForm);
        Mockito.when(formUtils.getFormJson(OpdConstants.JSON_FORM_KEY.NAME)).thenReturn(opdRegistrationFormJsonObject);
        String jsonForm = OpdReverseJsonFormUtils.prepareJsonEditOpdRegistrationForm(detailsMap, Arrays.asList(OpdJsonFormUtils.OPENSRP_ID, OpdConstants.JSON_FORM_KEY.BHT_ID), formUtils);
        JSONObject jsonObject = new JSONObject(jsonForm);
        Assert.assertEquals(jsonObject.optString(OpdJsonFormUtils.CURRENT_ZEIR_ID), detailsMap.get(OpdJsonFormUtils.OPENSRP_ID));
        Assert.assertEquals(jsonObject.optString(OpdJsonFormUtils.ENTITY_ID), detailsMap.get(OpdConstants.KEY.ID));

        JSONObject step1JsonObject = jsonObject.optJSONObject(JsonFormConstants.STEP1);

        JSONObject metadataJsonObject = jsonObject.optJSONObject(OpdJsonFormUtils.METADATA);
        String encounterLocation = metadataJsonObject.optString(OpdJsonFormUtils.ENCOUNTER_LOCATION);
        Assert.assertEquals("lombwe", encounterLocation);
        Assert.assertEquals(detailsMap.get(OpdJsonFormUtils.OPENSRP_ID), JsonFormUtils.getFieldValue(step1JsonObject.optJSONArray(JsonFormConstants.FIELDS), OpdJsonFormUtils.OPENSRP_ID));
        Assert.assertEquals(detailsMap.get(OpdConstants.JSON_FORM_KEY.AGE), JsonFormUtils.getFieldValue(step1JsonObject.optJSONArray(JsonFormConstants.FIELDS), OpdConstants.JSON_FORM_KEY.AGE_ENTERED));
        Assert.assertEquals(detailsMap.get(OpdConstants.JSON_FORM_KEY.FIRST_NAME), JsonFormUtils.getFieldValue(step1JsonObject.optJSONArray(JsonFormConstants.FIELDS), OpdConstants.JSON_FORM_KEY.FIRST_NAME));
        Assert.assertEquals(detailsMap.get(OpdConstants.JSON_FORM_KEY.LAST_NAME), JsonFormUtils.getFieldValue(step1JsonObject.optJSONArray(JsonFormConstants.FIELDS), OpdConstants.JSON_FORM_KEY.LAST_NAME));
        Assert.assertEquals(detailsMap.get(OpdConstants.JSON_FORM_KEY.GENDER), JsonFormUtils.getFieldValue(step1JsonObject.optJSONArray(JsonFormConstants.FIELDS), "Sex"));
    }
}