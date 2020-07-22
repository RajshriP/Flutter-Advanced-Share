package in.mertcan.advancedshare.shareintents;

import java.util.ArrayList;
import java.util.Map;

public class Message {
    String title;
    String msg;
    String subject;
    String type;
    String url;
    ArrayList<String> urls;

    public Message(Map params) {
        if(params == null) return;

        title = (String) params.get("title");
        msg = (String) params.get("msg");
        subject = (String) params.get("subject");
        url = (String) params.get("url");
        type = (String) params.get("url");
        urls = (ArrayList<String>) params.get("urls");
    }
}
