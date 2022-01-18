package org.smartregister.opd.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.util.Pair;
import androidx.test.core.app.ApplicationProvider;

import org.jeasy.rules.api.Facts;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.smartregister.opd.BaseUnitTest;
import org.smartregister.opd.R;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.util.StringUtil;

import java.util.ArrayList;

public class OpdProfileVisitsAdapterTest extends BaseUnitTest {

    private OpdProfileVisitsAdapter opdProfileVisitsAdapter;

    @Before
    public void setUp() throws Exception {
        opdProfileVisitsAdapter = spy(new OpdProfileVisitsAdapter(ApplicationProvider.getApplicationContext(), new ArrayList<>()));
    }

    @Test
    public void testOnCreateViewHolderShouldInitializeViewHolder() {
        assertNotNull(opdProfileVisitsAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), 0));
    }

    @Test
    public void testOnBindViewHolderShouldUpdateViewsCorrectly() {
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
        facts.put("tests_label", "Tests done");


        ArrayList<Pair<YamlConfigWrapper, Facts>> items = new ArrayList<>();
        items.add(Pair.create(yamlConfigWrapper, facts));

        WhiteboxImpl.setInternalState(opdProfileVisitsAdapter, "items", items);

        OpdProfileVisitsAdapter.YamlViewHolder viewHolder = opdProfileVisitsAdapter.onCreateViewHolder(new LinearLayout(ApplicationProvider.getApplicationContext()), 0);

        opdProfileVisitsAdapter.onBindViewHolder(viewHolder, 0);

        View root = viewHolder.itemView;
        assertEquals(facts.get("tests_label"), ((TextView) root.findViewById(R.id.overview_section_details_left)).getText());
        assertEquals("Malaria test", ((TextView) root.findViewById(R.id.overview_section_details_right)).getText().toString());
        assertEquals(StringUtil.humanize(group), ((TextView) root.findViewById(R.id.overview_section_header)).getText());
        assertEquals(StringUtil.humanize(subGroup), ((TextView) root.findViewById(R.id.overview_subsection_header)).getText());

    }
}