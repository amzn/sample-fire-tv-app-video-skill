/**
 * Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * A MediaDetails directive that contains details about a specific ui entity that was targeted by
 * the user
 */
public class MediaDetailsElement {
    /**
     * Entity whose details are shown to the user
     */
    @SerializedName("entity")
    @Expose
    private final Entity entity;

    public MediaDetailsElement(final Entity entity) {
        this.entity = entity;
    }

    /**
     * @return the entity whose details are to be displayed to the user
     */
    public Entity getEntity() {
        return entity;
    }


    @Override
    public String toString() {
        return "MediaDetailsElement{" +
                "entity=" + entity +
                '}';
    }
}
