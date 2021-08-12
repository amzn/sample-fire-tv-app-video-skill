/**
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: LicenseRef-.amazon.com.-AmznSL-1.0
 * Licensed under the Amazon Software License  http://aws.amazon.com/asl/
 */

package com.example.vskfiretv.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Cookie implements Parcelable
{

    public final static Creator<Cookie> CREATOR = new Creator<Cookie>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Cookie createFromParcel(Parcel in) {
            return new Cookie(in);
        }

        public Cookie[] newArray(int size) {
            return (new Cookie[size]);
        }

    }
    ;

    protected Cookie(Parcel in) {
    }

    public Cookie() {
    }

    public void writeToParcel(Parcel dest, int flags) {
    }

    public int describeContents() {
        return  0;
    }

}
