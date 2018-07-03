package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.publish;

import android.graphics.Bitmap;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Episode;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public interface PortofolioPublishViewMvc extends ViewMvc {

    interface OnButtonsClickListener {

        void onEditPodcastClick(Podcast podcast);

        void onEditDescriptionClick(String description);

        void onViewEpisodesClick();

        void onAddEpisodeClick();

        void onPosterClick();
    }

    interface OnPodcastSelectedListener {

        void onPodcastSelected(int position);
    }

    interface OnCategorySelectedListener {

        void onCategorySelected(int position);
    }

    void bindEpisodes(List<Episode> episodes);

    void bindPoster(Bitmap poster);

    void bindDescription(String description);

    void setOnButtonsClickListener(OnButtonsClickListener listener);

    void setOnPodcastSelectedListener(OnPodcastSelectedListener listener);

    void setOnCategorySelectedListener(OnCategorySelectedListener listener);

    boolean onLand();

    void addPodcastsToSpinner(String[] titles);

    void addCategoriesToSpinner(String[] titles);

    void bindTitle(String title);
}
