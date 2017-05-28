package eu.kalodiodev.customersnote.provider;

import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.kalodiodev.customersnote.data.Customer;
import eu.kalodiodev.customersnote.data.source.CustomersContract;
import eu.kalodiodev.customersnote.data.source.CustomersProvider;

/**
 * Customers Content Provider Test
 *
 * @author Raptodimos Athanasios
 */
@RunWith(AndroidJUnit4.class)
public class CustomersProviderTest extends ProviderTestCase2<CustomersProvider> {

    private Customer customer = new Customer("First Name", "Last Name",
            "Profession", "Company Name", "123456", "Dummy Notes");

    public CustomersProviderTest() {
        super(CustomersProvider.class, CustomersProvider.CONTENT_AUTHORITY);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        setContext(InstrumentationRegistry.getTargetContext());
    }


    @Test
    public void testInsert() {

        Uri uri = getMockContentResolver()
                .insert(CustomersContract.CONTENT_URI, customer.toContentValues());

        assertNotNull(uri);
    }

    @Test
    public void testEmptyCursor() {
        Cursor c = getMockContentResolver()
                .query(CustomersContract.CONTENT_URI, null, null, null, null);

        assertNotNull(c);
        c.close();
    }

    @Test
    public void testQuery() {
        Cursor c = getMockContentResolver()
                .query(CustomersContract.CONTENT_URI, null, null, null, null);

        assertTrue(c.getCount() >= 1);
        assertTrue(c.getColumnIndex(CustomersContract.Columns._ID) >=0);
        c.close();
    }

    @Test
    public void testUpdate() {
        // Insert Customer
        getMockContentResolver().insert(CustomersContract.CONTENT_URI, customer.toContentValues());

        Customer newCustomer = new Customer("New First Name", "New Last Name",
                "New Profession", "New Company Name", "7896", "New Dummy notes");

        String selection = CustomersContract.Columns.CUSTOMERS_FIRST_NAME + " = ?";
        String[] selectionArgs = {"First Name"};

        int count = getMockContentResolver().update(CustomersContract.CONTENT_URI,
                newCustomer.toContentValues(), selection, selectionArgs);

        assertTrue("Updated entries must be greater than zero", count > 0);
    }

    @Test
    public void testDelete() {
        // Insert Customer
        getMockContentResolver().insert(CustomersContract.CONTENT_URI, customer.toContentValues());

        // Delete Customer
        String criteria = CustomersContract.Columns.CUSTOMERS_FIRST_NAME + " = ?";
        String[] selectionArgs = {"First Name"};
        int count = getMockContentResolver().delete(CustomersContract.CONTENT_URI, criteria, selectionArgs);

        assertTrue("Deleted customers must be greater than zero", count > 0);
    }
}
