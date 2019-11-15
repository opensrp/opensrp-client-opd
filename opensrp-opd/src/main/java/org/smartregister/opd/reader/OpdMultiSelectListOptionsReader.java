package org.smartregister.opd.reader;

import com.vijay.jsonwizard.processor.MultiSelectListFileProcessor;
import com.vijay.jsonwizard.reader.MultiSelectListFileReader;
import com.vijay.jsonwizard.reader.MultiSelectListFileReaderAndProcessor;

import org.json.JSONArray;
import org.smartregister.opd.BuildConfig;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.pojos.OpdMultiSelectOption;
import org.smartregister.opd.repository.OpdMultiSelectOptionsRepository;

import timber.log.Timber;

public class OpdMultiSelectListOptionsReader extends MultiSelectListFileReaderAndProcessor {

    private OpdMultiSelectOptionsRepository opdMultiSelectOptionsRepository;
    private String version;
    private String documentName;

    public OpdMultiSelectListOptionsReader(MultiSelectListFileReader fileReader, MultiSelectListFileProcessor processor) {
        super(fileReader, processor);
        this.opdMultiSelectOptionsRepository = OpdLibrary.getInstance().getOpdMultiSelectOptionsRepository();
    }

    @Override
    protected boolean isAlreadySaved(String s) {
        String[] fileSegments = s.split("__");
        version = fileSegments[1].substring(0, 5);
        documentName = fileSegments[0];
        OpdMultiSelectOption multiSelectOption = opdMultiSelectOptionsRepository.
                findOne(new OpdMultiSelectOption(version, fileSegments[0]));
        return multiSelectOption != null;
    }

    @Override
    public void save(Object o) {
        JSONArray jsonArray = (JSONArray) o;
        OpdMultiSelectOption multiSelectOption = new OpdMultiSelectOption();
        multiSelectOption.setJson(jsonArray.toString());
        multiSelectOption.setVersion(version);
        multiSelectOption.setAppVersion(BuildConfig.VERSION_NAME);
        multiSelectOption.setType(documentName);
        boolean saved = opdMultiSelectOptionsRepository.saveOrUpdate(multiSelectOption);
        if (saved) {
            Timber.d("File %s saved", fileName);
        } else {
            Timber.d("File %s not saved", fileName);

        }
    }
}