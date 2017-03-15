package com.easyvaas.elapp.event;


public class SearchEvent {
    public String keyWord;
    public int position = -1;

    public SearchEvent(String keyWord) {
        this.keyWord = keyWord;
    }
    public SearchEvent(String keyWord,int position) {
        this.keyWord = keyWord;
    }
}
