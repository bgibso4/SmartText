package com.example.ben.smarttext;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private Context context;
    private List<Contact> contactList, filteredList;

    ContactAdapter(@NonNull Context context, int resource, @NonNull List<Contact> contactList) {
        super(context, resource, contactList);
        this.context = context;
        this.contactList = contactList;
        this.filteredList = contactList;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            List<Contact> tempList=new ArrayList<>();
            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if(charSequence != null && contactList!=null) {
                for(Contact contact : contactList){
                    //do whatever you wanna do here
                    //adding result set output array
                    if(contact.getName().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        tempList.add(contact);
                    }
                }
                //following two lines is very important
                //as publish result can only take FilterResults objects
                filterResults.values = tempList;
                filterResults.count = tempList.size();
            }
            else if(contactList!=null){
                tempList = contactList;
                filterResults.values = tempList;
                filterResults.count = tempList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredList = (ArrayList<Contact>) filterResults.values;
            if (filterResults.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.contacts_list_view,parent,false);

        Contact currentContact = filteredList.get(position);

        TextView name = listItem.findViewById(R.id.contactName);
        name.setText(currentContact.getName());
        TextView number = listItem.findViewById(R.id.contactNumber);
        number.setText(currentContact.getPhoneNumber());

        //CheckBox selected = listItem.findViewById(R.id.contactSelected);

        return listItem;
    }

    void updateContacts(List<Contact> contactList){
        this.contactList.clear();
        this.contactList.addAll(contactList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

}
