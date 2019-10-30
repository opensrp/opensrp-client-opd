package org.smartregister.opd.utils;

import org.smartregister.AllConstants;

public class OpdConstants extends AllConstants {

    public static final String SEX = "Sex";
    public static final String CLIENT_TYPE = "client";
    public static final String CONFIG = "opd_register";

    public interface IntentKey {
        String BASE_ENTITY_ID = "base-entity-id";
        String CLIENT_MAP = "client_map";
        String CLIENT_OBJECT = "common_person_object_client";
        String CONTACT_NO = "contact_number";
        String ENTITY_TABLE = "entity_table";
    }

    public interface Event {

        interface Visit {
            interface Detail {
                String VISIT_ID = "visitId";
                String VISIT_DATE = "visitDate";
            }
        }

        interface CheckIn {
            interface Detail {
                String VISIT_ID = "visitId";
                String VISIT_DATE = "visitDate";
            }
        }
    }

    public interface JsonFormField {
        String PATIENT_GENDER = "patient_gender";
        String PREGNANCY_STATUS = "pregnancy_status";
        String HIV_TESTED = "hiv_tested";
        String HIV_PREVIOUS_STATUS = "hiv_prev_status";
        String IS_PATIENT_TAKING_ART = "hiv_prev_pos_art";
        String CURRENT_HIV_STATUS = "current_hiv_status";
        String VISIT_TYPE = "visit_type";
        String APPOINTMENT_DUE = "appointment_due";
        String APPOINTMENT_DUE_DATE = "appointment_due_date";
        String APPOINTMENT_DUE_INLESS_TIME = "appointment_done_inless_time";
    }

    public interface JsonFormWidget {
        String MULTI_SELECT_DRUG_PICKER = "multi_select_drug_picker";
    }

    public static class JSON_FORM_KEY {
        public static final String OPTIONS = "options";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DOB = "dob";
        public static final String DOB_UNKNOWN = "dob_unknown";

        public static final String AGE_ENTERED = "age_entered";
        public static final String DOB_ENTERED = "dob_entered";
        public static final String ADDRESS_WIDGET_KEY = "home_address";
        public static final String NAME = "opd_registration";
        public static final String REMINDERS = "reminders";
        public static final String SERVICE_FEE = "service_fee";
        public static final String VISIT_ID = "visitId";
        public static final String MEDICINE = "medicine";
        public static final String DOSAGE = "dosage";
        public static final String DURATION = "duration";
        public static final String INFO = "info";
        public static final String META = "meta";
        public static final String DIAGNOSIS = "diagnosis";
        public static final String DIAGNOSIS_TYPE = "diagnosis_type";
        public static final String DISEASE_CODE = "disease_code";
        public static final String CODE = "code";
        public static final String ICD10 = "icd10";
        public static final String DIAGNOSTIC_TEST_RESULT_SPINNER = "diagnostic_test_result_spinner";
        public static final String DIAGNOSTIC_TEST_OTHER = "diagnostic_test_other";
        public static final String DIAGNOSTIC_TEST = "diagnostic_test";
        public static final String DIAGNOSTIC_TEST_RESULT_SPECIFY = "diagnostic_test_result_specify";
        public static final String ID = "ID";
        public static final String VISIT_END_DATE = "visit_end_date";
    }

    public static class JsonFormNameUtils {
        public static final String CHECK_IN = "opd_checkin";
    }

    public static class JSON_FORM_EXTRA {
        public static final String NEXT = "next";
        public static final String STEP = "step";
        public static final String JSON = "json";
        public static final String STEP1 = "step1";
        public static final String STEP2 = "step2";
        public static final String STEP3 = "step3";
        public static final String STEP4 = "step4";

    }

    public static class OPENMRS {
        public static final String ENTITY = "openmrs_entity";
        public static final String ENTITY_ID = "openmrs_entity_id";
    }

    public static final class KEY {
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String PHOTO = "photo";
        public static final String LOOK_UP = "look_up";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String DOB = "dob";//Date Of Birth
        public static final String OPENSRP_ID = "opensrp_id";
        public static final String RELATIONALID = "relationalid";
        public static final String NATIONAL_ID = "national_id";
    }

    public static class ENTITY {
        public static final String PERSON = "person";
    }

    public static class BOOLEAN_INT {
        public static final int TRUE = 1;
    }

    public static class FormActivity {
        public static final String EnableOnCloseDialog = "EnableOnCloseDialog";
    }

    public static final class EventType {
        public static final String OPD_REGISTRATION = "Opd Registration";
        public static final String UPDATE_OPD_REGISTRATION = "Update Opd Registration";
        public static final String CHECK_IN = "OPD Check-In";
        public static final String DIAGNOSIS_AND_TREAT = "OPD Diagnosis and Treatment";
        public static final String DIAGNOSIS = "OPD Diagnosis";
        public static final String TREATMENT = "OPD Treatment";
        public static final String TEST_CONDUCTED = "OPD Test Conducted";
        public static final String SERVICE_DETAIL = "OPD Service Detail";
        public static final String VISIT = "OPD Visit";
        public static final String CLOSE_OPD_VISIT = "OPD Close Visit";
    }

    public interface DateFormat {
        String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    }

    public interface Form {
        String OPD_DIAGNOSIS_AND_TREAT = "opd_diagnose_and_treat";
    }
}
