package com.teknokrait.tomatoclassification.realm;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/25/2017.
 */

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.teknokrait.tomatoclassification.model.Tomato;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {

        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {

        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }

    //Refresh the realm istance
    public void refresh() {

        realm.refresh();
    }

    //clear all objects from Tomato.class
    public void clearAll() {

        realm.beginTransaction();
        realm.clear(Tomato.class);
        realm.commitTransaction();
    }

    //find all objects in the Tomato.class
    public RealmResults<Tomato> getTomatoes() {

        return realm.where(Tomato.class).findAll();
    }

    //query a single item with the given id
    public Tomato getTomato(String id) {

        return realm.where(Tomato.class).equalTo("id", id).findFirst();
    }

    //check if Tomato.class is empty
    public boolean hasTomatoes() {

        return !realm.allObjects(Tomato.class).isEmpty();
    }

    public int getNextKey() {
        try {
            Number number = realm.where(Tomato.class).maximumInt("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    //query example
    public RealmResults<Tomato> queryedTomatoes() {

        /*return realm.where(Tomato.class)
                .contains("author", "Author 0")
                .or()
                .contains("title", "Realm")
                .findAll();*/
        return realm.where(Tomato.class)
                .contains("status", "matang")
                .or()
                .findAll();

    }
}