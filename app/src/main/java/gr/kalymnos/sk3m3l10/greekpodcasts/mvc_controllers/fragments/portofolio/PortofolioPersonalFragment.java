package gr.kalymnos.sk3m3l10.greekpodcasts.mvc_controllers.fragments.portofolio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.DataRepository;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_model.StaticFakeDataRepo;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal.PortofolioPersonalViewMvc;
import gr.kalymnos.sk3m3l10.greekpodcasts.mvc_views.portofolio_screen.personal.PortofolioPersonalViewMvcImpl;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcast;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.Podcaster;
import gr.kalymnos.sk3m3l10.greekpodcasts.pojos.PromotionLink;
import gr.kalymnos.sk3m3l10.greekpodcasts.utils.FileUtils;

public class PortofolioPersonalFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object>,
        ChangeSaver, PortofolioPersonalViewMvc.OnViewsClickListener {

    private static final int PROMOTION_LOADER_ID = 100;
    private static final int PODCASTER_LOADER_ID = 200;
    private static final String TAG = PortofolioPersonalFragment.class.getSimpleName();
    private static final int RC_POSTER_PIC = 1313;

    private List<PromotionLink> cachedPromotionLinks;
    private Podcaster cachedPodcaster;
    private Uri cachedPosterUri;

    private PortofolioPersonalViewMvc viewMvc;

    private InsertTextDialogFragment nameDialog, statementDialog;
    private InsertTextDialogFragment.OnInsertedTextListener
            nameInsertedListener = text -> viewMvc.bindPodcasterName(text),
            statementInsertedListener = text -> viewMvc.bindPersonalStatement(text);
    private PromotionLinkDialogFragment promotionDialog;
    private PromotionLinkDialogFragment.OnInsertedTextListener promotionInsertedTextListener =
            (title, url) -> {
                if (cachedPromotionLinks != null) {
                    boolean hasTitleAndUrl = !TextUtils.isEmpty(title) && !TextUtils.isEmpty(url);

                    if (hasTitleAndUrl) {
                        //  Update Ui
                        PromotionLink newPromotionLink = new PromotionLink(title, url, cachedPodcaster.getFirebasePushId());
                        cachedPromotionLinks.add(newPromotionLink);
                        viewMvc.bindPromotionLinks(cachedPromotionLinks);

                        //  TODO: The new promotion link must be uploaded
                    } else {
                        Toast.makeText(getContext(), viewMvc.getNoTitleOrUrlMessageId(), Toast.LENGTH_SHORT).show();
                    }
                }
            };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMvc = new PortofolioPersonalViewMvcImpl(inflater, container);
        viewMvc.setOnViewsClickListener(this);
        return viewMvc.getRootView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        boolean isValidState = savedInstanceState != null
                && savedInstanceState.containsKey(PromotionLink.PROMOTION_LINKS_KEY)
                && savedInstanceState.containsKey(Podcaster.PODCASTER_KEY);

        if (isValidState) {
            cachedPodcaster = savedInstanceState.getParcelable(Podcaster.PODCASTER_KEY);
            cachedPromotionLinks = savedInstanceState.getParcelableArrayList(PromotionLink.PROMOTION_LINKS_KEY);
            cachedPosterUri = savedInstanceState.getParcelable(Podcast.POSTER_KEY);
        }

        getLoaderManager().restartLoader(PROMOTION_LOADER_ID, null, this);
        getLoaderManager().restartLoader(PODCASTER_LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (cachedPosterUri != null) {
            String fileName = FileUtils.fileName(getContext().getContentResolver(), cachedPosterUri);
            viewMvc.bindImage(cachedPosterUri);
            viewMvc.bindImageFileName(fileName);
            viewMvc.displayImageHint(false);
            viewMvc.displayImageFileName(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_POSTER_PIC) {
            boolean imageFileFetched = resultCode == getActivity().RESULT_OK && data != null;
            if (imageFileFetched) {
                cachedPosterUri = data.getData();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(PromotionLink.PROMOTION_LINKS_KEY, (ArrayList<? extends Parcelable>) cachedPromotionLinks);
        outState.putParcelable(Podcaster.PODCASTER_KEY, cachedPodcaster);
        if (cachedPosterUri != null) {
            outState.putParcelable(Podcast.POSTER_KEY, cachedPosterUri);
        }
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case PROMOTION_LOADER_ID:
                return new AsyncTaskLoader<Object>(getContext()) {

                    @Override
                    protected void onStartLoading() {
                        if (cachedPromotionLinks != null) {
                            deliverResult(cachedPromotionLinks);
                        } else {
                            viewMvc.displayLoadingIndicator(true);
                            forceLoad();
                        }
                    }

                    @Nullable
                    @Override
                    public Object loadInBackground() {
                        //  TODO: Replace with real service.
                        DataRepository repo = new StaticFakeDataRepo();
                        return repo.fetchPromotionLinks(repo.getCurrentUserUid());
                    }
                };

            case PODCASTER_LOADER_ID:
                return new AsyncTaskLoader<Object>(getContext()) {

                    @Override
                    protected void onStartLoading() {
                        if (cachedPodcaster != null) {
                            deliverResult(cachedPodcaster);
                        } else {
                            forceLoad();
                        }
                    }

                    @Nullable
                    @Override
                    public Object loadInBackground() {
                        //  TODO: Replace with a real service
                        DataRepository repo = new StaticFakeDataRepo();
                        return repo.fetchPodcaster(repo.getCurrentUserUid());
                    }
                };

            default:
                throw new UnsupportedOperationException(TAG + ": unknown loader id.");
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        if (data != null) {
            switch (loader.getId()) {
                case PROMOTION_LOADER_ID:
                    List<PromotionLink> list = (List<PromotionLink>) data;
                    if (list != null && list.size() > 0) {
                        viewMvc.displayLoadingIndicator(false);
                        viewMvc.bindPromotionLinks(cachedPromotionLinks = list);
                    }
                    break;

                case PODCASTER_LOADER_ID:
                    if (data instanceof Podcaster) {
                        cachedPodcaster = (Podcaster) data;
                        viewMvc.bindPodcasterName(cachedPodcaster.getUsername());
                        if (cachedPosterUri == null) {
                            viewMvc.bindImage(cachedPodcaster.getImageUrl());
                        }
                        viewMvc.bindPersonalStatement(cachedPodcaster.getPersonalStatement());
                    }
                    break;

                default:
                    throw new UnsupportedOperationException(TAG + ": unknown loader id.");
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    @Override
    public void save() {
        boolean nameIsValid = !TextUtils.isEmpty(viewMvc.getPodcasterName());
        boolean pictureIsValid = viewMvc.pictureExists() && cachedPosterUri != null;
        boolean statementIsValid = !TextUtils.isEmpty(viewMvc.getPersonalStatement());

        if (nameIsValid && pictureIsValid && statementIsValid){
            //  TODO: Upload the image and when finished create the podcaster and upload it
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public boolean isValidStateToSave() {
        return false;
    }

    @Override
    public void onEditPodcasterName() {
        if (nameDialog == null) {
            nameDialog = createDialog(viewMvc.getNameDialogTitleRes());
            nameDialog.setOnInsertedTextListener(nameInsertedListener);
        }
        nameDialog.show(getFragmentManager(), getDialogFragmentTag(viewMvc.getNameDialogTitleRes()));
    }

    @Override
    public void onEditPersonalStatementClick() {
        if (statementDialog == null) {
            statementDialog = createDialog(viewMvc.getPersonalStatementDialogTitleRes());
            statementDialog.setOnInsertedTextListener(statementInsertedListener);
        }
        statementDialog.show(getFragmentManager(), getDialogFragmentTag(viewMvc.getPersonalStatementDialogTitleRes()));
    }

    @Override
    public void onEditPromotionLinkClick() {
        if (promotionDialog == null) {
            promotionDialog = new PromotionLinkDialogFragment();
            promotionDialog.setOnInsertedTextListener(promotionInsertedTextListener);
        }
        promotionDialog.show(getFragmentManager(), getPromotionDialogFragmentTag());
    }

    @Override
    public void onImageClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, RC_POSTER_PIC);
        }
    }

    private InsertTextDialogFragment createDialog(int titleRes) {
        Bundle args = new Bundle();
        args.putInt(InsertTextDialogFragment.TITLE_KEY, titleRes);

        InsertTextDialogFragment dialogFragment = new InsertTextDialogFragment();
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    private String getDialogFragmentTag(int titleRes) {
        return InsertTextDialogFragment.TAG + String.valueOf(titleRes);
    }

    private String getPromotionDialogFragmentTag() {
        return InsertTextDialogFragment.TAG + "_promotion_dialog_tag";
    }
}
