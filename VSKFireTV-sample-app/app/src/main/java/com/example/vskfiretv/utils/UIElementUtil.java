/**
 * Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.leanback.widget.Action;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ObjectAdapter;

import com.amazon.alexauicontroller.Element;
import com.amazon.alexauicontroller.ElementWithChildren;
import com.amazon.alexauicontroller.Entity;
import com.amazon.alexauicontroller.EntityName;
import com.amazon.alexauicontroller.EntityType;
import com.amazon.alexauicontroller.UIAction;
import com.amazon.alexauicontroller.stateproperties.FocusedUIElement;
import com.amazon.alexauicontroller.stateproperties.UIElements;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.example.vskfiretv.Movie;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.vskfiretv.utils.Constants.EXTRA_MEDIA_DETAILS_NAVIGATOR_ENTITY_ID;
import static com.example.vskfiretv.utils.Constants.EXTRA_MEDIA_DETAILS_NAVIGATOR_TYPE;
import static com.example.vskfiretv.utils.Constants.EXTRA_MEDIA_DETAILS_NAVIGATOR_VALUE;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ACTION;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ELEMENT_ID;
import static com.example.vskfiretv.utils.Constants.EXTRA_UI_CONTROLLER_ELEMENT_TYPE;

/**
 * Utility class that builds all the UI elements required for generating UI state event payload which
 * is reported to Alexa
 */
public class UIElementUtil {
    private static final String TAG = "UIElementUtil";

    public static final String EXTERNAL_ID_KEY = "entityId";
    public static final String EXTERNAL_ID_VALUE_PREFIX_FOR_ACTION = "webPageButton-";
    public static final String EXTERNAL_ID_VALUE_PREFIX_FOR_MOVIE = "video-";
    public static final String EXTERNAL_ID_VALUE_PREFIX_FOR_LIST_ROW = "videos-row-";
    public static final String ELEMENT_ID_PREFIX = "element-";
    public static final String ELEMENT_ID_PREFIX_FOR_ACTION = "action-";
    public static final String ELEMENT_ID_PREFIX_FOR_MOVIE = "movie-";
    public static final String ELEMENT_ID_PREFIX_FOR_VIDEO_DETAIL = "details-";
    public static final String ELEMENT_ID_PREFIX_FOR_LIST_ROW = "list-row-";
    public static final String UI_ELEMENTS_EVENT_KEY = "uiElements";
    public static final String FOCUSED_UI_ELEMENT_EVENT_KEY = "focusedUIElement";

    /**
     * Builds an {@link ElementWithChildren} object for a given details overview row
     * @param detailsOverviewRow the {@link DetailsOverviewRow} which contains details about a movie/video
     * @return {@link ElementWithChildren}
     */
    public static ElementWithChildren getUIElement(final DetailsOverviewRow detailsOverviewRow) {
        final ObjectAdapter actionsAdapter = detailsOverviewRow.getActionsAdapter();

        final List<ElementWithChildren> childElements = new ArrayList<>();
        for (int j = 0; j < actionsAdapter.size(); j++) {
            final Action action = (Action) actionsAdapter.get(j);

            final Entity actionEntity = getEntity(action);
            final ElementWithChildren element = new ElementWithChildren.Builder()
                    .withElementId(ELEMENT_ID_PREFIX+ELEMENT_ID_PREFIX_FOR_ACTION+action.getId())
                    .withEntity(actionEntity)
                    .withOrdinal((int)action.getId())
                    .addUISupportedAction(UIAction.SELECT).build();
            childElements.add(element);
        }

        final Movie currentMovieDetails = (Movie) detailsOverviewRow.getItem();
        final Entity currentMovieEntity = getEntity(currentMovieDetails);

        final ElementWithChildren currentMovieElement = new ElementWithChildren.Builder()
                .withElementId(ELEMENT_ID_PREFIX+ ELEMENT_ID_PREFIX_FOR_VIDEO_DETAIL +detailsOverviewRow.getId())
                .withEntity(currentMovieEntity)
                .addUISupportedAction(UIAction.SCROLL_DOWN)
                .withElements(childElements).build();

        return currentMovieElement;
    }

    /**
     * Builds an {@link ElementWithChildren} object for a list row
     * @param listRow the {@link ListRow} which contains a list of movies/videos
     * @return the {@link ElementWithChildren}
     */
    public static ElementWithChildren getUIElement(final ListRow listRow) {
        final ObjectAdapter listRowAdapter = listRow.getAdapter();

        final List<ElementWithChildren> childElements = new ArrayList<>();
        for (int j = 0; j < listRowAdapter.size(); j++) {
            final Movie movie = (Movie) listRowAdapter.get(j);

            final Entity movieEntity = getEntity(movie);
            final ElementWithChildren element = new ElementWithChildren.Builder()
                    .withElementId(ELEMENT_ID_PREFIX+ELEMENT_ID_PREFIX_FOR_MOVIE+movie.getId())
                    .withEntity(movieEntity)
                    .withOrdinal(movie.getOrdinal())
                    .addUISupportedAction(UIAction.SELECT)
                    .build();
            childElements.add(element);
        }

        final Entity listRowEntity = getEntity(listRow);
        final ElementWithChildren listRowElement = new ElementWithChildren.Builder()
                .withElementId(ELEMENT_ID_PREFIX+ ELEMENT_ID_PREFIX_FOR_LIST_ROW +listRow.getId())
                .withEntity(listRowEntity)
                .withOrdinal((int)listRow.getId())
                .addUISupportedAction(UIAction.SELECT)
                .addUISupportedAction(UIAction.EXPAND)
                .withElements(childElements).build();

        return listRowElement;
    }

    /**
     * Builds an {@link Element} object for a given {@link Action} item which is defined in video details screen
     * @param action the {@link Action} which contains one or more lines of text
     * @return the {@link Element}
     */
    public static Element getUIElement(final Action action) {
        final Entity actionEntity = getEntity(action);
        return new Element.Builder()
                .withElementId(ELEMENT_ID_PREFIX+ELEMENT_ID_PREFIX_FOR_ACTION+action.getId())
                .withEntity(actionEntity)
                .withOrdinal((int) action.getId())
                .addUISupportedAction(UIAction.SELECT)
                .build();
    }

    /**
     * Builds an {@link Element} object for a given {@link Movie} item
     * @param movie the {@link Movie} object which contains media details
     * @return the {@link Element}
     */
    public static Element getUIElement(final Movie movie) {
        final Entity movieEntity = getEntity(movie);
        return new Element.Builder()
                .withElementId(ELEMENT_ID_PREFIX+ELEMENT_ID_PREFIX_FOR_MOVIE+movie.getId())
                .withEntity(movieEntity)
                .withOrdinal(movie.getOrdinal())
                .addUISupportedAction(UIAction.SELECT)
                .build();
    }

    /**
     * Builds an {@link Entity} object for a given ui element which can be {@link Action}, {@link Movie} or {@link ListRow}
     * @param object the ui element for which entity needs to be built
     * @return the {@link Entity}
     */
    private static Entity getEntity(final Object object) {
        final EntityName entityName = new EntityName.Builder().withValue(getVoiceFriendlyTitle(object))
                .build();

        final EntityType entityType = getEntityType(object);
        final Map<String, String> externalIds = getExternalIds(object);

        return new Entity.Builder()
                .withName(entityName)
                .withType(entityType)
                .withExternalIds(externalIds)
                .build();
    }

    /**
     * Builds a map of externalIds for a given ui element which can be {@link Action}, {@link Movie} or {@link ListRow}
     * @param object the ui element for which externalId map needs to be built
     * @return the map of externalIds
     */
    private static Map<String, String> getExternalIds(final Object object) {
        final Map<String, String> externalIds = new HashMap<>();

        if (object == null) return externalIds;

        if (object instanceof Action) {
            externalIds.put(EXTERNAL_ID_KEY, EXTERNAL_ID_VALUE_PREFIX_FOR_ACTION + ((Action) object).getId());
        } else if (object instanceof Movie) {
            externalIds.put(EXTERNAL_ID_KEY, EXTERNAL_ID_VALUE_PREFIX_FOR_MOVIE + ((Movie) object).getId());
        } else if (object instanceof ListRow) {
            externalIds.put(EXTERNAL_ID_KEY, EXTERNAL_ID_VALUE_PREFIX_FOR_LIST_ROW + ((ListRow) object).getId());
        }
        return externalIds;
    }

    /**
     * Returns the entity type for a given ui element which can be {@link Action}, {@link Movie} or {@link ListRow}
     * @param object the ui element for which entity type is returned
     * @return the {@link EntityType}
     */
    private static EntityType getEntityType(final Object object) {
        if (object == null) return null;

        if (object instanceof ListRow) {
            return EntityType.AMAZON_ITEM_LIST;
        } else if (object instanceof Movie) {
            return EntityType.AMAZON_VIDEO_OBJECT;
        }
        return EntityType.AMAZON_THING;
    }

    /**
     * Returns a voice friendly name for a given ui element which can be {@link Action}, {@link Movie} or {@link ListRow}
     * @param object the ui element for which voice friendly name is returned
     * @return the voice friendly name
     */
    private static String getVoiceFriendlyTitle(final Object object) {
        if (object == null) return null;

        if (object instanceof Action) {
            final Action action = (Action) object;
            if (action.getLabel1() == null) return null;
            final String[] actionTokens = action.getLabel1().toString().split("\\.");
            return actionTokens.length > 1 ? actionTokens[1].trim() : action.getLabel1().toString();
        } else if (object instanceof Movie) {
            final Movie movie = (Movie) object;
            return  movie.getTitle();
        } else if (object instanceof ListRow) {
            final ListRow listRow = (ListRow) object;
            return listRow.getHeaderItem() != null ? listRow.getHeaderItem().getName() : null;
        }
        return null;
    }

    /**
     * Generates a ui state json payload for ui elements present on the current ui screen
     * @param uiElements the ui elements present on the current ui screen
     * @param focusedUIElement the ui element which is focused on the current ui screen
     * @return
     */
    public static Map<String, String> getUIStateJSON(final UIElements uiElements, final FocusedUIElement focusedUIElement) {
        final Map<String, String> uiState = new HashMap<>();
        String uiElementsJSON = null;
        String focusedUIElementJson = null;

        final ObjectMapper jsonMapper = getJsonObjectMapper();
        try {
            uiElementsJSON = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(uiElements);
            focusedUIElementJson = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(focusedUIElement);
        } catch (final IOException e) {
            Log.e(TAG, MessageFormat.format("Error while serializing the UI State with uiElements = {0}," +
                    " focusedUIElements = {1}", uiElements, focusedUIElement), e);
        }
        uiState.put(UI_ELEMENTS_EVENT_KEY, uiElementsJSON);
        uiState.put(FOCUSED_UI_ELEMENT_EVENT_KEY, focusedUIElementJson);
        return uiState;
    }

    /**
     * Generates a Bundle from the UIController provided intent.
     * @param intent the intent for the UIController action.
     * @return the Bundle containing information from the UIController intent.
     */
    public static Bundle getUIControllerBundleFromIntent(final Intent intent) {
        final String elementId = (String) intent.getSerializableExtra(EXTRA_UI_CONTROLLER_ELEMENT_ID);
        final EntityType entityType = (EntityType) intent.getSerializableExtra(EXTRA_UI_CONTROLLER_ELEMENT_TYPE);
        final UIAction uiAction = (UIAction) intent.getSerializableExtra(EXTRA_UI_CONTROLLER_ACTION);

        final Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_UI_CONTROLLER_ELEMENT_ID, elementId);
        bundle.putSerializable(EXTRA_UI_CONTROLLER_ELEMENT_TYPE, entityType);
        bundle.putSerializable(EXTRA_UI_CONTROLLER_ACTION, uiAction);
        return bundle;
    }

    /**
     * Generates a Bundle from the MediaDetailsNavigator provided intent.
     * @param intent the intent for the MediaDetailsNavigator action.
     * @return the Bundle containing information from the MediaDetailsNavigator intent.
     */
    public static Bundle getMediaDetailsNavigatorBundleFromIntent(final Intent intent) {
        final String type = (String) intent.getSerializableExtra(EXTRA_MEDIA_DETAILS_NAVIGATOR_TYPE);
        final String value = (String) intent.getSerializableExtra(EXTRA_MEDIA_DETAILS_NAVIGATOR_VALUE);
        String entityId = (String) intent.getSerializableExtra(EXTRA_MEDIA_DETAILS_NAVIGATOR_ENTITY_ID);

        final Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_MEDIA_DETAILS_NAVIGATOR_TYPE, type);
        bundle.putSerializable(EXTRA_MEDIA_DETAILS_NAVIGATOR_VALUE, value);
        bundle.putSerializable(EXTRA_MEDIA_DETAILS_NAVIGATOR_ENTITY_ID, entityId);
        return bundle;
    }

    /**
     * @return the {@link ObjectMapper} for serializing/de-serializing ui elements
     */
    private static ObjectMapper getJsonObjectMapper() {
        final ObjectMapper jsonObjectMapper = new ObjectMapper();
        jsonObjectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        jsonObjectMapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
        jsonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return jsonObjectMapper;
    }

    /**
     * @return the {@link Gson} for serializing/de-serializing directive data
     */
    public static Gson getGson() {
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(EntityType.class, new EntityTypeDeserializer())
                .registerTypeAdapter(EntityType.class, new EntityTypeSerializer())
                .create();
        return gson;
    }

    /**
     * Custom de-serializer for {@link EntityType} enum
     */
    static class EntityTypeDeserializer implements JsonDeserializer<EntityType> {

        @Override
        public EntityType deserialize(final JsonElement json, final Type typeOfT,
                                      final JsonDeserializationContext context) throws JsonParseException {
            final String serialized = json.getAsString();
            return EntityType.fromValue(serialized);
        }
    }

    /**
     * Custom serializer for {@link EntityType} enum
     */
    static class EntityTypeSerializer implements JsonSerializer<EntityType> {

        @Override
        public JsonElement serialize(final EntityType src, final Type typeOfSrc, final JsonSerializationContext context) {
            final JsonElement jsonElement = new JsonPrimitive(src.getValue());
            return jsonElement;
        }
    }

}
