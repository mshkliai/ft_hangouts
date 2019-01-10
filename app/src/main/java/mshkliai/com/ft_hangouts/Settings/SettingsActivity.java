package mshkliai.com.ft_hangouts.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.Locale;

import mshkliai.com.ft_hangouts.listView.Model;
import mshkliai.com.ft_hangouts.MainActivity;
import mshkliai.com.ft_hangouts.R;
import mshkliai.com.ft_hangouts.database.tables.ContactsTable;
import mshkliai.com.ft_hangouts.parsers.ColorDetector;

public class SettingsActivity extends AppCompatActivity implements IPickResult {
    //header
    private Toolbar                 toolbar;
    private ImageView               imageView;
    private View                    viewHeader;
    private TextView                nameProfile, phoneNumber, mail;
    private Button                  changeThemeButton, changeLanguageButton;
    private SharedPreferences       settings;


    //Instruments
    private PickSetup               setup;
    private ContactsTable           contactsTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initInstruments();
        initToolBar();
        initHeader();

        setSettings();
    }

    private  void    initInstruments() {
        viewHeader = findViewById(R.id.settings_header);

        nameProfile = viewHeader.findViewById(R.id.name);
        phoneNumber = viewHeader.findViewById(R.id.phone_number);
        imageView = viewHeader.findViewById(R.id.photo_profile);
        mail = viewHeader.findViewById(R.id.mail);

        changeThemeButton = findViewById(R.id.change_theme);
        changeLanguageButton = findViewById(R.id.change_language);

        setup = new PickSetup();
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        contactsTable = new ContactsTable(this);
    }

    private void    initHeader() {
        Model me = contactsTable.getContactById(MainActivity.MY_ID);
        Bitmap  photo = me.getPhoto();

        nameProfile.setText(me.getName());
        phoneNumber.setText(me.getNumber());
        mail.setText(me.getMail());
        if (photo != null) {
            imageView.setImageBitmap(photo);
        }

        getSupportActionBar().setTitle(R.string.settings);
    }

    private void    initToolBar() {
        toolbar = findViewById(R.id.settings_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar();

        toolbar.setNavigationOnClickListener(
                v -> {
                    contactsTable.close();
                    finish();
                } );
    }

    @SuppressLint("DefaultLocale")
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setSettings();
        }
    }

    public void changeImage(android.view.View view) {
        PickImageDialog.on(this, setup);
    }

    public void     changeTheme(View view) {
        PopupMenu   popup = new PopupMenu(this, view);

        popup.getMenuInflater().inflate(R.menu.menu_change_theme, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(menuItem -> {
            int theme = 0;

            switch (menuItem.getItemId()) {
                case R.id.alliance: theme = R.color.alliance;   break;
                case R.id.empire:   theme = R.color.empire;     break;
                case R.id.hive:     theme = R.color.hive;       break;
                case R.id.union:    theme = R.color.union;      break;
            }

            if (theme == 0) {
                return super.onOptionsItemSelected(menuItem);
            }
            else {
                settings.edit().putInt("theme", theme).apply();
                setThemeSettings();
                return true;
            }
        });
    }

    public void     changeLanguage(View view) {
        PopupMenu popup = new PopupMenu(this, view);

        popup.getMenuInflater().inflate(R.menu.language_menu, popup.getMenu());
        popup.getMenu().findItem(settings.getInt("locale", R.id.def)).setChecked(true);

        popup.show();

        popup.setOnMenuItemClickListener( (menuItem)-> {
            popup.getMenu().findItem(settings.getInt("locale", R.id.def)).setChecked(false);
            menuItem.setChecked(true);

            settings.edit().putInt("locale", menuItem.getItemId()).apply();
            setLanguageSettings();

            return true;
        });
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            final Bitmap bitmap = r.getBitmap();

            imageView.setImageBitmap(bitmap);

            new Thread( ()->contactsTable.updateImage(MainActivity.MY_ID, bitmap) ).run();
        }
    }

    public void goToChangeName(View view) {
        contactsTable.close();

        startActivity( new Intent(this, ChangeNameActivity.class) );
    }

    private void    setSettings() {
        initHeader();
        setThemeSettings();
        setLanguageSettings();
    }

    private void    setThemeSettings() {
        int     color = settings.getInt("theme", R.color.alliance);
        int     resource;

        toolbar.setBackgroundColor( getResources().getColor(color) );

        resource = ColorDetector.detectHeaderTheme(color);

        viewHeader.setBackgroundResource(resource);
        changeThemeButton.setBackgroundResource(resource);
        changeLanguageButton.setBackgroundResource(resource);
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

        toolbar.setTitle(R.string.settings);
        changeThemeButton.setText(R.string.change_theme);
        changeLanguageButton.setText(R.string.change_language);
    }
}