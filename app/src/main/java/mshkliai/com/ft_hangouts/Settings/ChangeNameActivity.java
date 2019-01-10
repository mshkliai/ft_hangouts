package mshkliai.com.ft_hangouts.Settings;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import mshkliai.com.ft_hangouts.listView.Model;
import mshkliai.com.ft_hangouts.database.tables.ContactsTable;
import mshkliai.com.ft_hangouts.parsers.ColorDetector;
import mshkliai.com.ft_hangouts.parsers.FieldValidator;
import mshkliai.com.ft_hangouts.R;


public class ChangeNameActivity extends AppCompatActivity {
    private EditText                    firstName, lastName, phoneNumberField, mail;
    private Toolbar                     toolbar;

    private ContactsTable               contactsTable;
    private int                         id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        id = getIntent().getIntExtra("id", 0);

        initInstruments();
        initInputFields();
        setSettings();
    }

    protected void    initInstruments() {

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.change_profile);

        toolbar.setNavigationOnClickListener(
                v -> {
                    contactsTable.close();
                    finish();
                }
        );

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumberField = findViewById(R.id.phone_number);
        mail = findViewById(R.id.mail);

        contactsTable = new ContactsTable(this);
    }

    private void    initInputFields() {
        Model me = contactsTable.getContactById(id);

        String  nameField = me.getName();
        String  mobileNumber = me.getNumber();
        String  mailField = me.getMail();

        phoneNumberField.setText( mobileNumber.replaceFirst("\\+", "") );
        if (!nameField.equals("No name")) {
            String[] name = nameField.split(" ");

            firstName.setText(name[0]);
            lastName.setText(name[1]);
        }
        if (!mailField.equals("No mail")) {
            mail.setText(mailField);
        }
    }

    public void     trySaveChanges(View view) {
        String  firstNameString = firstName.getText().toString();
        String  lastNameString = lastName.getText().toString();
        String  mobNum = phoneNumberField.getText().toString();
        String  mailString = mail.getText().toString();

        if ( !FieldValidator.validateNameFields(firstNameString, lastNameString, this)
                || !FieldValidator.validateMobileNumber(mobNum, this)
                || !FieldValidator.validateMail(mailString, this) ) {
            return;
        }

        contactsTable.updateFields(id,
                firstNameString + " " + lastNameString, mobNum, mailString);

        contactsTable.close();
        finish();
    }

    private void   setSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int     color = settings.getInt("theme", R.color.alliance);

        toolbar.setBackgroundColor( getResources().getColor(color) );
        findViewById(R.id.imageButton).setBackgroundResource( ColorDetector.detectButtonTheme(color) );
    }
}