package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gr.kalymnos.sk3m3l10.greekpodcasts.R;

public class PortofolioViewMvcImpl implements PortofolioViewMvc {

    private View rootView;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private FloatingActionButton saveButton;
    private TabLayout tabLayout;

    private PortofolioPagerAdapter pagerAdapter;

    public PortofolioViewMvcImpl(LayoutInflater inflater, ViewGroup parent, @NonNull FragmentManager fragmentManager) {
        this.rootView = inflater.inflate(R.layout.activity_portofolio, parent, false);
        initialize(fragmentManager);
    }

    private void initialize(FragmentManager fragmentManager) {
        initializeViews();
        initializeViewPager(fragmentManager);
    }

    private void initializeViews() {
        viewPager = rootView.findViewById(R.id.viewPager);
        toolbar = rootView.findViewById(R.id.toolbar);
        saveButton = rootView.findViewById(R.id.fab_save);
        tabLayout = rootView.findViewById(R.id.tabLayout);
    }

    private void initializeViewPager(FragmentManager fragmentManager) {
        String[] titles = {rootView.getContext().getString(R.string.portofolio_create_label),
                rootView.getContext().getString(R.string.portofolio_published_label),
                rootView.getContext().getString(R.string.portofolio_personal_label)};
        pagerAdapter = new PortofolioPagerAdapter(fragmentManager, titles);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public int getCurrentTabItem() {
        return viewPager.getCurrentItem();
    }

    @Override
    public int getViewPagerId() {
        return viewPager.getId();
    }

    @Override
    public void setOnActionSaveClickListener(OnActionSaveClickListener listener) {
        saveButton.setOnClickListener(view -> {
            if (listener!=null)
                listener.onSaveClick();
        });
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
