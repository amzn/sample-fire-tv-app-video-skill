/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.leanback.app.DetailsFragment;
import androidx.leanback.app.DetailsFragmentBackgroundController;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnActionClickedListener;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.widget.Toast;

import com.amazon.alexa.vsk.clientlib.AlexaClientManager;
import com.amazon.alexauicontroller.Element;
import com.amazon.alexauicontroller.ElementWithChildren;
import com.amazon.alexauicontroller.EntityType;
import com.amazon.alexauicontroller.Scene;
import com.amazon.alexauicontroller.UIAction;
import com.amazon.alexauicontroller.stateproperties.FocusedUIElement;
import com.amazon.alexauicontroller.stateproperties.UIElements;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.vskfiretv.utils.UIElementUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.vskfiretv.utils.Constants.EXTRA_MEDIA_DETAILS_NAVIGATOR_ENTITY_ID;
import static com.example.vskfiretv.utils.Constants.EXTRA_MEDIA_DETAILS_NAVIGATOR_TYPE;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ACTION;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ELEMENT_ID;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ELEMENT_TYPE;
import static com.example.vskfiretv.utils.UIElementUtil.getUIElement;
import static com.example.vskfiretv.utils.UIElementUtil.getUIStateJSON;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class VideoDetailsFragment extends DetailsFragment implements DetailsActivity.UIElementAction, DetailsActivity.MediaDetailsAction {
    public static final String VIDEO_DETAIL_SCENE_IDENTIFIER = "video-detail-scene-001";

    private static final String TAG = "VideoDetailsFragment";

    private static final int ACTION_WATCH_TRAILER = 1;
    private static final int ACTION_RENT = 2;
    private static final int ACTION_BUY = 3;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private static final int NUM_COLS = 4;

    private Movie mSelectedMovie;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private DetailsFragmentBackgroundController mDetailsBackground;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        mDetailsBackground = new DetailsFragmentBackgroundController(this);

        mSelectedMovie =
                (Movie) getActivity().getIntent().getSerializableExtra(DetailsActivity.MOVIE);
        if (mSelectedMovie != null) {
            mPresenterSelector = new ClassPresenterSelector();
            mAdapter = new ArrayObjectAdapter(mPresenterSelector);
            setupDetailsOverviewRow();
            setupDetailsOverviewRowPresenter();
            setupRelatedMovieListRow();
            setAdapter(mAdapter);
            initializeBackground(mSelectedMovie);
            setOnItemViewClickedListener(new ItemViewClickedListener());
            setOnItemViewSelectedListener(new ItemViewSelectedListener());
        } else {
            final Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        ((DetailsActivity) getActivity()).setUiElementAction(this);
        ((DetailsActivity) getActivity()).setMediaDetailsAction(this);
    }

    private void initializeBackground(final Movie data) {
        mDetailsBackground.enableParallax();
        Glide.with(getActivity())
                .load(data.getBackgroundImageUrl())
                .asBitmap()
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        mDetailsBackground.setCoverBitmap(bitmap);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });
    }

    private void setupDetailsOverviewRow() {
        Log.d(TAG, MessageFormat.format("doInBackground: {0}", mSelectedMovie.toString()));
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
        row.setImageDrawable(
                ContextCompat.getDrawable(getContext(), R.drawable.default_background));
        final int width = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        final int height = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);
        Glide.with(getActivity())
                .load(mSelectedMovie.getCardImageUrl())
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        Log.d(TAG, MessageFormat.format("details overview card image url ready: {0}", resource));
                        row.setImageDrawable(resource);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });

        final ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();

        actionAdapter.add(
                new Action(
                        ACTION_WATCH_TRAILER,
                        ACTION_WATCH_TRAILER + ". " + getResources().getString(R.string.watch_trailer_1),
                        getResources().getString(R.string.watch_trailer_2)));
        actionAdapter.add(
                new Action(
                        ACTION_RENT,
                        ACTION_RENT + ". " + getResources().getString(R.string.rent_1),
                        getResources().getString(R.string.rent_2)));
        actionAdapter.add(
                new Action(
                        ACTION_BUY,
                        ACTION_BUY + ". " + getResources().getString(R.string.buy_1),
                        getResources().getString(R.string.buy_2)));
        row.setActionsAdapter(actionAdapter);

        mAdapter.add(row);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        final FullWidthDetailsOverviewRowPresenter detailsPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.selected_background));

        // Hook up transition element.
        final FullWidthDetailsOverviewSharedElementHelper sharedElementHelper =
                new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(
                getActivity(), DetailsActivity.SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                triggerAction(action);
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void triggerAction(final Action action) {
        if (action.getId() == ACTION_WATCH_TRAILER) {
            final Intent intent = new Intent(getActivity(), PlaybackActivity.class);
            intent.putExtra(DetailsActivity.MOVIE, mSelectedMovie);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRelatedMovieListRow() {
        final String subcategories[] = {getString(R.string.related_movies)};
        final List<Movie> list = MovieList.getList();

        Collections.shuffle(list);
        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        int movieOrdinal = 0;
        for (int j = 0; j < NUM_COLS; j++) {
            final Movie movie = list.get(j % 5);
            final Movie clonedMovie = (Movie) movie.clone();
            ++movieOrdinal;
            clonedMovie.setOrdinal(movieOrdinal);
            listRowAdapter.add(clonedMovie);
        }

        final HeaderItem header = new HeaderItem(0, subcategories[0]);
        mAdapter.add(new ListRow(header, listRowAdapter));
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }

    private int convertDpToPixel(final Context context, final int dp) {
        final float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public void onUIElementAction(final Bundle bundle) {
        final String elementId = (String) bundle.getSerializable(EXTRA_UI_CONTROLLER_ELEMENT_ID);
        final EntityType entityType = (EntityType) bundle.getSerializable(EXTRA_UI_CONTROLLER_ELEMENT_TYPE);
        final UIAction uiAction = (UIAction) bundle.getSerializable(EXTRA_UI_CONTROLLER_ACTION);

        final ArrayObjectAdapter rowsAdapter = (ArrayObjectAdapter) getAdapter();
        final Object item = getItem(rowsAdapter, elementId, entityType);

        if (item == null) {
            Log.e(TAG, MessageFormat.format("No matching item for the received UI Controller elementId: {0}", elementId));
            return;
        }

        if (item instanceof Action) {
            applyUIControllerActionOnAction((Action) item, uiAction);
        } else if (item instanceof Movie) {
            applyUIControllerActionOnMovie((Movie) item, uiAction);
        } else if (item instanceof DetailsOverviewRow) {
            applyUIControllerActionOnDetailsPage(uiAction);
        }
    }

    @Override
    public void onMediaDetailsAction(final Bundle bundle) {
        final String type = (String) bundle.getSerializable(EXTRA_MEDIA_DETAILS_NAVIGATOR_TYPE);
        final String entityId = (String) bundle.getSerializable(EXTRA_MEDIA_DETAILS_NAVIGATOR_ENTITY_ID);

        // Determines which media to display based off properties of provided entity
        if ("Video".equals(type)) {
            Log.d(TAG, "TODO: Implement this according to the videos used in your app");
            // Note that the sample app only uses title matching on the value to select the media to display;
            // externalIds values are not used here.

            // Retrieves items on the screen to select media to display to the user
            final ArrayObjectAdapter rowsAdapter = (ArrayObjectAdapter) getAdapter();
            for (int i = 1; i < rowsAdapter.size(); i++) {
                final ListRow listRow = (ListRow) rowsAdapter.get(i);
                final ObjectAdapter listRowAdapter = listRow.getAdapter();
                for (int j = 0; j < listRowAdapter.size(); j++) {
                    final Movie movie = (Movie) listRowAdapter.get(j);
                    if (movie != null) {
                        final String movieExternalId = UIElementUtil.EXTERNAL_ID_VALUE_PREFIX_FOR_MOVIE + movie.getId();
                        if (Objects.equals(entityId, movieExternalId)) {
                            applyUIControllerActionOnMovie(movie, UIAction.SELECT);
                        }
                    }
                }
            }
        } else if ("App".equals(type)) {
            // Sample app does not support App functionality, implement your own logic for App case behavior
            Log.d(TAG, "TODO: Implement App case behavior");
        } else {
            Log.d(TAG, "TODO: Implement base case behavior");
        }
    }

    private void applyUIControllerActionOnDetailsPage(final UIAction uiAction) {
        switch (uiAction) {
            case SCROLL_DOWN:
                setSelectedPosition(1, true);
                break;
            default:
                Log.e(TAG, MessageFormat.format("Received uiAction type: {0} cannot be supported for detail page", uiAction));
        }
    }

    private void applyUIControllerActionOnAction(final Action action, final UIAction uiAction) {
        switch (uiAction) {
            case SELECT:
                triggerAction(action);
                break;
            default:
                Log.e(TAG, MessageFormat.format("Received uiAction type: {0} cannot be supported for action: {1}", uiAction, action.getLabel1()));
        }
    }

    private void applyUIControllerActionOnMovie(final Movie movie, final UIAction uiAction) {
        switch (uiAction) {
            case SELECT:
                selectMovie(movie,  null);
                break;
            default:
                Log.e(TAG, MessageFormat.format("Received uiAction type: {0} cannot be supported for movie: {1}", uiAction, movie.getTitle()));
        }
    }

    private void selectMovie(final Movie movie, final Presenter.ViewHolder itemViewHolder) {
        final Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.MOVIE, movie);

        Bundle bundle = null;
        if (itemViewHolder != null) {
            bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    ((ImageCardView) itemViewHolder.view).getMainImageView(),
                    DetailsActivity.SHARED_ELEMENT_NAME)
                    .toBundle();
        }
        getActivity().startActivity(intent, bundle);
    }

    private Object getItem(final ArrayObjectAdapter rowsAdapter, final String receivedElementId, final EntityType receivedElementType) {

        if (receivedElementType.equals(EntityType.AMAZON_THING)) {
            final DetailsOverviewRow detailsOverviewRow = (DetailsOverviewRow) rowsAdapter.get(0);
            final ArrayObjectAdapter actionsAdapter = (ArrayObjectAdapter) detailsOverviewRow.getActionsAdapter();
            for (int i = 0; i < actionsAdapter.size(); i++) {
                final Action action = (Action) actionsAdapter.get(i);
                final Element currentActionElement = getUIElement(action);
                if (currentActionElement.getElementId() != null &&
                        currentActionElement.getElementId().equals(receivedElementId)) {
                    return action;
                }
            }
        } else if (receivedElementType.equals(EntityType.AMAZON_VIDEO_OBJECT)) {
            final DetailsOverviewRow detailsOverviewRow = (DetailsOverviewRow) rowsAdapter.get(0);
            final Element currentDetailsOverviewElement = getUIElement(detailsOverviewRow);
            if (currentDetailsOverviewElement.getElementId() != null && currentDetailsOverviewElement.getElementId()
                    .equals(receivedElementId)) {
                return detailsOverviewRow;
            }

            for (int i = 1; i < rowsAdapter.size(); i++) {
                final ListRow listRow = (ListRow) rowsAdapter.get(i);
                final ObjectAdapter listRowAdapter = listRow.getAdapter();
                for (int j = 0; j < listRowAdapter.size(); j++) {
                    final Movie movie = (Movie) listRowAdapter.get(j);
                    final Element currentMovieElement = getUIElement(movie);
                    if (currentMovieElement.getElementId() != null && currentMovieElement.getElementId().equals(receivedElementId)) {
                        return movie;
                    }
                }
            }
        }
        return null;
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(
                final Presenter.ViewHolder itemViewHolder,
                final Object item,
                final RowPresenter.ViewHolder rowViewHolder,
                final Row row) {

            if (item instanceof Movie) {
                Log.d(TAG, MessageFormat.format("Item: {0}", item.toString()));
                final Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(getResources().getString(R.string.movie), (Movie) item);

                final Bundle bundle =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                getActivity(),
                                ((ImageCardView) itemViewHolder.view).getMainImageView(),
                                DetailsActivity.SHARED_ELEMENT_NAME)
                                .toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(final Presenter.ViewHolder itemViewHolder, final Object item,
                                   final RowPresenter.ViewHolder rowViewHolder, final Row row) {
            Log.d(TAG, MessageFormat.format("Current selected row is {0} and item is {1}", row, item));

            if (row != null) {
                Log.d(TAG, "Processing the current selected item and reporting the current UI state.");
                reportCurrentUIState(item, row);
            }
            Log.d(TAG, "Finished processing the current item");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final RowPresenter.ViewHolder selectedViewHolder = getRowsFragment().getRowViewHolder(getRowsFragment().getSelectedPosition());

        if(selectedViewHolder == null) {
            return;
        }

        final Object selectedItem = selectedViewHolder.getSelectedItem();
        final Row selectedRow = selectedViewHolder.getRow();
        Log.d(TAG, MessageFormat.format("Current selected row is {0} and item is {1}", selectedRow, selectedItem));

        if (selectedRow != null) {
            Log.d(TAG, "Processing the current selected item and reporting the current UI state.");
            reportCurrentUIState(selectedItem, selectedRow);
        }
        Log.d(TAG, "Finished processing the current item");
    }

    /**
     * Reports the ui state of the current video detail screen
     * @param selectedItem the selected item which is in focus on the current video detail screen
     * @param selectedRow the selected row which is in focus on the current video detail screen
     */
    private void reportCurrentUIState(Object selectedItem, final Row selectedRow) {
        final List<ElementWithChildren> elements = new ArrayList<>();
        Element focusedElement = null;

        if (selectedRow instanceof DetailsOverviewRow) {
            final DetailsOverviewRow detailsOverviewRow = (DetailsOverviewRow) selectedRow;
            final ElementWithChildren detailsOverviewElement = getUIElement(detailsOverviewRow);
            elements.add(detailsOverviewElement);

            if(selectedItem == null) {
                selectedItem = detailsOverviewRow.getActionsAdapter().get(0);
            }
            final Action currentActionSelected = (Action) selectedItem;
            focusedElement = getUIElement(currentActionSelected);
        } else if (selectedRow instanceof ListRow) {
            final ListRow relatedMoviesRow = (ListRow) selectedRow;
            final ElementWithChildren relatedMoviesElement = getUIElement(relatedMoviesRow);
            elements.add(relatedMoviesElement);

            if(selectedItem == null) {
                selectedItem = relatedMoviesRow.getAdapter().get(0);
            }
            final Movie currentMovieSelected = (Movie) selectedItem;
            focusedElement = getUIElement(currentMovieSelected);
        } else {
            Log.i(TAG, "Unknown row selected, cannot report the UI state to Alexa");
            return;
        }

        final Scene scene = new Scene.Builder().withSceneId(VIDEO_DETAIL_SCENE_IDENTIFIER).build();
        final UIElements uiElements = new UIElements.Builder().withScene(scene).withElements(elements).build();
        final FocusedUIElement focusedUIElement = new FocusedUIElement.Builder().withScene(scene).withElement(focusedElement).build();

        final Map<String, String> currentUIStateJSON = getUIStateJSON(uiElements, focusedUIElement);

        Log.d(TAG, MessageFormat.format("Reporting current video detail screen UI State to Alexa: {0}", currentUIStateJSON));
        AlexaClientManager.getSharedInstance().setUIState(currentUIStateJSON);
        Log.i(TAG, "Finished reporting video detail screen UI State to Alexa.");
    }

}
