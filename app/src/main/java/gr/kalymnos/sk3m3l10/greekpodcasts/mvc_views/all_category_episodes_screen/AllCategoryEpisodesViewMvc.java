package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.all_category_episodes_screen;

import android.support.v7.widget.Toolbar;

import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.ViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;

public interface AllCategoryEpisodesViewMvc extends ViewMvc {

    interface OnPodcastItemClickListener {
        void onItemPodcastClick(int position);
    }

    void bindPodcasts(List<Podcast> podcasts);

    void setOnPodcastItemClickListener(OnPodcastItemClickListener listener);

    Toolbar getToolbar();

    void displayLoadingIndicator(boolean display);
}
