package org.voiddog.mblog.http;

import org.voiddog.lib.http.DHttpRequestBase;

/**
 *
 * Created by Dog on 2015/4/4.
 */
public class MyHttpRequest {
    public static class UserLogin extends MyHttpRequestBase {
        public UserLogin(String email, String password){
            super("login");
            requestParams.put("email", email);
            requestParams.put("password", password);
        }
    }

    public static class MTest extends MyHttpRequestBase{

        public MTest() {
            super("test");
        }
    }

    public static class GetArticleList extends MyHttpRequestBase{

        public GetArticleList(int page, int num) {
            super("get_article_list");
            requestParams.put("page", Integer.toString(page));
            requestParams.put("num", Integer.toString(num));
        }
    }

    public static class GetArticle extends MyHttpRequestBase{

        public GetArticle(int id) {
            super("get_article_detail");
            requestParams.put("id", Integer.toString(id));
        }
    }

    public static class GetToken extends MyHttpRequestBase{

        public GetToken() {
            super("get_token");
            method = GET;
        }
    }
}
