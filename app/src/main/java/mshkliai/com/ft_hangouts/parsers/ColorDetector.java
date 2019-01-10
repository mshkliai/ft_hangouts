package mshkliai.com.ft_hangouts.parsers;

import mshkliai.com.ft_hangouts.R;

public class ColorDetector {
    public static int   detectButtonTheme(int color) {
        switch (color) {
            case R.color.empire:   return R.drawable.empire_button;
            case R.color.hive:     return R.drawable.hive_button;
            case R.color.union:    return R.drawable.union_button;
            default:            return R.drawable.alliance_button;
        }
    }

    public static int   detectHeaderTheme(int color) {
        switch (color) {
            case R.color.empire:   return R.drawable.empire;
            case R.color.hive:     return R.drawable.hive;
            case R.color.union:    return R.drawable.union;
            default:            return R.drawable.alliance;
        }
    }
}
