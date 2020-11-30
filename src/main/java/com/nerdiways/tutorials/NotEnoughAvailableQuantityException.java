package com.nerdiways.tutorials;

public class NotEnoughAvailableQuantityException extends Exception{

    public NotEnoughAvailableQuantityException(String message){
        super(message);
    }
}
