package mshkliai.com.ft_hangouts.Contacts;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import mshkliai.com.ft_hangouts.R;
import mshkliai.com.ft_hangouts.database.tables.ContactsTable;
import mshkliai.com.ft_hangouts.parsers.ColorDetector;
import mshkliai.com.ft_hangouts.parsers.FieldValidator;

public class AddContactActivity extends AppCompatActivity implements IPickResult {
    private Toolbar             toolbar;

    private SharedPreferences   settings;
    private ContactsTable       contactsTable;
    private PickSetup           setup;


    protected EditText          firstName, lastName, mail, number;
    protected ImageView         photoProfile;

    protected Bitmap            bitmap = null;

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        initInstruments();
        initToolbar();
        setSettings();
    }

    public void     tryCreateContact(View view) {
        // Name validation
        String  firstNameS = firstName.getText().toString();
        String  lastNameS = lastName.getText().toString();

        if ( !FieldValidator.validateNameFields(firstNameS, lastNameS, this) ) {
            return;
        }

        String  numberS = number.getText().toString();

        if ( !FieldValidator.validateMobileNumber(numberS, this) ) {
            return;
        }

        String  mailS = mail.getText().toString();

        if ( !FieldValidator.validateMail(mailS, this) ) {
            return;
        }
        // Filling database
        if ( contactsTable.addContact(firstNameS + " " + lastNameS, numberS, mailS, bitmap) ) {
            Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT).show();
            // Releasing resources
            contactsTable.close();
            finish();
        }
    }

    public void changeImage(android.view.View view) {
        PickImageDialog.on(this, setup);
    }

    private void    initInstruments() {
        toolbar = findViewById(R.id.toolbar);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        mail = findViewById(R.id.mail);
        number = findViewById(R.id.phone_number);
        photoProfile = findViewById(R.id.photo_profile);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        contactsTable = new ContactsTable(this);
        setup = new PickSetup();
    }

    private void    initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.new_contact);

        toolbar.setNavigationOnClickListener(
                v -> {
                    contactsTable.close();
                    finish();
                } );
    }

    private void    setSettings() {
        int color = settings.getInt("theme", R.color.alliance);

        toolbar.setBackgroundColor( getResources().getColor(color) );
        findViewById(R.id.imageButton).setBackgroundResource( ColorDetector.detectButtonTheme(color) );
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            bitmap = r.getBitmap();

            photoProfile.setImageBitmap(bitmap);
        }
    }
}