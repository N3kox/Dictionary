package com.example.XMLParser;

import java.io.Serializable;
import java.util.ArrayList;

public class dict implements Serializable {
    private String ps;
    private String pos;
    private String acceptation;
    private ArrayList<sent> sents;

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getAcceptation() {
        return acceptation;
    }

    public void setAcceptation(String acceptation) {
        this.acceptation = acceptation;
    }

    public ArrayList<sent> getSents() {
        return sents;
    }

    public void setSents(ArrayList<sent> sents) {
        this.sents = sents;
    }

    @Override
    public String toString() {
        String ret = "ps:"+ps+"\n" +
                "pos:"+pos+"\n" +
                "accptation:"+acceptation+"\n";
        for(int i = 0;i<sents.size();i++){
            ret += "example"+i+":\n" +
                    "orig:"+sents.get(i).getOrig()+"\n" +
                    "trans:"+sents.get(i).getTrans()+"\n";
        }
        return ret;
    }
}
