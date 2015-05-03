package org.voiddog.mblog.http;

/**
 * Created by Dog on 2015/4/4.
 */
public class HttpStruct {
    public class Article{
        public int id;
        public int user_id;
        public String title;
        public String subtitle;
        public String body;
        public String image;
    }

    public class User{
        public int uid;
        public String nickname;
        public String head;
    }

    public class Card{
        public int id;
        public int uid;
        public String nickname;
        public String head;
        public String last_ip;
    }

    public class Token{
        public String _token;
    }

    public class MRTest{

    }
}
