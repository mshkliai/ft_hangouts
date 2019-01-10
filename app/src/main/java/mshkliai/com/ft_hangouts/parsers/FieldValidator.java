package mshkliai.com.ft_hangouts.parsers;

import android.content.Context;
import android.widget.Toast;

import mshkliai.com.ft_hangouts.R;

public class FieldValidator {
    public static boolean  validateNameFields(String firstName, String lastName, Context context) {
        int firstNameLength = firstName.length();
        int lastNameLength = lastName.length();

        if (firstNameLength < 3 || firstNameLength > 15 || lastNameLength < 3 || lastNameLength > 15) {
            Toast.makeText(context, R.string.error1, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean   validateMobileNumber(String number, Context context) {
        if (number.length() != 12) {
            Toast.makeText(context, R.string.error2, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (number.contains("+")) {
            Toast.makeText(context, R.string.error3, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean   validateMail(String mail, Context context) {
        int length = mail.length();

        if (length < 5 || length > 25) {
            Toast.makeText(context, R.string.error4, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}