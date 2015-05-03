package org.voiddog.mblog.preference;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * 设置类SharaPref
 * Created by Dog on 2015/4/14.
 */
@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface Config {
    @DefaultString("NO NAME")
    String user_name();
    @DefaultString("男")
    String sex();
    @DefaultString("")
    String head_image();
    String _token();
    @DefaultBoolean(false)
    boolean auto_login();
}
