package org.voiddog.mblog.preference;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Dog on 2015/4/14.
 */
@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface Config {
    String user_name();
    String sex();
    String head_image();
    String _token();
}
