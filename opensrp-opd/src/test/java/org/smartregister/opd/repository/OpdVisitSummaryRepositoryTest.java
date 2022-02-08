package org.smartregister.opd.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smartregister.opd.BaseUnitTest;
import org.smartregister.opd.pojo.OpdVisitSummary;
import org.smartregister.opd.utils.OpdDbConstants;

import java.util.List;
import java.util.UUID;

public class OpdVisitSummaryRepositoryTest extends BaseUnitTest {

    private OpdVisitSummaryRepository opdVisitSummaryRepository;

    @Mock
    private SQLiteDatabase mockSQLiteDatabase;

    @Mock
    private Cursor mockCursor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        opdVisitSummaryRepository = spy(new OpdVisitSummaryRepository());
    }

    @Test
    public void testGetOpdVisitSummariesShouldReturnOneVisitSummaryIfBaseEntityIdIsNotNull() {
        String baseEntityId = UUID.randomUUID().toString();
        String[] visitIds = new String[]{"1"};
        doReturn(mockSQLiteDatabase).when(opdVisitSummaryRepository).getReadableDatabase();
        doReturn(visitIds).when(opdVisitSummaryRepository).getVisitIds(eq(baseEntityId), eq(1));

        doReturn("2020-10-02 11:09:17").when(mockCursor).getString(5);
        doReturn(5).when(mockCursor).getColumnIndex(OpdDbConstants.Column.OpdVisit.VISIT_DATE);

        String diagnosis = "Diagnosis A";
        doReturn(diagnosis).when(mockCursor).getString(4);
        doReturn(4).when(mockCursor).getColumnIndex(OpdDbConstants.Column.OpdDiagnosisDetail.DIAGNOSIS);

        doReturn("Diagnosis Type A").when(mockCursor).getString(3);
        doReturn(3).when(mockCursor).getColumnIndex(OpdDbConstants.Column.OpdDiagnosisDetail.TYPE);

        doReturn("Malaria").when(mockCursor).getString(2);
        doReturn(1).when(mockCursor).getColumnIndex(OpdDbConstants.Column.OpdDiagnosisDetail.DISEASE);

        doReturn("ab12302").when(mockCursor).getString(0);
        doReturn(0).when(mockCursor).getColumnIndex(OpdDbConstants.Column.OpdDiagnosisDetail.CODE);

        doReturn(mockCursor).when(mockSQLiteDatabase).rawQuery(anyString(), isNull());
        doAnswer(new Answer<Boolean>() {
            int count = 0;
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                if(count == 0) {
                    count++;
                    return true;
                }
                return false;
            }
        }).when(mockCursor).moveToNext();

        List<OpdVisitSummary> resultOpdVisitSummaries = opdVisitSummaryRepository.getOpdVisitSummaries(baseEntityId, 1);

        verify(mockCursor).close();
        assertFalse(resultOpdVisitSummaries.isEmpty());
        assertEquals(1, resultOpdVisitSummaries.size());
        assertEquals(diagnosis, resultOpdVisitSummaries.get(0).getDiagnosis());
    }

    @Test
    public void testGetVisitIdsShouldReturnOneVisitIdIfBaseEntityIdIsNotNull() {
        String baseEntityId = UUID.randomUUID().toString();
        doReturn(mockSQLiteDatabase).when(opdVisitSummaryRepository).getReadableDatabase();
        doReturn("1").when(mockCursor).getString(eq(0));
        doReturn(mockCursor).when(mockSQLiteDatabase).rawQuery(anyString(), isNull());
        doAnswer(new Answer<Boolean>() {
            int count = 0;
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                if(count == 0) {
                    count++;
                    return true;
                }
                return false;
            }
        }).when(mockCursor).moveToNext();

        String[] resultVisitIds = opdVisitSummaryRepository.getVisitIds(baseEntityId, 1);
        assertEquals(1, resultVisitIds.length);
        assertEquals("1", resultVisitIds[0]);
    }

    @Test
    public void testGetVisitPageCountShouldReturnOneIfBaseEntityIsNotNull() {
        String baseEntityId = UUID.randomUUID().toString();
        doReturn(mockSQLiteDatabase).when(opdVisitSummaryRepository).getReadableDatabase();
        doReturn(6).when(mockCursor).getInt(eq(0));
        doReturn(mockCursor).when(mockSQLiteDatabase).rawQuery(anyString(), isNull());
        doAnswer(new Answer<Boolean>() {
            int count = 0;
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                if(count == 0) {
                    count++;
                    return true;
                }
                return false;
            }
        }).when(mockCursor).moveToNext();

        int resultPageCount = opdVisitSummaryRepository.getVisitPageCount(baseEntityId);
        assertEquals(1, resultPageCount);
    }
}