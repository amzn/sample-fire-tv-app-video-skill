/**
 * Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv.data;

import com.amazon.alexauicontroller.Element;
import com.amazon.alexauicontroller.Scene;
import com.amazon.alexauicontroller.UIAction;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * A UIController directive that describes which specific ui action to be performed on the selected
 * UI element on the current user's screen
 */
public class ActionOnUIElement implements Serializable {

    /**
     * Scene on which the UI element to be acted upon was rendered.
     */
    @SerializedName("scene")
    @Expose
    private final Scene scene;

    /**
     * Action to take on the element.
     */
    @SerializedName("action")
    @Expose
    private final UIAction action;

    /**
     * Element to perform the action on.
     */
    @SerializedName("element")
    @Expose
    private final Element element;

    public ActionOnUIElement(final Scene scene, final UIAction action, final Element element) {
        this.scene = scene;
        this.action = action;
        this.element = element;
    }

    /**
     * @return the scene on which UI element to be acted upon
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * @return the action to take on the element
     */
    public UIAction getAction() {
        return action;
    }

    /**
     * @return the element to perform the action on.
     */
    public Element getElement() {
        return element;
    }

    @Override
    public String toString() {
        return "ActionOnUIElement{" +
                "scene=" + scene +
                ", action=" + action +
                ", element=" + element +
                '}';
    }
}
