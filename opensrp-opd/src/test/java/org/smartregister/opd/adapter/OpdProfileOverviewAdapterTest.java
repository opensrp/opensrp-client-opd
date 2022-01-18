package org.smartregister.opd.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.jeasy.rules.api.Facts;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.smartregister.opd.BaseUnitTest;
import org.smartregister.opd.R;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class OpdProfileOverviewAdapterTest extends BaseUnitTest {

    private OpdProfileOverviewAdapter opdProfileOverviewAdapter;

    @Before
    public void setUp() throws Exception {
        List<YamlConfigWrapper> data = new ArrayList<>();
        Facts facts = new Facts();
        opdProfileOverviewAdapter = spy(new OpdProfileOverviewAdapter(ApplicationProvider.getApplicationContext(), data, facts));
    }

    @Test
    public void testOnCreateViewHolderShouldInitializeViewHolder() {
        ViewGroup viewGroup = new LinearLayout(ApplicationProvider.getApplicationContext());
        OpdProfileOverviewAdapter.ViewHolder viewHolder = opdProfileOverviewAdapter.onCreateViewHolder(viewGroup, 0);
        assertNotNull(viewHolder);
    }

    @Test
    public void testOnBindViewHolderShouldPopulateViews() {
        ViewGroup viewGroup = new LinearLayout(ApplicationProvider.getApplicationContext());
        OpdProfileOverviewAdapter.ViewHolder viewHolder = opdProfileOverviewAdapter.onCreateViewHolder(viewGroup, 0);
        YamlConfigItem yamlConfigItem = new YamlConfigItem();
        yamlConfigItem.setHtml(true);
        yamlConfigItem.setRelevance("");
        yamlConfigItem.setTemplate("{tests_label}: {tests}");
        yamlConfigItem.setIsMultiWidget(false);

        String group = "groupA";
        String subGroup = "subGroupA";
        YamlConfigWrapper yamlConfigWrapper = new YamlConfigWrapper(group, subGroup, yamlConfigItem);
        Facts facts = new Facts();
        facts.put("tests", "Malaria Test");
        WhiteboxImpl.setInternalState(opdProfileOverviewAdapter, "facts", facts);
        WhiteboxImpl.setInternalState(opdProfileOverviewAdapter, "mData", Collections.singletonList(yamlConfigWrapper));
        opdProfileOverviewAdapter.onBindViewHolder(viewHolder, 0);
        View root = viewHolder.itemView;
        assertEquals("{tests_label}", ((TextView) root.findViewById(R.id.overview_section_details_left)).getText());
        assertEquals("Malaria test", ((TextView) root.findViewById(R.id.overview_section_details_right)).getText());
        assertEquals(group.toUpperCase(Locale.ROOT), ((TextView) root.findViewById(R.id.overview_section_header)).getText());
        assertEquals(subGroup.toUpperCase(Locale.ROOT), ((TextView) root.findViewById(R.id.overview_subsection_header)).getText());
    }
}