package mshkliai.com.ft_hangouts.Contacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mshkliai.com.ft_hangouts.listView.CustomAdapter;
import mshkliai.com.ft_hangouts.R;
import mshkliai.com.ft_hangouts.database.tables.ContactsTable;
import mshkliai.com.ft_hangouts.listView.Model;
import mshkliai.com.ft_hangouts.message.SendMessageActivity;

public class ContactsActivity extends AppCompatActivity {
    private Toolbar             toolbar;
    private SharedPreferences   settings;
    private ContactsTable       contactsTable;
    private ListView            listContacts;

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initInstruments();
        initToolBar();
        setSettings();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_contact) {
            startActivity( new Intent(this, AddContactActivity.class) );
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_contact, menu);

        return true;
    }

    @SuppressLint("DefaultLocale")
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setSettings();
        }
    }

    private void    initInstruments() {
        toolbar = findViewById(R.id.include);
        listContacts = findViewById(R.id.listContacts);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        contactsTable = new ContactsTable(this);

        this.initListContacts();
    }

    private void    initListContacts() {
        listContacts.setOnItemClickListener( (parent, view, position, id) -> {
            Intent  pageContactActivity = new Intent(this, PageContact.class);

            pageContactActivity.putExtra("id", position + 1);
            startActivity(pageContactActivity);
        });

        listContacts.setLongClickable(true);
        listContacts.setOnItemLongClickListener( (parent, view, position, id) -> {
            PopupMenu popup = new PopupMenu(this, view);

            popup.getMenuInflater().inflate(R.menu.communication_menu, popup.getMenu());
            popup.show();

            popup.setOnMenuItemClickListener(menuItem -> {
                int menuItemItemId = menuItem.getItemId();

                if (menuItemItemId == R.id.message) {
                    Intent  sendMessageActivity = new Intent(this, SendMessageActivity.class);

                    sendMessageActivity.putExtra("id", position + 1);
                    startActivity(sendMessageActivity);
                }
                else if (menuItemItemId == R.id.call) {
                    tryCallContact(view.findViewById(R.id.number));
                }
                return true;
            });
            return true;
        });
    }

    private void    tryCallContact(TextView view) {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            String number = view.getText().toString();
            Intent myIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));

            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
        } else {
            Toast.makeText(this, R.string.error5, Toast.LENGTH_LONG).show();
        }
    }

    private void    initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.contacts);

        toolbar.inflateMenu(R.menu.add_contact);
        toolbar.setNavigationOnClickListener(
                v -> {
                    contactsTable.close();
                    finish();
                } );
    }

    private void    setSettings() {
        setTheme();

        List<Model>   contacts = contactsTable.getContactsForList();

        listContacts.setAdapter( new CustomAdapter(this, contacts) );
    }

    private void    setTheme() {
        int     color = settings.getInt("theme", R.color.alliance);

        toolbar.setBackgroundColor( getResources().getColor(color) );
    }
}