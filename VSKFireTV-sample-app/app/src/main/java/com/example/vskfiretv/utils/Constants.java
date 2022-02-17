/**
 * Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv.utils;

public class Constants {

    // UI Controller Intent Extras
    public static final String EXTRA_UI_CONTROLLER_ELEMENT_ID = "elementId";
    public static final String EXTRA_UI_CONTROLLER_ELEMENT_TYPE = "elementType";
    public static final String EXTRA_UI_CONTROLLER_ACTION = "action";

    // UI Controller Intent Actions
    public static final String ACTION_ON_UI_ELEMENT = "com.example.vskfiretv.ACTION_ON_UI_ELEMENT";

    // MediaDetailsNavigator Intent Extras
    public static final String EXTRA_MEDIA_DETAILS_NAVIGATOR_TYPE = "type";
    public static final String EXTRA_MEDIA_DETAILS_NAVIGATOR_VALUE = "value";
    public static final String EXTRA_MEDIA_DETAILS_NAVIGATOR_ENTITY_ID = "entityId";

    // MediaDetailsNavigator Intent Actions
    public static final String ACTION_ON_MEDIA_DETAILS = "com.example.vskfiretv.ACTION_ON_MEDIA_DETAILS";

    // Launch Targets
    public static final String PLAY_SOMETHING = "play something";
    public static final String PLAY_SOMETHING_ELSE = "play something else";

    // Launch Target URIs
    public static final String URI_FOR_PLAY_SOMETHING = "uri.for.play.something";
    public static final String URI_FOR_PLAY_SOMETHING_ELSE = "uri.for.play.something.else";
}
