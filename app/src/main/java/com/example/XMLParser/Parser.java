package com.example.XMLParser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;

public class Parser {
    public static dict parseXML(InputStream in) throws Exception{
        dict d = new dict();
        ArrayList<sent> sents = new ArrayList<>();
        sent s = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in,"utf-8");
        int type = parser.getEventType();
        while(type!=XmlPullParser.END_DOCUMENT){
            switch (type){
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:{
                    String name = parser.getName();
                    if("ps".equals(name)){
                        d.setPs(parser.nextText());
                    }
                    else if("pos".equals(name)){
                        d.setPos(parser.nextText());
                    }
                    else if("acceptation".equals(name)){
                        d.setAcceptation(parser.nextText());
                    }
                    else if("sent".equals(name)){
                        if(s!=null)
                            sents.add(s);
                        s = new sent();
                    }
                    else if("orig".equals(name)){
                        String orig = parser.nextText();
                        int left = 0,right = orig.length()-1;
                        while(orig.charAt(left)==' '||orig.charAt(left)=='\n')
                            left++;
                        while(orig.charAt(right)==' '||orig.charAt(right)=='\n')
                            right--;
                        orig = orig.substring(left,right+1);
                        s.setOrig(orig);
                    }
                    else if("trans".equals(name)){
                        String trans = parser.nextText();
                        int left = 0,right = trans.length()-1;
                        while(trans.charAt(left)==' '||trans.charAt(left)=='\n')
                            left++;
                        while(trans.charAt(right)==' '||trans.charAt(right)=='\n')
                            right--;
                        trans = trans.substring(left,right+1);
                        s.setTrans(trans);
                    }
                    break;
                }
                case XmlPullParser.END_TAG:{
                    d.setSents(sents);
                    break;
                }
            }
            type = parser.next();
        }
        return d;
    }
}
