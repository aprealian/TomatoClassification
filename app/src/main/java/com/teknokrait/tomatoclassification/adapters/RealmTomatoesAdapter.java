package com.teknokrait.tomatoclassification.adapters;

import android.content.Context;
import com.teknokrait.tomatoclassification.model.Tomato;
import io.realm.RealmResults;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/25/2017.
 */

public class RealmTomatoesAdapter extends RealmModelAdapter<Tomato> {

    public RealmTomatoesAdapter(Context context, RealmResults<Tomato> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}
