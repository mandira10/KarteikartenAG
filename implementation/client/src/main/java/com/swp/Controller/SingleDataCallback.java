package com.swp.Controller;

public interface SingleDataCallback <E>{
    void onSuccess(E data);
    void onFailure(String msg);
}

