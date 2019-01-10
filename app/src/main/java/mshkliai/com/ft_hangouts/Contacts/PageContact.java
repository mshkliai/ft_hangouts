package mshkliai.com.ft_hangouts.Contacts;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import mshkliai.com.ft_hangouts.R;
import mshkliai.com.ft_hangouts.database.tables.ContactsTable;
import mshkliai.com.ft_hangouts.listView.Model;
import mshkliai.com.ft_hangouts.parsers.ColorDetector;

public class PageContact extends AppCompatActivity implements IPickResult {
    private int                 id;
    private Toolbar             toolbar;
    private View                header;

    private ImageView           photoView;
    private TextView            nameField, numField, mailField;
    private ImageButton         editButton, deleteButton;

    private SharedPreferences   settings;
    private ContactsTable       contactsTable;
    private AlertDialog.Builder alertBuilder;
    private PickSetup           setup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_contact);

        id = getIntent().getIntExtra("id", 1);

        initInstruments();
        initToolbar();
        setSettings();
    }

    @Override
    public void onPickResult(PickResult r) {
        Bitmap  photo = r.getBitmap();

        photoView.setImageBitmap(photo);
        contactsTable.updateImage(id, photo);
    }

    @SuppressLint("DefaultLocale")
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setSettings();
        }
    }

    public void     deleteContact(View view) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, result) -> {
            if (result == DialogInterface.BUTTON_POSITIVE) {
                new Thread( ()->{
                    contactsTable.removeContact(id);
                    contactsTable.close();
                }).run();
                finish();
            }
        };

        alertBuilder.setMessage(R.string.are_you_sure).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    public void     goToEditContact(View view) {
        Intent  editContactActivity = new Intent(this, EditContactActivity.class);

        editContactActivity.putExtra("id", id);

        startActivity(editContactActivity);
    }

    public void     changePhoto(View view) {
        PickImageDialog.on(this, setup);
    }

    private void    initInstruments() {
        toolbar = findViewById(R.id.toolbarPage);
        header = findViewById(R.id.headerPage);

        photoView = findViewById(R.id.photo_profile);
        nameField = findViewById(R.id.name);
        numField = findViewById(R.id.phone_number);
        mailField = findViewById(R.id.mail);

        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        contactsTable = new ContactsTable(this);
        alertBuilder = new AlertDialog.Builder(this);
        setup = new PickSetup();
    }

    private void    initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.contact);

        toolbar.setNavigationOnClickListener(
                v -> {
                    contactsTable.close();
                    finish();
                } );
    }

    private void    setSettings() {
        setTheme();
        initContact();
    }

    private void    setTheme() {
        int color = settings.getInt("theme", R.color.alliance);
        int resource = ColorDetector.detectButtonTheme(color);

        toolbar.setBackgroundColor( getResources().getColor(color) );
        header.setBackgroundResource(ColorDetector.detectHeaderTheme(color));

        editButton.setBackgroundResource(resource);
        deleteButton.setBackgroundResource(resource);
    }

    private void    initContact() {
        Model contact = contactsTable.getContactById(id);

        nameField.setText( contact.getName() );
        numField.setText( contact.getNumber() );
        mailField.setText( contact.getMail() );

        Bitmap  photo = contact.getPhoto();
        if (photo != null) {
            photoView.setImageBitmap(contact.getPhoto());
        } else {
            photoView.setImageResource(R.drawable.ic_photo_black_24dp);
        }
    }
}