/*
 * Copyright (c) 2017 Athanasios Raptodimos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.kalodiodev.customersnote.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.io.Serializable;

import eu.kalodiodev.customersnote.data.source.CustomersContract;

/**
 * Customer Model
 *
 * @author Raptodimos Athanasios
 */
public class Customer implements Serializable{

    public static final long serialVersionUID = 20170405L;

    private long id;
    private int version;

    private String firstName;
    private String lastName;
    private String profession;
    private String companyName;
    private String phoneNumber;
    private String notes;

    /**
     * Customer constructor
     *
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @param profession customer's profession
     * @param companyName customer's companyName
     * @param phoneNumber customer's phone number
     * @param notes customer's notes
     */
    public Customer(String firstName, String lastName, String profession,
                    String companyName, String phoneNumber, String notes) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.profession = profession;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.notes = notes;
    }

    /**
     * Customer constructor
     *
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @param profession customer's profession
     * @param companyName customer's companyName
     * @param phoneNumber customer's phone number
     * @param notes customer's notes
     */
    public Customer(long id, String firstName, String lastName, String profession,
                    String companyName, String phoneNumber, String notes) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profession = profession;
        this.companyName = companyName;
        this.phoneNumber = phoneNumber;
        this.notes = notes;
    }

    /**
     * Customer constructor
     *
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @param profession customer's profession
     */
    public Customer(String firstName, String lastName, String profession) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.profession = profession;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Convert customer to content values
     *
     * @return customer content values
     */
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();

        values.put(CustomersContract.Columns.CUSTOMERS_FIRST_NAME, this.getFirstName());
        values.put(CustomersContract.Columns.CUSTOMERS_LAST_NAME, this.getLastName());
        values.put(CustomersContract.Columns.CUSTOMERS_PROFESSION, this.getProfession());
        values.put(CustomersContract.Columns.CUSTOMERS_COMPANY_NAME, this.getCompanyName());
        values.put(CustomersContract.Columns.CUSTOMERS_PHONE_NUMBER, this.getPhoneNumber());
        values.put(CustomersContract.Columns.CUSTOMERS_NOTES, this.getNotes());

        return values;
    }

    /**
     * Get Customer from Cursor
     *
     * @param cursor cursor contains customer
     * @return customer
     */
    public static Customer from(@NonNull Cursor cursor) {
        return new Customer(
                cursor.getString(cursor.getColumnIndex(CustomersContract.Columns.CUSTOMERS_FIRST_NAME)),
                cursor.getString(cursor.getColumnIndex(CustomersContract.Columns.CUSTOMERS_LAST_NAME)),
                cursor.getString(cursor.getColumnIndex(CustomersContract.Columns.CUSTOMERS_PROFESSION)),
                cursor.getString(cursor.getColumnIndex(CustomersContract.Columns.CUSTOMERS_COMPANY_NAME)),
                cursor.getString(cursor.getColumnIndex(CustomersContract.Columns.CUSTOMERS_PHONE_NUMBER)),
                cursor.getString(cursor.getColumnIndex(CustomersContract.Columns.CUSTOMERS_NOTES)));

    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", profession='" + profession + '\'' +
                ", companyName='" + companyName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != customer.id) return false;
        if (version != customer.version) return false;
        if (firstName != null ? !firstName.equals(customer.firstName) : customer.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(customer.lastName) : customer.lastName != null)
            return false;
        if (profession != null ? !profession.equals(customer.profession) : customer.profession != null) return false;
        if (companyName != null ? !companyName.equals(customer.companyName) : customer.companyName != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(customer.phoneNumber) : customer.phoneNumber != null)
            return false;
        return notes != null ? notes.equals(customer.notes) : customer.notes == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + version;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (profession != null ? profession.hashCode() : 0);
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        return result;
    }
}
