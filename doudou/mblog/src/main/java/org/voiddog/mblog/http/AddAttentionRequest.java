package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;
import org.voiddog.mblog.Const;

/**
 * 关注某人
 * Created by Dog on 2015/5/31.
 */
public class AddAttentionRequest extends DHttpRequestBase{
    public String s_email;
    public String t_email;

    @Override
    public String getHost() {
        return Const.HOST + "Contact/add_attention";
    }
}
