package se.vgregion.metaservice.keywordservice.exception;

public class KeywordsException extends Exception{

    public KeywordsException(Throwable ex) {
        super(ex);
    }

    public KeywordsException(String message){
        super(message);
    }

}
