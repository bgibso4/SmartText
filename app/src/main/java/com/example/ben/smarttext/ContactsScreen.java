package com.example.ben.smarttext;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsScreen extends AppCompatActivity {
    // The ListView
    private ListView lstNames;
    private SearchView searchContacts;
    private List<Contact> contacts, queriedContacts;
    private ContactAdapter adapter;

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

//    private static final String[] PROJECTION = new String[] {
//            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
//            ContactsContract.Contacts.DISPLAY_NAME,
//            ContactsContract.CommonDataKinds.Phone.NUMBER
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_screen);

        searchContacts = findViewById(R.id.searchContacts);

        // Find the list view
        this.lstNames = findViewById(R.id.lstNames);
        lstNames.setOnClickListener();

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        Set<String> contactSet = appSharedPrefs.getStringSet("ContactsList", new HashSet<>());
        contacts = new ArrayList<>();
        for(String contact : contactSet){
            Contact createContact  = gson.fromJson(contact, Contact.class);
            contacts.add(createContact);
        }
        Collections.sort(contacts, new ContactsComparator());
        adapter = new ContactAdapter(this, 0, new ArrayList<>(contacts));
        lstNames.setAdapter(adapter);

        // perform set on query text listener event
        searchContacts.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queriedContacts = new ArrayList<>();
                for(Contact c: contacts){
                    if(c.getName().toUpperCase().startsWith(query.toUpperCase())){
                        queriedContacts.add(new Contact(c));
                    }
                }
                adapter.updateContacts(queriedContacts);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                queriedContacts = new ArrayList<>();
                for(Contact c: contacts){
                    if(c.getName().toUpperCase().startsWith(newText.toUpperCase())){
                        queriedContacts.add(new Contact(c));
                    }
                }
                adapter.updateContacts(queriedContacts);
                return false;
            }
        });
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    /**
//     * Show the contacts in the ListView.
//     */
//    private void showContacts() {
//        // Check the SDK version and whether the permission is already granted or not.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//        } else {
//            // Android version is lesser than 6.0 or the permission is already granted.
//            contacts = getContactNames();
//            adapter = new ContactAdapter(this, 0, new ArrayList<>(contacts));
//            lstNames.setAdapter(adapter);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission is granted
//                showContacts();
//            } else {
//                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    /**
//     * Read the name of all the contacts.
//     *
//     * @return a list of names.
//     */
//    private List<Contact> getContactNames() {
//        List<Contact> contacts = new ArrayList<>();
//        ContentResolver cr = getContentResolver();
//        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME.toUpperCase()+" ASC");
//        if (cursor != null) {
//            try {
//                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
//                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//
//                while (cursor.moveToNext()) {
//                    contacts.add(new Contact(cursor.getString(nameIndex), cursor.getString(numberIndex)));
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//        return contacts;
//    }
}
