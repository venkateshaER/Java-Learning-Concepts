package com.bikes.exceptions;

public class InvalidBikeModelExecption extends Exception{
    public InvalidBikeModelExecption(String message){
        super(message);
    }
    public InvalidBikeModelExecption(String message, Throwable cause){
        super(message, cause);
    }
}
