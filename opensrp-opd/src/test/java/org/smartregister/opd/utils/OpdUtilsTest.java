package org.smartregister.opd.utils;

import org.jeasy.rules.api.Facts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-17
 */

@RunWith(MockitoJUnitRunner.class)
public class OpdUtilsTest {

    @Test
    public void fillTemplateShouldReplaceTheBracketedVariableWithCorrectValue() {
        String template = "Gender: {gender}";
        Facts facts = new Facts();
        facts.put("gender", "Male");

        assertEquals("Gender:  Male", OpdUtils.fillTemplate(template, facts));
    }

}