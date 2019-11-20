package com.example.newsFetcher;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {

    public static List<NewsOverview> parseHtmlNews(String result) {
        List<NewsOverview> list = new ArrayList<>();
        Pattern pattern = Pattern
                .compile("<li class=\"txt_Tit new_icn\"><h1><a target=\"_blank\" href=\"(.*)\">(.*)</a></h1></li>\n" +
                        "(\\s*)<li class=\"txt_Info\">(.*)....</li>\n" +
                        "(\\s*)<li class=\"txt_Link\">Published&nbsp;:&nbsp;(.*)</li>");

        Matcher matcher = pattern.matcher(result);
        int i = 0;
        while (matcher.find() && i++ < 10) {
            //获取
            Log.d("#INFO","url:"+matcher.group(1).trim());
            Log.d("#INFO","title:"+matcher.group(2).trim());
            Log.d("#INFO","intro:"+matcher.group(4).trim());
            Log.d("#INFO","date:"+matcher.group(6).trim());

            NewsOverview model = new NewsOverview();
            model.setNewsUrl(matcher.group(1).trim());
            model.setNewsTitle(matcher.group(2).trim());
            model.setNewsDate(matcher.group(6).trim());

            list.add(model);
        }

        //检查
        /*
        for(int j = 0;j<list.size();j++){
            Log.d("rage","title:"+list.get(j).getNewsTitle());
            Log.d("rage","url:"+list.get(j).getNewsUrl());
            Log.d("rage","date:"+list.get(j).getNewsDate());
        }
        */
        return list;
    }

    public static List<String> parseHtmlNewsDetailData(String result) {

        //抓取适配种类1
        List<String> list = new ArrayList<>();
        Pattern pattern1 = Pattern.compile("</p>(\\s*)((<p class=\"MsoNormal\" style=\"text-align: left; line-height: 19.5pt; mso-pagination: widow-orphan;\" align=\"left\"><span style=\"mso-bidi-font-size: 10.5pt; font-family: 'Arial',sans-serif; mso-fareast-font-family: 宋体; color: #404040; mso-font-kerning: 0pt;\" lang=\"EN-US\">(.*)</span></p>(\\s*))*)<p class=\"MsoNormal\"><span style=\"mso-bidi-font-size: 10.5pt;\" lang=\"EN-US\">&nbsp;</span></p><!--repaste.body.end--></p>");
        Matcher matcher1 = pattern1.matcher(result);

        if(matcher1.find()){
            StringBuffer sb = new StringBuffer();
            String br = "";
            Log.d("#INFO","found type 1");
            sb.append(matcher1.group(2).trim());
            Pattern patternDetail = Pattern.compile("<p class=\"MsoNormal\" style=\"text-align: left; line-height: 19.5pt; mso-pagination: widow-orphan;\" align=\"left\"><span style=\"mso-bidi-font-size: 10.5pt; font-family: 'Arial',sans-serif; mso-fareast-font-family: 宋体; color: #404040; mso-font-kerning: 0pt;\" lang=\"EN-US\">(.*)</span></p>(\\s*)");
            Matcher matcherDetail = patternDetail.matcher(sb.toString());
            while (matcherDetail.find()) {
                br = matcherDetail.group(1).trim();
                if (br.contains("<br/>")) {
                    Pattern patternBr = Pattern.compile("(.*?)<br /><br />");
                    Matcher matcherBr = patternBr.matcher(sb.toString());
                    while (matcherBr.find()) {
                        list.add(matcherBr.group(1).trim() + "\n");
                        Log.d("#INFO", "MSG:"+matcherBr.group(1).trim());
                    }
                } else {
                    list.add(matcherDetail.group(1).trim() + "\n");
                    Log.d("#INFO", "MSG:"+matcherDetail.group(1).trim());
                }
            }
            return list;
        }


        //抓取适配种类2
        pattern1 = Pattern.compile("</span></em></p>(\\s*)((<p>(.*)</p>(\\s*))*)<!--repaste.body.end--></p>");
        matcher1 = pattern1.matcher(result);

        if(matcher1.find()){
            StringBuffer sb = new StringBuffer();
            Log.d("#INFO","found type 2");
            sb.append(matcher1.group(2).trim());
            Log.d("#INFO","FULL:"+matcher1.group(2).trim());
            Pattern patternDetail = Pattern.compile("<p>(.*)</p>(\\s*)");
            Matcher matcherDetail = patternDetail.matcher(sb.toString());
            while (matcherDetail.find()) {
                list.add(matcherDetail.group(1).trim() + "\n");
                Log.d("#INFO", "MSG:"+matcherDetail.group(1).trim());
            }
            return list;
        }

        //抓取适配种类3
        pattern1 = Pattern.compile("<p id=\"zt\"><!--repaste.body.begin-->(\\s*)((<p>(.*)</p>(\\s*))*)</div>(\\s*)" + "<style type=\"text/css\">");
        matcher1 = pattern1.matcher(result);
        if(matcher1.find()){
            StringBuffer sb = new StringBuffer();
            Log.d("#INFO","found type 3");
            sb.append(matcher1.group(2).trim());
            Log.d("#INFO","FULL:"+matcher1.group(2).trim());
            Pattern patternDetail = Pattern.compile("lang=\"EN-US\">(.*)</(.*)(\\s*)");
            Matcher matcherDetail = patternDetail.matcher(sb.toString());
            while (matcherDetail.find()) {
                list.add(matcherDetail.group(1).trim() + "\n");
                Log.d("#INFO", "MSG:"+matcherDetail.group(1).trim());
            }
            return list;
        }


        //抓取适配种类4
        pattern1 = Pattern.compile("(\\s*)lang=\"EN-US\">(\\s*)(.*)(\\s*)</span>");
        matcher1 = pattern1.matcher(result);
        int found4 = 0;
        while(matcher1.find()){
            if(found4 == 0){
                Log.d("#INFO","found type 4");
                found4 = 1;
            }
            list.add(matcher1.group(3).trim() + "\n");
            return list;
        }


        //均不适配
        Log.d("#INFO","not found!");
        list.add("-1");
        return list;
    }
}
