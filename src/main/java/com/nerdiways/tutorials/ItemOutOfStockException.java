package com.nerdiways.tutorials;

public class ItemOutOfStockException extends Exception{
    public ItemOutOfStockException(String message){
        super(message);
    }
}
