package eu.kalodiodev.customersnote;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import eu.kalodiodev.customersnote.utils.backup.IBackupDBTask;
import eu.kalodiodev.customersnote.utils.backup.IRestoreDBTask;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Main Presenter Test
 *
 * @author Athanasios Raptodimos
 */

public class MainPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MainContract.View view;

    @Mock
    private IBackupDBTask backupDBTask;

    @Mock
    private IRestoreDBTask restoreDBTask;

    @Captor
    private ArgumentCaptor<IBackupDBTask.BackupEvents> backupDBEventsCaptor;

    private MainPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mPresenter = new MainPresenter(view, backupDBTask, restoreDBTask);
    }

    @Test
    public void shouldRequestWritePermissionOnBackup() {

        // When presenter is asked to backup database without write permission granted
        mPresenter.backupDatabase(false, false);

        // Then write permission request should be shown
        verify(view).requestWritePermission();
    }

    @Test
    public void shouldPerformDatabaseBackup() {

        // When presenter is asked to backup database with write permission granted
        mPresenter.backupDatabase(true, false);

        // Then backup task performed
        verify(backupDBTask).perform();
    }

    @Test
    public void shouldShowReadOnlyStorageWarningOnBackup() {

        verify(backupDBTask).initTask(anyInt(), anyString(), anyString(), anyString(),
                backupDBEventsCaptor.capture());

        // Simulate Backup readonly storage task callback
        backupDBEventsCaptor.getValue().onBackupReadOnly(anyInt());

        // Then readonly backup problem warning shown
        verify(view).showProblemWarning(MainContract.WarningMessage.BACKUP_READ_ONLY);
    }

    @Test
    public void shouldShowStorageMissingWarningOnBackup() {

        // Simulate Backup Problem task callback
        mPresenter.onBackupProblem(anyInt());

        // Then backup storage missing warning shown
        verify(view).showProblemWarning(MainContract.WarningMessage.BACKUP_STORAGE_MISSING);
    }

    @Test
    public void shouldShowOverwriteBackupFileConfirmation() {

        // Simulate Backup file already exists task callback
        mPresenter.onBackupFileAlreadyExists(100);

        // Then overwrite confirmation shown
        verify(view).showOverwriteBackupFileConfirmation();
    }

    @Test
    public void shouldShowBackupCompletedMessage() {

        // Simulate Backup completed task callback
        mPresenter.onBackupComplete(100);

        // Then completed message shown
        verify(view).showBackupCompletedMessage();
    }

    @Test
    public void shouldRequestReadPermissionOnRestore() {

        // When presenter is asked to restore database without read permission granted
        mPresenter.restoreDatabase(false, false);

        // Then read permission request should be shown
        verify(view).requestReadPermission();
    }

    @Test
    public void shouldPerformBackupRestore() {

        // When presenter is asked to restore database with read permission granted
        mPresenter.restoreDatabase(true, false);

        // Then database restore should be performed
        verify(restoreDBTask).perform();
    }

    @Test
    public void shouldShowRestoreOverwriteDatabaseConfirmation() {
        // Simulate Database restore overwrite confirmation required
        mPresenter.onRestoreOverwriteRequired(100);

        // Then overwrite confirmation shown
        verify(view).showRestoreOverwriteDatabaseConfirmation();
    }

    @Test
    public void shouldShowFileNotFoundWarningOnDatabaseRestore() {
        // Simulate Database restore file not found
        mPresenter.onRestoreFileNotFound(100);

        // Then restore database file not found warning shown
        verify(view).showProblemWarning(MainContract.WarningMessage.RESTORE_FILE_NOT_FOUND);
    }

    @Test
    public void shouldShowDatabaseVersionMismatchWarningOnDatabaseRestore() {
        // Simulate Database version mismatch
        mPresenter.onRestoreDatabaseVersionMismatch(100);

        // Then restore database version mismatch warning shown
        verify(view).showProblemWarning(MainContract.WarningMessage.RESTORE_VERSION_MISMATCH);
    }

    @Test
    public void shouldShowExternalStorageMissingOnDatabaseRestore() {
        // Simulate external storage missing
        mPresenter.onRestoreProblem(100);

        // Then external storage missing warning shown
        verify(view).showProblemWarning(MainContract.WarningMessage.RESTORE_PROBLEM);
    }

    @Test
    public void shouldShowRestoreCompletedMessage() {
        // Simulate database restore completed
        mPresenter.onRestoreComplete(100);

        // Then restore completed message should be shown
        verify(view).showRestoreCompletedMessage();
    }

    @Test
    public void shouldRefreshCustomersListOnRestoreCompleted() {
        // Simulate database restore completed
        mPresenter.onRestoreComplete(100);

        // Then should refresh customers list
        verify(view).refreshCustomersList();
    }
}