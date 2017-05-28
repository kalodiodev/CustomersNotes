package eu.kalodiodev.customersnote;

/**
 * Constants
 *
 * @author Athanasios Raptodimos
 */
public class Constants {

    // Database constants
    public static class Database {

        public static final String DATABASE_NAME = "customers.db";
        public static final int DATABASE_VERSION = 1;

        // Database backup
        public static final String BACKUP_FOLDER = "/CustomerNotesBackup";
        public static final String BACKUP_FILENAME = "customerNotesBackup.db";
        public static final String SOURCE_DATABASE = "/data/" +
                "eu.kalodiodev.customersnote" + "/databases/" + DATABASE_NAME;

        private Database() {
            // private constructor to prevent instantiation
        }
    }
}