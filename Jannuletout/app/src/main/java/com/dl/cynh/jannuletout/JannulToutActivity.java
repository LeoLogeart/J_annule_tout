package com.dl.cynh.jannuletout;

import android.os.Bundle;

import com.cynh.podcastdownloader.context.DownloadActivity;
import com.cynh.podcastdownloader.model.Podcast;
import com.google.firebase.analytics.FirebaseAnalytics;

public class JannulToutActivity extends DownloadActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] types = new String[]{"Intégrales", "Guillaume Meurice", "Clara Dupont Monod", "Thomas VDB",
                "André Manoukian", "Frédéric Fromet", "Hippolyte Girardot", "Pablo Mira",
                "Isabelle Sorente", "Juliette Arnaud", "Journal de 17h17", "Christine Gonzalez", "Mélanie Bauer", "Thomas Bidegain"};
        setAdId("ca-app-pub-9891261141906247/5874151219");
        Podcast.setPodcastTypes(types);
        setParser(new PodcastParser());
        setRssUrl("http://radiofrance-podcast.net/podcast09/rss_18153.xml");
        setFirebase(FirebaseAnalytics.getInstance(this));
        super.onCreate(savedInstanceState);
    }

}
