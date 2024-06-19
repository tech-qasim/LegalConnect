package com.example.blacksuits.Notifications;


public class NotificationTemplate {



    private String token;
    NotificationTemplate(String s)
    {
        token = s;
    }


    public static String message = "{" +
            "  \"to\": \"%s\"," +
            "  \"data\": {" +
            "       \"body\":\"%s\"," +
            "       \"for\":\"%s\"" +
            "       \"forUid\":\"%s\"" +
            "   }" +
            "}";
}
