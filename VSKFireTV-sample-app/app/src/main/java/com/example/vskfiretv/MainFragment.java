/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.example.vskfiretv.utils.UIElementUtil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.vskfiretv.utils.Constants.EXTRA_MEDIA_DETAILS_NAVIGATOR_ENTITY_ID;
import static com.example.vskfiretv.utils.Constants.EXTRA_MEDIA_DETAILS_NAVIGATOR_TYPE;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ACTION;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ELEMENT_ID;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ELEMENT_TYPE;
import static com.example.vskfiretv.utils.UIElementUtil.getUIElement;
import static com.example.vskfiretv.utils.UIElementUtil.getUIStateJSON;

public class MainFragment extends BrowseFragment implements MainActivity.UIElementAction, MainActivity.MediaDetailsAction {
    public static final String HOME_BROWSER_SCENE_IDENTIFIER = "home-browser-scene-000";

    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int NUM_ROWS = 2;
    private static final int NUM_COLS = 4;

    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        loadRows();

        setupEventListeners();

        ((MainActivity) getActivity()).setUiElementAction(this);
        ((MainActivity) getActivity()).setMediaDetailsAction(this);
    }

    private Object getItem(final ArrayObjectAdapter rowsAdapter, final String receivedElementId, final EntityType receivedElementType) {

        if (EntityType.AMAZON_ITEM_LIST.equals(receivedElementType)) {
            for (int i = 0; i < rowsAdapter.size(); i++) {
                final ListRow listRow = (ListRow) rowsAdapter.get(i);
                final Element currentListRowElement = getUIElement(listRow);
                if (currentListRowElement.getElementId() != null &&
                        currentListRowElement.getElementId().equals(receivedElementId)) {
                    return listRow;
                }
            }
        } else if (EntityType.AMAZON_VIDEO_OBJECT.equals(receivedElementType)) {
            for (int i = 0; i < rowsAdapter.size(); i++) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, MessageFormat.format("onDestroy: {0}", mBackgroundTimer.toString()));
            mBackgroundTimer.cancel();
        }
    }

    private void loadRows() {
        final List<Movie> list = MovieList.setupMovies();
        final ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        final CardPresenter cardPresenter = new CardPresenter();

        int movieOrdinal = 0;
        for (int i = 0; i < NUM_ROWS; i++) {
            if (i != 0) {
                Collections.shuffle(list);
            }
            final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            for (int j = 0; j < NUM_COLS; j++) {
                ++movieOrdinal;
                final Movie movie = list.get(j % 5);
                final Movie clonedMovie = (Movie) movie.clone();
                clonedMovie.setOrdinal(movieOrdinal);
                listRowAdapter.add(clonedMovie);
            }
            final HeaderItem header = new HeaderItem(i, MovieList.MOVIE_CATEGORY[i]);
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        setAdapter(rowsAdapter);
    }

    public void displaySearchResults(final String searchTerm, final JsonElement searchPayload){
        Log.d(TAG, MessageFormat.format("TODO: Show results from your catalog matching: {0}", searchTerm));
        Log.d(TAG, MessageFormat.format("Search Payload: {0}", searchPayload));

        setTitle(getString(R.string.search_results) + " " + searchTerm);

        Toast.makeText(getActivity(), "Searching for '" + searchTerm + "'", Toast.LENGTH_LONG)
                .show();

        final JsonElement jsonTree = searchPayload;
        final JsonArray jSearchArray = jsonTree.getAsJsonArray();

        final ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        final CardPresenter cardPresenter = new CardPresenter();
        final List<Movie> movieList = MovieList.getList();
        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);

        int movieOrdinal = 0;
        for (JsonElement e : jSearchArray) {

            final JsonObject jItem = e.getAsJsonObject().get("item").getAsJsonObject();
            final String sourceEntityName = jItem.get("title").getAsString();
            final String searchResultMovieID = jItem.get("movie_id").getAsString();

            for (int j = 0; j < movieList.size(); j++) {
                final Movie thisMovie = movieList.get(j);
                final String thisMovieID = thisMovie.getMovieId();
                final int var1 = searchResultMovieID.compareTo( thisMovieID );
                if (var1 == 0) {
                    ++movieOrdinal;
                    final Movie clonedMovie = (Movie) thisMovie.clone();
                    clonedMovie.setOrdinal(movieOrdinal);
                    listRowAdapter.add(clonedMovie);
                    Log.d(TAG, MessageFormat.format("Adding movie {0}", thisMovie.getTitle()));
                }
            }

            Log.d(TAG, sourceEntityName);
        }

        final HeaderItem header = new HeaderItem(3, "Search Results");
        rowsAdapter.add(new ListRow(header, listRowAdapter));
        setAdapter(rowsAdapter);

    }

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackground = ContextCompat.getDrawable(getContext(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getContext(), R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(getContext(), R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                        .show();
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private void updateBackground(final String uri) {
        final int width = mMetrics.widthPixels;
        final int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
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

            if (item instanceof Movie) {
                applyUIControllerActionOnMovie((Movie) item, uiAction);
            } else if (item instanceof ListRow) {
                applyUIControllerActionOnListRow((ListRow) item, uiAction);
            } else {
                Log.e(TAG, MessageFormat.format("Unknown item {0}, cannot process the UI Controller action", item));
            }
    }

    private void applyUIControllerActionOnMovie(final Movie movie, final UIAction uiAction) {
        switch (uiAction) {
            case SELECT:
                selectMovie(movie,  null);
                break;
            default:
                Log.e(TAG, MessageFormat.format("Received uiAction type: {0} cannot be supported for Movie: {1}", uiAction, movie.getTitle()));
        }
    }

    private void applyUIControllerActionOnListRow(final ListRow listRow, final UIAction uiAction) {
        switch (uiAction) {
            case SELECT:
                setSelectedPosition((int)listRow.getId());
                break;
            case EXPAND:
                startHeadersTransition(false);
                break;
            default:
                Log.e(TAG, MessageFormat.format("Received uiAction type: {0} cannot be supported for ListRow: {1}",
                        uiAction, listRow.getHeaderItem().getName()));
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
            for (int i = 0; i < rowsAdapter.size(); i++) {
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

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(final Presenter.ViewHolder itemViewHolder, final Object item,
                                  final RowPresenter.ViewHolder rowViewHolder, final Row row) {

            if (item instanceof Movie) {
                 selectMovie((Movie) item, itemViewHolder);
            } else if (item instanceof String) {
                if (((String) item).contains(getString(R.string.error_fragment))) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT).show();
                }
            }
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

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {
            Log.d(TAG, MessageFormat.format("Current selected item is {0}", item));
            if (item instanceof Movie) {
                mBackgroundUri = ((Movie) item).getBackgroundImageUrl();
                startBackgroundTimer();
            }

            if (item != null && row != null) {
                Log.d(TAG, "Processing the current selected item and reporting the current UI state.");
                reportCurrentUIState(item);
            }
            Log.d(TAG, "Finished processing the current item");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final RowPresenter.ViewHolder selectedViewHolder = getSelectedRowViewHolder();

        if(selectedViewHolder == null) {
            return;
        }

        final Object selectedItem = selectedViewHolder.getSelectedItem();
        Log.d(TAG, MessageFormat.format("Current selected item is {0}", selectedItem));

        if (selectedItem != null) {
            Log.d(TAG, "Processing the current selected item and reporting the current UI state.");
            reportCurrentUIState(selectedItem);
        }
        Log.d(TAG, "Finished processing the current item");
    }

    /**
     * Reports the ui state of the current home browser screen
     * @param selectedItem the selected item which is in focus on the current home browser screen
     */
    private void reportCurrentUIState(final Object selectedItem) {
        final ArrayObjectAdapter rowsAdapter = (ArrayObjectAdapter) getAdapter();
        final List<ElementWithChildren> elements = getUIElements(rowsAdapter);

        final Movie currentMovieSelected = (Movie) selectedItem;
        final Element focusedElement = getUIElement(currentMovieSelected);

        Log.d(TAG, MessageFormat.format("Focused movie element id is {0}", focusedElement.getElementId()));

        final Scene scene = new Scene.Builder().withSceneId(HOME_BROWSER_SCENE_IDENTIFIER).build();
        final UIElements uiElements = new UIElements.Builder().withScene(scene).withElements(elements).build();
        final FocusedUIElement focusedUIElement = new FocusedUIElement.Builder().withScene(scene).withElement(focusedElement).build();

        final Map<String, String> currentUIStateJSON = getUIStateJSON(uiElements, focusedUIElement);

        Log.d(TAG, MessageFormat.format("Reporting home screen UI State to Alexa: {0}", currentUIStateJSON));
        AlexaClientManager.getSharedInstance().setUIState(currentUIStateJSON);
        Log.i(TAG, "Finished reporting home screen UI State to Alexa.");
    }

    private List<ElementWithChildren> getUIElements(final ObjectAdapter rowsAdapter) {
        final List<ElementWithChildren> elements = new ArrayList<>();
        for (int i = 0; i < rowsAdapter.size(); i++) {
            final ListRow listRow = (ListRow) rowsAdapter.get(i);
            final ElementWithChildren listRowElement = getUIElement(listRow);
            elements.add(listRowElement);
        }
        return elements;
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateBackground(mBackgroundUri);
                }
            });
        }
    }

}
