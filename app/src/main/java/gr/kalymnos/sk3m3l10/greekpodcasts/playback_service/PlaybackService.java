package gr.kalymnos.sk3m3l10.greekpodcasts.playback_service;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.PlaybackUtils;

public class PlaybackService extends MediaBrowserServiceCompat implements PlaybackInfoListener {

    private static final String TAG = PlaybackService.class.getSimpleName();
    private static final String SESSION_TAG = "MyMediaSession";
    private static final float DEFAULT_PLAYBACK_SPEED = 1f;


    private MediaSessionCompat session;
    private MediaSessionCallback sessionCallback;

    private PlaybackStateCompat.Builder stateBuilder;
    private MediaMetadataCompat.Builder metadataBuilder;

    private PlayerHolder player;
    private int reportedPlayerState;
    private String cachedMediaId = null;
    private String cachedAlbumArtUrl = null;

    private AsyncTask<String, Void, List<Episode>> fetchEpisodesTask;
    private AsyncTask<String, Void, String> fetchPodcasterNameTask;
    private List<MediaBrowserCompat.MediaItem> cachedMediaItems;
    private String cachedPodcastersName;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeMediaSession();
        initializePlayer();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        //  Allow anyone to browse this service, by returning something.
        return new BrowserRoot(getString(R.string.app_name), rootHints);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Bundle rootHints = getBrowserRootHints();
        if (areRootHintsValid(rootHints)) {

            //  Cache the podcast poster url, it will be used as album art
            cachedAlbumArtUrl = rootHints.getString(Podcast.POSTER_KEY);

            //  Detatch to fetch the data in another thread
            result.detach();

            if (fetchEpisodesTask == null) {
                initializeFetchEpisodesTask(result);
            } else {
                //  If an older instance of this thread is running, cancel it
                fetchEpisodesTask.cancel(true);
                fetchEpisodesTask = null;
                initializeFetchEpisodesTask(result);
            }

            if (fetchPodcasterNameTask == null) {
                initializeFetchPodcasterNameTask();
            } else {
                fetchPodcasterNameTask.cancel(true);
                fetchPodcasterNameTask = null;
                initializeFetchPodcasterNameTask();
            }

            //  Start fetching the name. After the name is fetched then fetchPodcasterNameTask thread executes also fetchEpisodesTask
            fetchPodcasterNameTask.execute(new String[]{
                    rootHints.getString(Podcaster.PUSH_ID_KEY) /*   Id to fetch podcaster (artist) name*/,
                    rootHints.getString(Episode.EPISODES_KEY /* Id to fetch the episodes (tracks) list*/)});

            return;
        }
        result.sendResult(null);
    }

    private void initializeFetchPodcasterNameTask() {

        final String[] episodeTaskParam = new String[1];

        fetchPodcasterNameTask = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                //  Cache the episodeTaskParam to execute fetchEpisodesTask in onPostExecute().
                episodeTaskParam[0] = strings[1];
                //  TODO:   Swap with a real service.
                DataRepository repo = new StaticFakeDataRepo();
                return repo.fetchPodcasterName(strings[0]);
            }

            @Override
            protected void onPostExecute(String podcasterName) {
                //  Start fetching episodes (track) list.
                if (!TextUtils.isEmpty(episodeTaskParam[0])) {
                    fetchEpisodesTask.execute(episodeTaskParam[0]);
                } else {
                    throw new IllegalArgumentException(TAG + ": Cannot fetch episodes (track) list with null parameter");
                }

                if (!TextUtils.isEmpty(podcasterName)) {
                    //  An artist name was fetched, cache it to set it to metadata later
                    cachedPodcastersName = podcasterName;
                } else {
                    throw new IllegalArgumentException(TAG + ": Cannot fetch podcaster (artist) name with null parameter");
                }
            }
        };
    }

    private void initializeFetchEpisodesTask(@NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        fetchEpisodesTask = new AsyncTask<String, Void, List<Episode>>() {
            @Override
            protected List<Episode> doInBackground(String... strings) {
                //  TODO:   Replace with a real service.
                DataRepository repo = new StaticFakeDataRepo();
                return repo.fetchPodcastEpisodes(strings[0]);
            }

            @Override
            protected void onPostExecute(List<Episode> episodes) {
                if (episodes != null && episodes.size() > 0) {
                    //  Data is fetched. Create mediaItems for clients
                    List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
                    for (Episode episode : episodes) {

                        Bundle extras = new Bundle();
                        extras.putLong(Episode.DATE_KEY, episode.getDateMilli());
                        extras.putInt(Episode.MINUTES_KEY, episode.getMinutes());
                        extras.putInt(Episode.SECONDS_KEY, episode.getSeconds());

                        MediaDescriptionCompat mediaDescription = new MediaDescriptionCompat
                                .Builder()
                                .setTitle(episode.getTitle())
                                .setMediaId(episode.getFirebasePushId())
                                .setMediaUri(Uri.parse(episode.getUrl()))
                                .setIconUri(Uri.parse(cachedAlbumArtUrl))
                                .setExtras(extras)
                                .build();
                        mediaItems.add(new MediaBrowserCompat.MediaItem(mediaDescription, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
                    }
                    //  MediaItems are ready, cache them for use in onPlayFromMediaId and send the result
                    cachedMediaItems = mediaItems;
                    result.sendResult(mediaItems);
                } else {
                    //  No dataFetched send nothing
                    result.sendResult(null);
                }
            }
        };
    }

    private void initializeMediaSession() {
        session = new MediaSessionCompat(this, SESSION_TAG);

        //  Enable callbacks from MediaButtons and TransportControls
        session.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //  Set an initial PlaybackState with ACTION_PLAY so media buttons can start the player
        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE);
        session.setPlaybackState(stateBuilder.build());

        //  Handle callbacks from a MediaController
        session.setCallback(sessionCallback = new MediaSessionCallback());

        setSessionToken(session.getSessionToken());
    }

    @Override
    public void onDurationChanged(int duration) {

    }

    @Override
    public void onPositionChanged(int position) {

    }

    @Override
    public void onStateChanged(int state) {
        //  First cache the state to check whether is valid to take actions
        // on the actual player (MediaPlayer in my case)
        reportedPlayerState = state;

        //  Set the playback state for the session, the metadata will be set inside the sessionCallback methods.
        if (reportedPlayerState == State.STARTED || reportedPlayerState == State.PAUSED || reportedPlayerState == State.STOPPED) {
            int currentPosition = PlaybackUtils.validStateToGetPosition(state) ? player.getCurrentPosition() : 0;
            session.setPlaybackState(PlaybackUtils.getPlaybackStateFromPlayerState(state, currentPosition, DEFAULT_PLAYBACK_SPEED, stateBuilder));
        }
    }

    @Override
    public void onPlaybackPrepared() {
        sessionCallback.onPlayFromMediaId(cachedMediaId, null);
    }

    @Override
    public void onPlaybackCompleted() {

    }

    @Override
    public void onErrorHappened(String errorMessage) {

    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        //  This audioFocusChangeListener pauses playback when audio focus is lost

        private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
                audioFocus -> this.onPause();

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            if (PlaybackUtils.gainedAudioFocus(PlaybackService.this, audioFocusChangeListener)) {
                if (cachedMediaItems != null && cachedMediaItems.size() > 0) {
                    if (!TextUtils.isEmpty(mediaId)) {
                        if (PlaybackUtils.validStateToPlay(reportedPlayerState)) {
                            //  Start this service to be alive. For now it was bound and we don't
                            //  want the playback to stop when all clients unbind
                            startService(new Intent(PlaybackService.this, PlaybackService.class));
                            session.setActive(true);
                            //  Only onPlaybackPrepared() calls this method, so the media is prepared to be played
                            player.play();
                            //  Set metadata. Playback state is set in onStateChanged(), don't need to do it here
                            session.setMetadata(metadataBuilder
                                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, cachedPodcastersName)
                                    /*  For album art it only accepts a bitmap, so better fetch a bitmap instead of poster url*/
                                    .build());
                        }
                    } else {
                        throw new UnsupportedOperationException(TAG + ": Cannot play an item with null or empty mediaId");
                    }
                }
            }
        }

        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            if (cachedMediaItems != null && cachedMediaItems.size() > 0) {
                MediaBrowserCompat.MediaItem item = getMediaItemByMediaId(cachedMediaItems, mediaId);
                if (item != null) {
                    cachedMediaId = mediaId;
                    //  Reseting the player will set its state to State.IDLE, thus validating setDataSource()
                    player.reset();
                    if (PlaybackUtils.validStateToSetDataSource(reportedPlayerState)) {
                        //  When the item is loaded onPlaybackPrepared() will be called
                        player.loadUrl(item.getDescription().getMediaUri().toString());
                    } else {
                        throw new UnsupportedOperationException(TAG + ": Invalid state to prepare the MediaPlayer.");
                    }
                }
            }
        }

        @Override
        public void onPrepare() {
            if (cachedMediaItems != null && cachedMediaItems.size() > 0) {
                //  Just play the first item
                MediaBrowserCompat.MediaItem item = cachedMediaItems.get(0);
                cachedMediaId = item.getMediaId();
                //  Reseting the player will set its state to State.IDLE, thus validating setDataSource()
                player.reset();
                if (PlaybackUtils.validStateToSetDataSource(reportedPlayerState)) {
                    //  When the item is loaded onPlaybackPrepared() will be called
                    player.loadUrl(item.getDescription().getMediaUri().toString());
                } else {
                    throw new UnsupportedOperationException(TAG + ": Invalid state to prepare the MediaPlayer.");
                }
            }
        }

        @Override
        public void onPause() {
            if (PlaybackUtils.validStateToPause(reportedPlayerState)) {
                player.pause();
            }
        }

        @Override
        public void onStop() {
            if (PlaybackUtils.validStateToStop(reportedPlayerState)) {
                PlaybackUtils.abandonAudioFocus(PlaybackService.this, audioFocusChangeListener);
                session.setActive(false);
                PlaybackService.this.stopSelf();
            } else {
                throw new UnsupportedOperationException(TAG + ": Invalid state to pause the MediaPlayer.");
            }
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }

    }

    private MediaBrowserCompat.MediaItem getMediaItemByMediaId(List<MediaBrowserCompat.MediaItem> list, String id) {
        for (MediaBrowserCompat.MediaItem item : list) {
            if (item.getMediaId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    private void initializePlayer() {
        player = new MediaPlayerHolder(this);
        player.setPlaybackInfoListener(this);
    }

    private boolean areRootHintsValid(Bundle rootHints) {
        //  One key to fetch all the episodes of the podcast, one key for the podcast poster
        //  and one key to fetch the podcaster (artist) name.
        if (rootHints != null && rootHints.containsKey(Episode.EPISODES_KEY)
                && rootHints.containsKey(Podcast.POSTER_KEY)
                && rootHints.containsKey(Podcaster.PUSH_ID_KEY)) {
            return true;
        }
        return false;
    }
}
