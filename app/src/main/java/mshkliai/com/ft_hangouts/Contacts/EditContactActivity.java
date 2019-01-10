package mshkliai.com.ft_hangouts.Contacts;

import mshkliai.com.ft_hangouts.R;
import mshkliai.com.ft_hangouts.Settings.ChangeNameActivity;

public class EditContactActivity extends ChangeNameActivity {

    @Override
    protected void    initInstruments() {
        super.initInstruments();

        getSupportActionBar().setTitle(R.string.editing_contact);
    }

}
