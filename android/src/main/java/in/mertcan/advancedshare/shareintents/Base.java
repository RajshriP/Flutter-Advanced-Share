package in.mertcan.advancedshare.shareintents;

import io.flutter.plugin.common.PluginRegistry.Registrar;

import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Map;

import in.mertcan.advancedshare.FileHelper;

public abstract class Base {
    protected final Registrar registrar;
    protected Map params;
    protected String title = "Share";
    protected ArrayList<FileHelper> fileHelpers;
    protected Intent intent;

    public Base(Registrar registrar) {
        this.registrar = registrar;
    }

    public int share(Map params) {
        this.params = params;
        Message message = new Message(params);

        this.intent = new Intent();
        if(message.urls != null && !message.urls.isEmpty() ) {
            this.intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        } else {
            this.intent.setAction(Intent.ACTION_SEND);
        }
        this.intent.setType("text/plain");
        fileHelpers = getFileHelpers(message);

        if (message.title != null) {
            title = message.title;
        }

        if (message.msg != null) {
            intent.putExtra(Intent.EXTRA_TEXT, message.msg);
        }

        if (message.subject != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, message.subject);
        }

        if(message.urls != null) {
            ArrayList<Uri> uris = new ArrayList<>();
            String type = null; // assumption: all the files have the same type
            for(FileHelper fileHelper: fileHelpers) {
                if(fileHelper.isFile()) {
                    uris.add(fileHelper.getUri());
                    type = fileHelper.getType();
                }
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            intent.setType(type);
        }
        else if (message.url != null) {
            FileHelper fileHelper = fileHelpers.get(0);
            if (fileHelper != null && fileHelper.isFile()) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, fileHelper.getUri());
                intent.setType(fileHelper.getType());
            }
        }
        return 0;
    }

    protected void openChooser() {
        Intent chooser = Intent.createChooser(intent, title);
        if (registrar.activity() == null) {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            registrar.context().startActivity(chooser);
        } else {
            registrar.activity().startActivity(chooser);
        }
    }

    protected ArrayList<FileHelper> getFileHelpers(Message message) {
        ArrayList<FileHelper> fileHelpers = new ArrayList<>();
        if(message.urls != null) {
            for (String url : message.urls) {
                fileHelpers.add(getFileHelper(url, message.type));
            }
        }
        else if (message.url != null) {
            fileHelpers.add(getFileHelper(message.url, message.type));
        }
        return fileHelpers;
    }

    protected FileHelper getFileHelper(String url, String type) {
        if (type != null) {
            return new FileHelper(registrar, (String) url, (String) params.get("type"));
        } else {
            return new FileHelper(registrar, (String) url);
        }
    }
}