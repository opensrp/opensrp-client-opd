package org.smartregister.opd.utils;

import org.smartregister.AllConstants;

public class OpdConstants extends AllConstants {

    public static final String SEX = "Sex";
    public static final String CLIENT_TYPE = "client";
    public static final String CONFIG = "opd_register";

    public static class JSON_FORM_KEY {
        public static final String OPTIONS = "options";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DOB = "dob";
        public static final String DOB_UNKNOWN = "dob_unknown";
        public static final String AGE_ENTERED = "age_entered" ;

        public static final String DOB_ENTERED = "dob_entered";
        public static final String ADDRESS_WIDGET_KEY = "home_address";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String BHT_ID = "bht_id";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String REMINDERS = "reminders";
        public static final String NATIONAL_ID = "national_id";
        public static final String HOME_ADDRESS = "home_address";
        public static final String AGE_CALCULATED = "age_calculated";
        public static final String GENDER = "gender";
        public static final String NAME = "opd_registration";
        public static final String ENCOUNTER_TYPE = "encounter_type";
        public static final String ENTITY_ID = "entity_id";
        public static final String ENCOUNTER = "encounter";
        public static final String ENCOUNTER_LOCATION = "encounter_location";
        public static final String UNIQUE_ID = "unique_id";
        public static final String AGE = "age";
        public static final String OPD_EDIT_FORM_TITLE = "Update Opd Registration";
        public static final String FORM_TITLE = "title";
        public static final String OPENSRP_ID = "opensrp_id";
    }

    public static class JsonFormNameUtils {
        public static final String CHECK_IN = "opd_checkin";
    }

    public static class JSON_FORM_EXTRA {
        public static final String NEXT = "next";
        public static final String JSON = "json";
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
        public static final String ZEIR_ID = "zeir_id";
        public static final String ID = "id";
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
        public static final String VISIT = "OPD Visit";
    }

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

    public interface FormValue {
        String IS_DOB_UNKNOWN = "isDobUnknown";
        String IS_ENROLLED_IN_MESSAGES = "isEnrolledInSmsMessages";
        String OTHER = "other";
    }
}
