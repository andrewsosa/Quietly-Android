package com.andrewsosa.quietly;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by andrewsosa on 10/7/15.
 */
public class Quietly extends Application {

    public final static String DEFAULT_FILTER = "default_filter";
    public final static String SHARED_PRFERENCES = "quietly";

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Event.class);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "PrYmEUwZZPfEnz7Hx826TpqQIHTsvGd1GzbnhO22", "XAqu51GC8MHyZfDHDOr4JdF12KgxqDwGkuwJQe40");
    }
}
