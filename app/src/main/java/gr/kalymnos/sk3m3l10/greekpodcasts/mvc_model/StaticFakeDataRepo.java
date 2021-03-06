package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Category;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;

public class StaticFakeDataRepo implements DataRepository {

    private static final long SLEEP_TIME = 500;

    private static final String IMG_URL = "https://images-na.ssl-images-amazon.com/images/I/51-hDsBas0L.jpg";
    private static final String IMG_URL_2 = "http://cdn2.wpbeginner.com/wp-content/uploads/2017/02/category-description.jpg";
    private static final String IMG_URL_3 = "https://images.pexels.com/photos/219998/pexels-photo-219998.jpeg?auto=compress&cs=tinysrgb&h=350";
    private static final String IMG_URL_4 = "https://images.pexels.com/photos/33044/sunflower-sun-summer-yellow.jpg?auto=compress&cs=tinysrgb&h=350";

    private static final String PODCASTER_ID = "podcaster id";
    private static final String EPISODES_ID_1 = "episodes id 1";
    private static final String EPISODES_ID_2 = "episodes id 2";
    private static final String EPISODES_ID_3 = "episodes id 3";
    private static final String EPISODES_ID_4 = "episodes id 4";
    private static final String EPISODES_ID_5 = "episodes id 5";
    private static final String EPISODES_ID_6 = "episodes id 6";
    private static final String FIREBASE_ID_1 = "FIREBASE id 1";
    private static final String FIREBASE_ID_2 = "FIREBASE id 2";

    private static final String CATEGORY_1 = "Education";
    private static final String CATEGORY_2 = "News & Politics";
    private static final String CATEGORY_3 = "Stories";
    private static final String CATEGORY_4 = "Arts & Entertainment";
    private static final String CATEGORY_5 = "Music";
    private static final String CATEGORY_6 = "Sports";
    private static final String CATEGORY_ID_1 = "education";
    private static final String CATEGORY_ID_2 = "news_politics";
    private static final String CATEGORY_ID_3 = "stories";
    private static final String CATEGORY_ID_4 = "arts";
    private static final String CATEGORY_ID_5 = "music";
    private static final String CATEGORY_ID_6 = "sports";


    private static final String EPISODE_URL_1 = "https://www.mfiles.co.uk/mp3-downloads/chopin-nocturne-op9-no2.mp3";
    private static final String EPISODE_URL_2 = "https://www.mfiles.co.uk/mp3-downloads/francisco-tarrega-lagrima.mp3";
    private static final String EPISODE_URL_3 = "https://www.mfiles.co.uk/mp3-downloads/bach-bourree-in-e-minor-piano.mp3";
    private static final String EPISODE_URL_4 = "https://www.mfiles.co.uk/mp3-downloads/moonlight-movement1.mp3";
    private static final String EPISODE_URL_5 = "https://www.mfiles.co.uk/mp3-downloads/mozart-horn-concerto4-3-rondo.mp3";
    private static final String EPISODE_URL_6 = "https://www.mfiles.co.uk/mp3-downloads/edvard-grieg-peer-gynt1-morning-mood-piano.mp3";
    private static final String DESCRIPTION = "This is a descriptions and bla bla bla.";
    private static final String PUSH_ID = "Promotion push id";

    private OnCreatedPodcastListener onCreatedPodcastListener;
    private OnAudioUploadSuccessListener onAudioUploadSuccessListener;

    @Override
    public void setOnAllPodcastsFetchedSuccessListener(OnAllPodcastsFetchedSuccessListener listener) {

    }

    @Override
    public List<Podcast> fetchAllPodcasts() {
        sleep(SLEEP_TIME);
        Podcast p1 = new Podcast("All about the World Cup 2018", CATEGORY_ID_3, IMG_URL, "This is my description for the world cup and bla bla bklabla bla bklabla bla bklabla bla bklabla bla bkla."
                , PODCASTER_ID, FIREBASE_ID_1, EPISODES_ID_1);
        Podcast p2 = new Podcast("The Beautiful Kalymnos", CATEGORY_ID_5, IMG_URL_2, "Is Kalymnos the most beautiful island in the world? And bla bla bklabla bla bklabla bla bklabla bla bklabla bla bkla."
                , PODCASTER_ID, FIREBASE_ID_2, EPISODES_ID_2);
        List<Podcast> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);
        return list;
    }

    @Override
    public List<Podcast> fetchPodcastsFromPodcaster(String podcasterPushId) {
        return fetchAllPodcasts();
    }

    @Override
    public List<Podcast> fetchPodcastsFromCategory(String categoryPushId) {
        return fetchAllPodcasts();
    }

    @Override
    public List<Podcast> fetchStarredPodcasts(Cursor starredPodcastsCursor) {
        sleep(SLEEP_TIME);
        return fetchAllPodcasts();
    }

    @Override
    public List<Episode> fetchEpisodes(String episodesId) {
        sleep(SLEEP_TIME);
        List<Episode> list = new ArrayList<>();
        if (episodesId.equals(EPISODES_ID_1)) {
            Episode e1 = new Episode("Episode 1", EPISODE_URL_1, 30, 34, System.currentTimeMillis());
            e1.setFirebasePushId(EPISODES_ID_1);
            Episode e2 = new Episode("Episode 2", EPISODE_URL_2, 20, 34, System.currentTimeMillis());
            e2.setFirebasePushId(EPISODES_ID_2);
            Episode e3 = new Episode("Episode 3", EPISODE_URL_3, 30, 34, System.currentTimeMillis());
            e3.setFirebasePushId(EPISODES_ID_3);
            list.add(e1);
            list.add(e2);
            list.add(e3);
        } else {
            Episode e4 = new Episode("Episode 4", EPISODE_URL_4, 60, 64, System.currentTimeMillis());
            e4.setFirebasePushId(EPISODES_ID_4);
            Episode e5 = new Episode("Episode 5", EPISODE_URL_5, 50, 64, System.currentTimeMillis());
            e5.setFirebasePushId(EPISODES_ID_5);
            Episode e6 = new Episode("Episode 6", EPISODE_URL_6, 60, 64, System.currentTimeMillis());
            e6.setFirebasePushId(EPISODES_ID_6);
            list.add(e4);
            list.add(e5);
            list.add(e6);
        }
        return list;
    }

    @Override
    public List<Category> fetchAllCategories() {
        sleep(SLEEP_TIME);
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(CATEGORY_1, DESCRIPTION, IMG_URL_2, CATEGORY_ID_1));
        categories.add(new Category(CATEGORY_2, DESCRIPTION, IMG_URL_2, CATEGORY_ID_2));
        categories.add(new Category(CATEGORY_3, DESCRIPTION, IMG_URL_2, CATEGORY_ID_3));
        categories.add(new Category(CATEGORY_4, DESCRIPTION, IMG_URL_2, CATEGORY_ID_4));
        categories.add(new Category(CATEGORY_5, DESCRIPTION, IMG_URL_2, CATEGORY_ID_5));
        categories.add(new Category(CATEGORY_6, DESCRIPTION, IMG_URL_2, CATEGORY_ID_6));
        return categories;
    }

    @Override
    public List<PromotionLink> fetchPromotionLinks(String podcasterId) {
        sleep(SLEEP_TIME);
        List<PromotionLink> links = new ArrayList<>();
//        links.add(new PromotionLink("Support me on Patreon", "https://www.patreon.com/powerplaychess", PUSH_ID, PUSH_ID));
//        links.add(new PromotionLink("Friend me on Facebook", "https://www.facebook.com/madonna/", PUSH_ID, PUSH_ID));
        return links;
    }

    @Override
    public Podcaster fetchPodcaster(String pushId) {
        sleep(SLEEP_TIME);
        return null;
    }

    @Override
    public String fetchPodcasterName(String pushId) {
        sleep(SLEEP_TIME);
        return "Solomontas";
    }

    @Override
    public void createPodcaster(@NonNull Activity activity, @NonNull String pushId, Runnable actionAfterCreation) {
        sleep(SLEEP_TIME);
        activity.runOnUiThread(actionAfterCreation);
    }

    @Override
    public boolean podcasterExists(String pushId) {
        sleep(SLEEP_TIME);
        return true;
    }

    @Override
    public String getCurrentUserUid() {
        return PODCASTER_ID;
    }

    @Override
    public void createNewPodcast(Podcast podcast) {
        sleep(SLEEP_TIME);
        //  Just a fake condition to mimic that the operation was successful or not
        boolean isPodcastCreated = true;
        podcast.setFirebasePushId(FIREBASE_ID_1);
        if (isPodcastCreated) {
            if (onCreatedPodcastListener != null) {
                onCreatedPodcastListener.onPodcastCreationSuccess(podcast.getFirebasePushId());
            }
        } else {
            if (onCreatedPodcastListener != null) {
                onCreatedPodcastListener.onPodcastCreationFailure("Podcast was not created!");
            }
        }
    }

    @Override
    public void setOnCreatedPodcastListener(OnCreatedPodcastListener listener) {
        onCreatedPodcastListener = listener;
    }

    @Override
    public void uploadImage(String podcastPushId, byte[] posterData) {
        sleep(2000);
    }

    @Override
    public void uploadAudio(Uri audioUri, String podcastPushId) {
        sleep(SLEEP_TIME);
        if (onAudioUploadSuccessListener!=null)
            onAudioUploadSuccessListener.onSuccess();
    }

    @Override
    public void setOnAudioUploadSuccessListener(OnAudioUploadSuccessListener listener) {
        onAudioUploadSuccessListener=listener;
    }

    private static void sleep(long timeMilli) {
        try {
            Thread.sleep(timeMilli);
        } catch (InterruptedException e) {
            Log.e(StaticFakeDataRepo.class.getSimpleName(), "Problem at sleep(). " + e.getMessage());
        }
    }
}
