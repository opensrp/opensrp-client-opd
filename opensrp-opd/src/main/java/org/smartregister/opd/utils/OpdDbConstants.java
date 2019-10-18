package org.smartregister.opd.utils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public interface OpdDbConstants {

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    interface KEY {

        String MOTHER_FIRST_NAME = "mother_first_name";
        String MOTHER_MIDDLE_NAME = "mother_middle_name";
        String MOTHER_LAST_NAME = "mother_last_name";
        String HOME_ADDRESS = "home_address";

        String REGISTER_TYPE = "register_type";

        String FIRST_NAME = "first_name";
        String MIDDLE_NAME = "middle_name";
        String LAST_NAME = "last_name";
        String DOB = "dob";

        String GENDER = "gender";
        String TABLE = "ec_client";
        String OPENSRP_ID = "opensrp_id";
        String BASE_ENTITY_ID = "base_entity_id";
        String LAST_INTERACTED_WITH = "last_interacted_with";
        String DATE_REMOVED = "date_removed";

        String REGISTER_ID = "register_id";
    }

    interface Column {

        interface OpdCheckIn {
            String ID = "_id";
            String EVENT_ID = "event_id";
            String VISIT_ID = "visit_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String PREGNANCY_STATUS = "pregnancy_status";
            String HAS_HIV_TEST_PREVIOUSLY = "has_hiv_test_previously";
            String HIV_RESULTS_PREVIOUSLY = "hiv_results_previously";
            String IS_TAKING_ART = "is_taking_art";
            String CURRENT_HIV_RESULT = "current_hiv_result";
            String VISIT_TYPE = "visit_type";
            String APPOINTMENT_SCHEDULED_PREVIOUSLY = "appointment_scheduled_previously";
            String APPOINTMENT_DUE_DATE = "appointment_due_date";
            String CREATED_AT = "created_at";
            String UPDATED_AT = "updated_at";
        }

        interface OpdVisit {
            String ID = "_id";
            String VISIT_DATE = "visit_date";
            String PROVIDER_ID = "provider_id";
            String LOCATION_ID = "location_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String CREATED_AT = "created_at";
        }

        interface Client {
            String PHOTO = "photo";
            String FIRST_NAME = "first_name";
            String LAST_NAME = "last_name";
            String BASE_ENTITY_ID = "base_entity_id";
            String DOB = "dob";
            String OPENSRP_ID = "opensrp_id";
            String RELATIONALID = "relationalid";
            String NATIONAL_ID = "national_id";
            String GENDER = "gender";
        }
    }

    interface Table {

        String OPD_CHECK_IN = "opd_check_in";
        String OPD_VISIT = "opd_visit";

    }
}
