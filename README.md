[![Build Status](https://travis-ci.org/OpenSRP/opensrp-client-opd.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-client-opd) 
[![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-opd/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-opd?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/62f3061728134b53a4f5fbc19f593ee8)](https://www.codacy.com/manual/bennsimon/opensrp-client-opd?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-opd&amp;utm_campaign=Badge_Grade)
# OpenSRP Client OPD Library

This library provides the ability to show an OPD(Outpatient Department) Register to a client application


## Table Of Contents

 1. [Getting started](#1-getting-started)
 2. [Required Implementations](#2-required-implementations)
 3. [Enable OPD Registration](#3-enable-opd-registration)

## 1. Getting started

Add the module to your project as follows

 1. Add the repository to your project-root `build.gradle`
```groovy
allprojects {
    repositories {
        ...

        maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    }
}
```


```groovy

dependencies {

    ...

    implementation 'org.smartregister:opensrp-client-opd:0.0.1-SNAPSHOT'
}
```


2. Initialise the library in the `onCreate` method of your `Application` class

```java

public class HealthApplication extends DrishtiApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ...

        OpdLibrary.init(context, getRepository(), 
            new OpdConfiguration.Builder(OpdRegisterQueryProvider.class)
                .build()
        );
    }
}

```

where you should have implemented your own:
 - `OpdRegisterActivity` from the abstract Activity `org.smartregister.opd.activity.BaseOpdRegisterActivity`
 - `OpdRegisterActivityPresenter` from the abstract Presenter `org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter`
 - `OpdRegisterFragment` from the abstract Fragment `org.smartregister.opd.fragment.BaseOpdRegisterFragment`
 - `OpdRegisterQueryProvider` from the interface `org.smartregister.opd.configuration.OpdRegisterQueryProviderContract`
 
 
 3. Add your implemented `OpdRegisterActivity` to the `Android.manifest` file
 4. Call `OpdRegisterActivity` from your navigation menu
 
 5. Create the OPD repositories inside your application repository class
 
    This can be done by adding the following lines of code to your `ApplicationRepository#onCreate(SQLiteDatabase)`:
 
 ```java
 
    ...
 
    public void onCreate(SQLiteDatabase database) {
    
        
        ...
 
        VisitRepository.createTable(database);
        CheckInRepository.createTable(database);
    }
    
 ```
 
 
 ## 2. Required Implementations
 
 ### OpdRegisterFragment
 
 This class should extend `org.smartregister.opd.fragment.BaseOpdRegisterFragment`
 This implements
 
 The following methods are implemented
 
 ```java
 
 protected void startRegistration();

 protected void performPatientAction(@NonNull CommonPersonObjectClient commonPersonObjectClient);
 
 protected void goToClientDetailActivity(@NonNull CommonPersonObjectClient commonPersonObjectClient);
 ```

- You should add your logic for reading the registration form and injecting metadata and other data in the `startRegistration()` implementation
- You should add logic for performing an action when the button on the right of the patient row is clicked in the `performPatientAction(@NonNull CommonPersonObjectClient)` implementation
- You should add your logic for openning the client profile activity in the implementation of `goToClientDetailActivity(@NonNull CommonPersonObjectClient)`

 ### OpdRegisterActivityPresenter
 
 This class should extend `org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter`.
 This implements the interface method in `org.smartregister.opd.contract.OpdRegisterActivityContract.Presenter` not implemented in abstract class `org.smartregister.opd.presenter.BaseOpdRegisterActivityPresenter` 
 
 ```java
 void saveForm(String jsonString, boolean isEditMode);

 ```
 
 You should perform the logic for:
    - Showing a progress dialog
    - Generating the required events and/or clients for the form
    - Processing the event and/or clients for the form
    - Removing the progress dialog
    - Refresh the register by sending a broadcast of sync complete
    
 in your implementation of `saveForm(String, boolean);`
   
 
 ### OpdRegisterActivity
 
 This class should extend `org.smartregister.opd.activity.BaseOpdRegisterActivity`.
 This implements the abstract method in `org.smartregister.opd.activity.BaseOpdRegisterActivity`
 
 ```java
 protected BaseOpdRegisterActivityPresenter createPresenter(@NonNull OpdRegisterActivityContract.View view, @NonNull OpdRegisterActivityContract.Model model);

 public void startFormActivity(JSONObject jsonObject);

 protected void onActivityResultExtended(int i, int i1, Intent intent);
 
 public void startRegistration();

 ```
 
 -  You should add basically create a new instance of the Presenter implemented above  
 
      ```java
    
      return new OpdRegisterActivityPresenter(view, model);
    
      ```
    in your implementation of `BaseOpdRegisterActivityPresenter createPresenter(@NonNull OpdRegisterActivityContract.View, @NonNull OpdRegisterActivityContract.Model)` 


## 3. Enable OPD Registration


Add the following `bindobject` to your `ec_client_fields` `bindobjects` array:

```json
{
      "name": "ec_client",
      "columns": [
        {
          "column_name": "base_entity_id",
          "type": "Client",
          "json_mapping": {
            "field": "baseEntityId"
          }
        },
        {
          "column_name": "opensrp_id",
          "type": "Client",
          "json_mapping": {
            "field": "identifiers.OPENSRP_ID"
          }
        },
        {
          "column_name": "first_name",
          "type": "Client",
          "json_mapping": {
            "field": "firstName"
          }
        },
        {
          "column_name": "last_name",
          "type": "Client",
          "json_mapping": {
            "field": "lastName"
          }
        },
        {
          "column_name": "dob",
          "type": "Client",
          "json_mapping": {
            "field": "birthdate"
          }
        },
        {
          "column_name": "national_id",
          "type": "Client",
          "json_mapping": {
            "field": "attributes.national_id"
          }
        },
        {
          "column_name": "opd_reg_number",
          "type": "Client",
          "json_mapping": {
            "field": "attributes.annual_serial_number"
          }
        },
        {
          "column_name": "bht_mid",
          "type": "Client",
          "json_mapping": {
            "field": "identifiers.bht_mid"
          }
        },
        {
          "column_name": "phone_number",
          "type": "Client",
          "json_mapping": {
            "field": "attributes.phone_number"
          }
        },
        {
          "column_name": "reminders",
          "type": "Client",
          "json_mapping": {
            "field": "attributes.reminders"
          }
        },
        {
          "column_name": "gender",
          "type": "Client",
          "json_mapping": {
            "field": "gender"
          }
        },
        {
          "column_name": "date",
          "type": "Event",
          "json_mapping": {
            "field": "eventDate"
          }
        },
        {
          "column_name": "date_removed",
          "type": "Client",
          "json_mapping": {
            "field": "attributes.dateRemoved"
          }
        }
      ]
    }

```