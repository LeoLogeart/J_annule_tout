package com.dl.cynh.jannuletout;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.SimpleAdapter;

public class Utils {

    private static DownloadActivity act;


    public Utils(DownloadActivity activity) {
        act=activity;
    }


    /**
     * Add a value to the list of downloaded podcasts
     * @param seenPodcast string id of the podcast to add to the "seen" list
     */
    public void addSeen(String seenPodcast){
        SharedPreferences sharedPref = act.getPreferences(Context.MODE_PRIVATE);
        String downloaded = sharedPref.getString("Downloaded",null);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(downloaded==null){
            editor.putString("Downloaded",seenPodcast);
        } else {
            editor.putString("Downloaded", downloaded + "," + seenPodcast);
        }
        editor.apply();

        ArrayList<HashMap<String, String>> list = act.getListItem();
        for(HashMap<String,String> item : list){
            if(item.get("description").equals(seenPodcast) && !item.get("day").contains(" ( Téléchargé )")){
                item.put("day", item.get("day")+" ( Téléchargé )");
            }
        }
        updateLayout();
    }

    /**
     * Retrieve the list of downloaded podcasts
     * @return list of downloaded podcasts
     */
    public static List<String> getDownloaded(){
        SharedPreferences sharedPref = act.getPreferences(Context.MODE_PRIVATE);
        String downloaded = sharedPref.getString("Downloaded", "");
        ArrayList<String> seen = new ArrayList<>(Arrays.asList(downloaded.split(",")));
        if(seen.size()>30){
            seen.remove(0);
        }
        return seen;
    }


    /**
     * Parses the html page to get podcast titles and urls
     *
     * @param responseString the whole html page
     *
     */
    public static void parsePage(String responseString, List<String> podcasts, List<String> titles, List<HashMap<String, String>> listItem) {
        String[] items = responseString.split("<item>");
        for(int i=1;i<items.length;i++){
            parseItem(items[i],podcasts,titles,listItem);
        }
    }


    public static void parseItem(String item, List<String> podcasts, List<String> titles, List<HashMap<String, String>> listItem) {
        int i = item.indexOf("enclosure url");
        if(i==0){
            return;
        }
        // url
        int start = item.indexOf("http",i);
        int end = item.indexOf("\"", start);
        podcasts.add(item.substring(start,end));

        //title
        String title;
        start = item.indexOf("<title>")+7;
        end = item.indexOf("</title>", start);
        if(start==0 || end==0){
            title = "";
        } else {
            title = stripTitle(item.substring(start,end));
        }

        //date
        start = item.indexOf("<pubDate>")+9;
        end = item.indexOf("</pubDate>", start);
        String date;
        if(start<=0 || end<=0){
            date = "";
        } else {
            date = item.substring(start,end);
        }
        title=title+getShortDate(date);
        titles.add(title);
        HashMap<String, String> map = new HashMap<>();
        if(Utils.getDownloaded().contains(title)){
            map.put("day", getDay(date)+" ( Téléchargé )");
        } else {
            map.put("day", getDay(date));
        }

        map.put("description", title);
        map.put("img",String.valueOf(R.drawable.inter));
        if(item.contains("fromet")){
            map.put("img",String.valueOf(R.drawable.fromet));
        } else if(item.contains("moment-meurice")){
            map.put("img",String.valueOf(R.drawable.meurice));
        } else if(item.contains("vdb")){
            map.put("img",String.valueOf(R.drawable.vdb));
        } else if(item.contains("Monod")){
            map.put("img",String.valueOf(R.drawable.clara));
        } else if(item.contains("Manoukian")){
            map.put("img",String.valueOf(R.drawable.andre));
        }else if(item.contains("Pablo Mira") || item.contains("pablo-mira")){
            map.put("img",String.valueOf(R.drawable.pablo));
        }else if(item.contains("Isabelle Sorente") || item.contains("isabelle-sorente")){
            map.put("img",String.valueOf(R.drawable.isabelle));
        }else if(item.contains("Juliette Arnaud") || item.contains("juliette-arnaud")){
            map.put("img",String.valueOf(R.drawable.juliette));
        }else if(item.contains("hippolyte") || item.contains("Hippolyte") ){
            map.put("img",String.valueOf(R.drawable.hippolyte));
        }else if(item.contains("17h17")){
            map.put("img",String.valueOf(R.drawable.journal));
        }
        listItem.add(map);

    }

    public static String getShortDate(String date) {
        if(date.equals(""))
            return "";
        String res=" - ";
        for(int i=0;i<date.length();i++){
            if(Character.isDigit(date.charAt(i))){
                if(i+1<date.length() && Character.isDigit(date.charAt(i+1))){
                    res+=date.substring(i,i+2);
                } else {
                    res+=date.charAt(i);
                }
                break;
            }
        }
        return res+"."+getMonthFromStr(date);
    }

    public static String stripTitle(String title) {
        title=title.replaceAll("&quot;","\"");
        String pattern = "\\d{2}.\\d{2}.\\d{4}";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(title);
        if (matcher.find()) {
            // if the title contains 15.10.2015
            return title.substring(0,matcher.start()-1);
        }
        return title;
    }


    /**
     *
     * @param tmp the podcast title
     * @return the number of the month contained in the string
     */
    public static int getMonthFromStr(String tmp) {
        int m;
        if (tmp.contains("Jan")) {
            m = 1;
        } else if (tmp.contains("Feb")) {
            m = 2;
        } else if (tmp.contains("Mar")) {
            m = 3;
        } else if (tmp.contains("Apr")) {
            m = 4;
        } else if (tmp.contains("May")) {
            m = 5;
        } else if (tmp.contains("Jun")) {
            m = 6;
        } else if (tmp.contains("Jul")) {
            m = 7;
        } else if (tmp.contains("Aug")) {
            m = 8;
        } else if (tmp.contains("Sep")) {
            m = 9;
        } else if (tmp.contains("Oct")) {
            m = 10;
        } else if (tmp.contains("Nov")) {
            m = 11;
        } else if (tmp.contains("Dec")) {
            m = 12;
        } else {
            return -1;
        }
        return m;
    }


    /**
     * Gets the day depending on the podcast title
     *
     * @param title the title of the podcast
     * @return the day of the week contained in the title
     */
    private static String getDay(String title) {
        String res = "";
        if (title.contains("Mon")) {
            res = "Lundi";
        } else if (title.contains("Tue") ) {
            res = "Mardi";
        } else if (title.contains("Wed")) {
            res = "Mercredi";
        } else if (title.contains("Thu")) {
            res = "Jeudi";
        } else if (title.contains("Fri")) {
            res = "Vendredi";
        } else if (title.contains("Sat")) {
            res = "Samedi";
        } else if (title.contains("Sun")) {
            res = "Dimanche";
        }
        return res;
    }


    /**
     * Update the layout with values selected by the user
     */
    public static void updateLayout() {
        ArrayList<HashMap<String, String>> list = act.getListItem();
        ArrayList<HashMap<String, String>> newList = new ArrayList<>();
        SharedPreferences sharedPref = act.getPreferences(Context.MODE_PRIVATE);
        boolean integ = sharedPref.getBoolean("integral",true);
        boolean meurice = sharedPref.getBoolean("meurice", true);
        boolean fromet = sharedPref.getBoolean("fromet",true);
        boolean vdb = sharedPref.getBoolean("vdb",true);
        boolean clara = sharedPref.getBoolean("clara",true);
        boolean andre = sharedPref.getBoolean("andre",true);
        boolean hippolyte = sharedPref.getBoolean("hippolyte",true);
        boolean juliette = sharedPref.getBoolean("juliette",true);
        boolean isabelle = sharedPref.getBoolean("isabelle",true);
        boolean pablo = sharedPref.getBoolean("pablo",true);
        boolean journal = sharedPref.getBoolean("journal",true);
        for(HashMap<String,String> item : list){
            if((item.get("img").equals(String.valueOf(R.drawable.andre)) && andre) ||
                    (item.get("img").equals(String.valueOf(R.drawable.meurice)) && meurice) ||
                    (item.get("img").equals(String.valueOf(R.drawable.fromet)) && fromet) ||
                    (item.get("img").equals(String.valueOf(R.drawable.inter)) && integ) ||
                    (item.get("img").equals(String.valueOf(R.drawable.clara)) && clara) ||
                    (item.get("img").equals(String.valueOf(R.drawable.vdb)) && vdb) ||
                    (item.get("img").equals(String.valueOf(R.drawable.hippolyte)) && hippolyte) ||
                    (item.get("img").equals(String.valueOf(R.drawable.juliette)) && juliette) ||
                    (item.get("img").equals(String.valueOf(R.drawable.isabelle)) && isabelle) ||
                    (item.get("img").equals(String.valueOf(R.drawable.pablo)) && pablo) ||
                    (item.get("img").equals(String.valueOf(R.drawable.journal)) && journal)
                    ){
                newList.add(item);
            }
        }
        SimpleAdapter adapt = new SimpleAdapter(
                act.getBaseContext(), newList,
                R.layout.print_item, new String[] { "img", "day",
                "description" }, new int[] { R.id.img, R.id.title,
                R.id.description });
        act.getpodcastList().setAdapter(adapt);
    }

}