package org.voiddog.mblog.preference;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * 设置类SharaPref
 * Created by Dog on 2015/4/14.
 */
@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface Config {
    String email();
    @DefaultString("NO NAME")
    String nickname();
    @DefaultInt(0)
    int sex();
    @DefaultInt(0)
    int age();
    @DefaultString("")
    String head();
    @DefaultInt(0)
    int moving_num();
    @DefaultInt(0)
    int updata_time();

    @DefaultBoolean(false)
    boolean auto_login();
}
