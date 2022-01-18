package org.smartregister.opd.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.test.core.app.ApplicationProvider;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.opd.BaseUnitTest;
import org.smartregister.opd.R;

import java.util.Collections;
import java.util.List;


public class OpdRepeatingGroupTest extends BaseUnitTest {

    private OpdRepeatingGroup opdRepeatingGroup;

    @Mock
    private CommonListener commonListener;

    @Mock
    private JsonFormFragment jsonFormFragment;

    @Mock
    private Context context;

    @Before
    public void setUp() {
        opdRepeatingGroup = spy(new OpdRepeatingGroup());
    }

    @Test
    public void testGetViewsFromJsonShouldInitializeWidget() throws Exception {
        JSONObject fieldJsonObject = new JSONObject();
        String fieldKey = "fieldA";
        LinearLayout view = new LinearLayout(ApplicationProvider.getApplicationContext());
        Button repeatingGroupIntermediateBtn = new Button(view.getContext());
        repeatingGroupIntermediateBtn.setId(R.id.repeating_group_intermediate_btn);
        MaterialEditText referenceEditText = new MaterialEditText(view.getContext());
        referenceEditText.setText(R.id.reference_edit_text);
        ImageButton doneButton = new ImageButton(view.getContext());
        view.addView(referenceEditText);
        view.addView(repeatingGroupIntermediateBtn);
        view.addView(doneButton);

        doReturn(Collections.singletonList(view)).when(opdRepeatingGroup)
                .getParentViewsFromJson(fieldKey, context,
                        jsonFormFragment, fieldJsonObject, commonListener, false);

        assertFalse(repeatingGroupIntermediateBtn.hasOnClickListeners());
        List<View> viewList = opdRepeatingGroup.getViewsFromJson(fieldKey, context,
                jsonFormFragment, fieldJsonObject, commonListener, false);

        assertTrue(repeatingGroupIntermediateBtn.hasOnClickListeners());
        assertNotNull(viewList);
        assertEquals(1, viewList.size());
    }
}