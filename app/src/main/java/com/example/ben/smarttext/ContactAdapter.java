package com.example.ben.smarttext;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private Context context;
    private List<Contact> contactList;

    public ContactAdapter(@NonNull Context context, int resource, @NonNull List<Contact> contactList) {
        super(context, resource, contactList);
        this.context = context;
        this.contactList = contactList;
    }

//   objectspublic ContactAdapter(@NonNull Context context, ArrayList<Contact> contactList) {
//        super(context, 0 , contactList);
//    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.contacts_list_view,parent,false);

        Contact currentContact = contactList.get(position);

        TextView name = listItem.findViewById(R.id.contactName);
        name.setText(currentContact.getName());

        return listItem;
    }
}