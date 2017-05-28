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
package eu.kalodiodev.customersnote.customers;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.kalodiodev.customersnote.R;
import eu.kalodiodev.customersnote.data.Customer;
import eu.kalodiodev.customersnote.data.source.CustomersContract;

/**
 * Cursor - RecyclerView Adapter
 *
 * @author Raptodimos Athanasios
 */
public class CursorRecyclerViewAdapter extends RecyclerView.Adapter<CursorRecyclerViewAdapter.CustomerViewHolder> {
    private static final String TAG = "CursorRecyclerViewAdapt";

    private Cursor mCursor;
    private OnCustomerClickListener mListener;

    public interface OnCustomerClickListener {
        void onEditClick(Customer customer);
    }

    /**
     * Cursor - RecyclerView Adapter Constructor
     *
     * @param cursor cursor to be used
     */
    public CursorRecyclerViewAdapter(Cursor cursor, OnCustomerClickListener listener) {
        this.mCursor = cursor;
        this.mListener = listener;
    }

    @Override
    public CustomerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list_item, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerViewHolder holder, int position) {
        if((mCursor != null) && (mCursor.getCount() > 0)) {
            if(!mCursor.moveToPosition(position)) {
                throw new IllegalStateException("Couldn't move cursor to position " + position);
            }

            final Customer customer = Customer.from(mCursor);

            // Set customer ID
            customer.setId(mCursor.getLong(mCursor.getColumnIndex(CustomersContract.Columns._ID)));

            holder.firstName.setText(customer.getFirstName());
            holder.lastName.setText(customer.getLastName());
            holder.profession.setText(customer.getProfession());

            // Item Click Listener
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onEditClick(customer);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: starts");
        if((mCursor == null) || (mCursor.getCount() == 0)) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    /**
    * Swap in a new Cursor, returning the old cursor.
    * The returned old Cursor is <em>not</em> closed.
    *
    * @param newCursor The new cursor to be used
    * @return The previously set Cursor, or null if there wasn't one.
    * If the given new Cursor is the same instance as the previously set Cursor, null is also returned.
    */
    public Cursor swapCursor(Cursor newCursor) {
        if(newCursor == mCursor) {
            return null;
        }

        final Cursor oldCursor = mCursor;
        mCursor = newCursor;
        if(newCursor != null) {
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyItemRangeRemoved(0, getItemCount());
        }

        return oldCursor;
    }

    /**
     * Customer View Holder
     *
     * <p>Contains License Plate, Manufacturer and Model textViews</p>
     */
    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "CustomerViewHolder";

        TextView firstName = null;
        TextView lastName = null;
        TextView profession = null;

        CustomerViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "CustomerViewHolder: starts");

            this.firstName = (TextView) itemView.findViewById(R.id.cli_first_name);
            this.lastName = (TextView) itemView.findViewById(R.id.cli_last_name);
            this.profession = (TextView) itemView.findViewById(R.id.cli_profession);
        }
    }
}
