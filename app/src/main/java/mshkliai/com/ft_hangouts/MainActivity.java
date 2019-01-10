package mshkliai.com.ft_hangouts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import mshkliai.com.ft_hangouts.database.tables.MessagesTable;
import mshkliai.com.ft_hangouts.listView.CustomAdapter;
import mshkliai.com.ft_hangouts.listView.Model;
import mshkliai.com.ft_hangouts.Contacts.ContactsActivity;
import mshkliai.com.ft_hangouts.Settings.SettingsActivity;
import mshkliai.com.ft_hangouts.database.tables.ContactsTable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar                 toolbar;
    private DrawerLayout            drawerLayout;
    private NavigationView          navigationView;
    private View                    headerView;
    private TextView                nameProfile, mobileNumber, mail;
    private ImageView               imageView;

    private ListView                listMessages;

    private ContactsTable           contactsTable;
    private MessagesTable           messagesTable;
    private SharedPreferences       settings;
    private ActionBarDrawerToggle   toggle;

    private long                    timer = System.currentTimeMillis();

    public static final int        MY_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        new Thread(this::initInstruments).run();
        setSettings();
    }

    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings || id == R.id.Contacts || id == R.id.exit) {
            contactsTable.close();
        }

        switch (id) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.Contacts:
                startActivity(new Intent(this, ContactsActivity.class));
                break;
            case R.id.exit:
                ActivityCompat.finishAffinity(this);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @SuppressLint("DefaultLocale")
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setSettings();

            timer = (System.currentTimeMillis() - timer) / 1000;

            Toast.makeText(this, String.format("%02d:%02d", timer / 60, timer % 60),
                    Toast.LENGTH_SHORT).show();
        }
        else {
            timer = System.currentTimeMillis();
        }
    }

    private  void initInstruments() {
        // Drawable layout
        drawerLayout = findViewById(R.id.activity_main);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Header Instruments
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        headerView = navigationView.inflateHeaderView(R.layout.header);

        nameProfile = headerView.findViewById(R.id.name);
        mobileNumber = headerView.findViewById(R.id.phone_number);
        mail = headerView.findViewById(R.id.mail);
        imageView = headerView.findViewById(R.id.main_header_photo);

        listMessages = findViewById(R.id.listDialogs);

        // Helpers
        contactsTable = new ContactsTable(this);
        messagesTable = new MessagesTable(this);

        if (contactsTable.sizeDatabase() == 0) {
            contactsTable.addContact("No name", "380", "No mail", null);
        }
        settings = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @SuppressLint("SetTextI18n")
    private void    initNavigationView() {
        Model me = contactsTable.getContactById(0);
        Bitmap  photo = me.getPhoto();

        nameProfile.setText(me.getName());
        mobileNumber.setText(me.getNumber());
        mail.setText(me.getMail());
        if (photo != null) {
            imageView.setImageBitmap(photo);
        }
    }

    private void    setSettings() {
        initNavigationView();
        setThemeSettings();
        setLanguageSettings();
        setListMessages();
        setPermissionSettings();
    }

    private void    setListMessages() {
        List<Model> messages = messagesTable.getMessagesForList();

        listMessages.setAdapter( new CustomAdapter(this, messages) );
    }

    private void    setThemeSettings() {
        @ColorInt int     color = getResources()
                .getColor( settings.getInt("theme", R.color.alliance) );

        toolbar.setBackgroundColor(color);
        headerView.setBackgroundColor(color);
    }

    private void    setLanguageSettings() {
        int             language = settings.getInt("locale", R.id.def);
        Configuration   configuration = new Configuration();

        if (language != R.id.def) {
            configuration.locale = new Locale( language == R.id.rus ? "ru" : "en" );
        } else {
            configuration.locale = Locale.getDefault();
        }

        getBaseContext().getResources().updateConfiguration(configuration, null);

        // Settings text
        Menu    menu = navigationView.getMenu();

        menu.findItem(R.id.Contacts).setTitle(R.string.contacts);
        menu.findItem(R.id.settings).setTitle(R.string.settings);
        menu.findItem(R.id.exit).setTitle(R.string.exit);
    }

    private void    setPermissionSettings() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 0);
        }

        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, 0);
        }
    }
}