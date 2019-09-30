package org.smartregister.opd.rule;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */


public class ContactRule {

    public static final String RULE_KEY = "contactRule";

    public String baseEntityId;

    public boolean isFirst;

    public int initialVisit;

    public int currentVisit;

    public Set<Integer> set;

    public ContactRule(int wks, boolean isFirst, String baseEntityId) {

        this.baseEntityId = baseEntityId;

        this.initialVisit = wks;
        this.currentVisit = wks;

        this.isFirst = isFirst;

        this.set = new HashSet<>();
    }

}