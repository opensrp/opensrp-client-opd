package org.smartregister.opd.utils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public interface OpdDbConstants {
    
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

        String REGISTER_ID = "register_id";
        String BASE_ENTITY_ID = "base_entity_id";
    }

    interface Table {

        String CHECK_IN = "check_in";
    }

    interface Column {

        interface CheckIn {
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

        interface Visit {

        }
    }
}
