package org.smartregister.opd.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.opd.BaseUnitTest;
import org.smartregister.opd.pojo.OpdDetails;
import org.smartregister.opd.utils.OpdDbConstants;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class OpdDetailsRepositoryTest extends BaseUnitTest {

    private OpdDetailsRepository opdDetailsRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private Cursor cursor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        opdDetailsRepository = spy(new OpdDetailsRepository());
    }

    @Test
    public void testSaveOrUpdateShouldInvokeExpectedMethods() {
        OpdDetails opdDetails = new OpdDetails();

        doReturn(sqLiteDatabase).when(opdDetailsRepository).getWritableDatabase();

        opdDetailsRepository.saveOrUpdate(opdDetails);

        verify(sqLiteDatabase, only()).insertWithOnConflict(eq(OpdDbConstants.Table.OPD_DETAILS), isNull(), any(ContentValues.class), eq(SQLiteDatabase.CONFLICT_REPLACE));
    }

    @Test(expected = NotImplementedException.class)
    public void testSaveShouldThrowNotImplementedException() {
        opdDetailsRepository.save(new OpdDetails());
    }

    @Test
    public void testFindOneShouldInvokeExpectedMethods() {
        OpdDetails opdDetails = new OpdDetails();
        opdDetails.setCurrentVisitId(UUID.randomUUID().toString());
        opdDetails.setBaseEntityId(UUID.randomUUID().toString());

        doReturn(true).when(cursor).moveToNext();

        doReturn(cursor).when(sqLiteDatabase).query(
                eq(OpdDbConstants.Table.OPD_DETAILS),
                eq(opdDetailsRepository.columns),
                eq(OpdDbConstants.Column.OpdDetails.BASE_ENTITY_ID + "=? and " + OpdDbConstants.Column.OpdDetails.CURRENT_VISIT_ID + "=?"),
                eq(new String[]{opdDetails.getBaseEntityId(), opdDetails.getCurrentVisitId()}),
                isNull(), isNull(), isNull(), eq("1")
        );

        doReturn(sqLiteDatabase).when(opdDetailsRepository).getReadableDatabase();

        assertNotNull(opdDetailsRepository.findOne(opdDetails));
    }

    @Test(expected = NotImplementedException.class)
    public void testDeleteShouldThrowNotImplementedException() {
        opdDetailsRepository.delete(new OpdDetails());
    }

    @Test(expected = NotImplementedException.class)
    public void testFindAllShouldThrowNotImplementedException() {
        opdDetailsRepository.findAll();
    }
}