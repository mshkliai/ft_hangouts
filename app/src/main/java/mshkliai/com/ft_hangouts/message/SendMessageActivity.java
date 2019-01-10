package mshkliai.com.ft_hangouts.message;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import mshkliai.com.ft_hangouts.database.tables.MessagesTable;
import mshkliai.com.ft_hangouts.listView.Model;
import mshkliai.com.ft_hangouts.R;
import mshkliai.com.ft_hangouts.database.tables.ContactsTable;

public class SendMessageActivity extends AppCompatActivity {
    private int                 id;
    private Model               contact;

    private TextView            name, phone;
    private EditText            fieldMessage;
    private ImageButton         button;
    private Toolbar             toolbar;
    private SharedPreferences   settings;
    private ContactsTable       contactsTable;
    private MessagesTable       messagesTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        id = getIntent().getIntExtra("id", 1);

        initInstruments();
        initToolbar();
        setSettings();
    }

    public void trySendMessage(View view) {
        String  bodyMessage = fieldMessage.getText().toString();
        int     bodyMessageLength = bodyMessage.length();

        if (bodyMessageLength != 0 && bodyMessageLength < 1000) {
            contact.setMail(bodyMessage);
            SendSMSMessage();

            contactsTable.close();
            messagesTable.close();
            finish();
        }
    }

    private void    initInstruments() {
        toolbar = findViewById(R.id.toolbarPage);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        contactsTable = new ContactsTable(this);
        messagesTable = new MessagesTable(this);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.number);
        fieldMessage = findViewById(R.id.message);
        button = findViewById(R.id.send_button);
    }

    private void    initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.send_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(
                v -> {
                    contactsTable.close();
                    messagesTable.close();
                    finish();
                } );
    }

    private void    setSettings() {
        // Fields
        contact = contactsTable.getContactById(id);
        name.setText(contact.getName());
        phone.setText(contact.getNumber());

        // Color
        int color = settings.getInt("theme", R.color.alliance);

        toolbar.setBackgroundColor( getResources().getColor(color) );
        button.setColorFilter( getResources().getColor(color) );
    }

    private void SendSMSMessage() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), R.string.error5, Toast.LENGTH_LONG).show();
        } else {
            sendMessage();
        }
    }

    private void    sendMessage() {
        String     number = contact.getNumber();

        if (number.charAt(0) != '+') {
            Toast.makeText(getApplicationContext(), R.string.error6, Toast.LENGTH_LONG).show();
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contact.getNumber(), null, contact.getMail(), null, null);

            messagesTable.addMessage(contact.getName(), "Sent: " + contact.getMail(), contact.getPhoto());
            Toast.makeText(getApplicationContext(), R.string.send_success, Toast.LENGTH_LONG).show();
        }
    }
}