package org.smartregister.opd.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.opd.BaseUnitTest;
import org.smartregister.opd.R;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ClientLookUpListAdapterTest extends BaseUnitTest {

    private ClientLookUpListAdapter clientLookUpListAdapter;

    @Before
    public void setUp() {
        clientLookUpListAdapter = spy(new ClientLookUpListAdapter(new ArrayList<>(), ApplicationProvider.getApplicationContext()));
    }

    @Test
    public void testOnCreateViewHolderShouldInitializeViewHolder() {
        assertNotNull(clientLookUpListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), 0));
    }

    @Test
    public void testOnBindViewHolderShouldUpdateViewsCorrectly() {
        HashMap<String, String> detailsHashMap = new HashMap<>();
        detailsHashMap.put(OpdDbConstants.Column.Client.FIRST_NAME, "John");
        detailsHashMap.put(OpdDbConstants.Column.Client.LAST_NAME, "Doe");
        detailsHashMap.put(OpdDbConstants.Column.Client.OPENSRP_ID, "1234");

        CommonPersonObject commonPersonObject = new CommonPersonObject("1", "2", detailsHashMap, "");
        commonPersonObject.setColumnmaps(detailsHashMap);

        WhiteboxImpl.setInternalState(clientLookUpListAdapter, "data", Collections.singletonList(commonPersonObject));

        ClientLookUpListAdapter.MyViewHolder viewHolder = clientLookUpListAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), 0);

        clientLookUpListAdapter.onBindViewHolder(viewHolder, 0);

        View root = viewHolder.itemView;
        assertEquals(String.format("%s %s", Utils.getValue(commonPersonObject.getColumnmaps(), OpdDbConstants.Column.Client.FIRST_NAME, true), Utils.getValue(commonPersonObject.getColumnmaps(), OpdDbConstants.Column.Client.LAST_NAME, true)), ((TextView) root.findViewById(R.id.txtName)).getText());
        assertEquals(String.format("%s - %s", ApplicationProvider.getApplicationContext().getString(R.string.opd_opensrp_id_type), Utils.getValue(commonPersonObject.getColumnmaps(), OpdDbConstants.KEY.OPENSRP_ID, true)), ((TextView) root.findViewById(R.id.txtDetails)).getText());
    }
}