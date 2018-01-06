package com.teknokrait.tomatoclassification.model;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/29/2017.
 */

public class Status {

    private Integer id;
    private String status;

    public Status(){
    }

    public Status(Integer id, String status){
        this.id = id;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
