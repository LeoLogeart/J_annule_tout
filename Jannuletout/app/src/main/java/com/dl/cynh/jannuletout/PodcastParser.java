package com.dl.cynh.jannuletout;

import android.support.annotation.Nullable;

import com.cynh.podcastdownloader.model.Podcast;
import com.cynh.podcastdownloader.utils.PodcastParserInterface;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PodcastParser implements PodcastParserInterface {

    private String desc;
    private String date;
    private String type;
    private int image;
    private int duration;

    private String stripTitle(String title) {
        title = title.replaceAll("&quot;", "\"");
        String pattern = "\\d{2}.\\d{2}.\\d{4}";
        Pattern r = Pattern.compile(pattern);
        Matcher matcher = r.matcher(title);
        if (matcher.find()) {
            // if the title contains 15.10.2015
            return title.substring(0, matcher.start() - 1);
        }
        return title;
    }

    private String getDay(String title) {
        String res = "";
        if (title.contains("Mon")) {
            res = "Lundi";
        } else if (title.contains("Tue")) {
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

    private String getShortDate() {
        if (date.equals(""))
            return "";
        String res = " - ";
        for (int i = 0; i < date.length(); i++) {
            if (Character.isDigit(date.charAt(i))) {
                if (i + 1 < date.length() && Character.isDigit(date.charAt(i + 1))) {
                    res += date.substring(i, i + 2);
                } else {
                    res += date.charAt(i);
                }
                break;
            }
        }
        return res + "." + getMonthFromStr(date);
    }

    private int getMonthFromStr(String tmp) {
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

    @Override
    public ArrayList<Podcast> parsePage(String htmlPage) {
        ArrayList<Podcast> podcasts = new ArrayList<>();
        Podcast pod;
        String[] items = htmlPage.split("<item>");
        for (int i = 1; i < items.length; i++) {
            pod = parseItem(items[i]);
            if (pod != null) {
                podcasts.add(pod);
            }
        }
        return podcasts;
    }

    private Podcast parseItem(String item) {
        String url = findUrl(item);
        if (url == null) return null;

        desc = findDescription(item);
        date = findDate(item);
        desc += getShortDate();
        image = R.drawable.inter;
        type = "Intégrales";

        findImageAndType(item);
        findDuration(item);
        return new Podcast(getDay(date), desc, image, url, type, duration);
    }

    private void findDuration(String item) {
        int start;
        int end;
        start = item.indexOf("<itunes:duration>") + 17;
        end = item.indexOf("</itunes:duration>", start);
        String durationStr;
        if (start <= 0 || end <= 0) {
            durationStr = "";
        } else {
            durationStr = item.substring(start, end);
        }
        duration = getDurationFromString(durationStr);
    }

    private int getDurationFromString(String durationStr) {
        String[] units = durationStr.split(":");
        return (Integer.parseInt(units[0]) * 3600 + Integer.parseInt(units[1]) * 60 + Integer.parseInt(units[2])) * 1000;
    }

    private void findImageAndType(String item) {
        if (item.contains("fromet")) {
            image = R.drawable.fromet;
            type = "Frédéric Fromet";
        } else if (item.contains("moment-meurice")) {
            image = R.drawable.meurice;
            type = "Guillaume Meurice";
        } else if (item.contains("vdb")) {
            image = R.drawable.vdb;
            type = "Thomas VDB";
        } else if (item.contains("chronique de Clara Dupont-Monod")) {
            image = R.drawable.clara;
            type = "Clara Dupont Monod";
        } else if (item.contains("Manoukian") && !item.contains("VANHOENACKER")) {
            image = R.drawable.andre;
            type = "André Manoukian";
        } else if (item.contains("Pablo Mira") || item.contains("pablo-mira")) {
            image = R.drawable.pablo;
            type = "Pablo Mira";
        } else if (item.contains("Isabelle Sorente") || item.contains("isabelle-sorente")) {
            image = R.drawable.isabelle;
            type = "Isabelle Sorente";
        } else if (item.contains("Juliette Arnaud") || item.contains("juliette-arnaud")) {
            image = R.drawable.juliette;
            type = "Juliette Arnaud";
        } else if (item.contains("hippolyte") || item.contains("Hippolyte")) {
            image = R.drawable.hippolyte;
            type = "Hippolyte Girardot";
        } else if (item.contains("17h17")) {
            image = R.drawable.journal;
            type = "Journal de 17h17";
        } else if (item.contains("gonzalez")){
            image = R.drawable.gonzalez;
            type = "Christine Gonzalez";
        } else if (item.contains("Bauer")){
            image = R.drawable.bauer;
            type = "Mélanie Bauer";
        } else if (item.contains("bidegain") || item.contains("Bidegain")){
            image = R.drawable.bidegain;
            type = "Thomas Bidegain";
        }
    }

    private String findDate(String item) {
        int start;
        int end;
        start = item.indexOf("<pubDate>") + 9;
        end = item.indexOf("</pubDate>", start);
        String date;
        if (start <= 0 || end <= 0) {
            date = "";
        } else {
            date = item.substring(start, end);
        }
        return date;
    }

    private String findDescription(String item) {
        int start;
        int end;
        String title;
        start = item.indexOf("<title>") + 7;
        end = item.indexOf("</title>", start);
        if (start == 0 || end == 0) {
            title = "";
        } else {
            title = stripTitle(item.substring(start, end));
        }
        return title;
    }

    @Nullable
    private String findUrl(String item) {
        int i = item.indexOf("enclosure url");
        if (i == 0) {
            return null;
        }
        // url
        int start = item.indexOf("http", i);
        int end = item.indexOf("\"", start);
        return item.substring(start, end);
    }
}
