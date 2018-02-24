package solutions.farsight.pixabayimagesearcher;

import android.arch.lifecycle.ViewModel;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stephen on 2/24/2018.
 */

public class ImageResult extends ViewModel implements Parcelable {
    public final Integer width;
    public final Integer height;
    public final String url;

    public ImageResult(Hit hit) {
        width = hit.getPreviewWidth();
        height = hit.getPreviewHeight();
        url = hit.getPreviewURL();
    }

    protected ImageResult(Parcel in) {
        if (in.readByte() == 0) {
            width = null;
        } else {
            width = in.readInt();
        }
        if (in.readByte() == 0) {
            height = null;
        } else {
            height = in.readInt();
        }
        url = in.readString();
    }

    public static final Creator<ImageResult> CREATOR = new Creator<ImageResult>() {
        @Override
        public ImageResult createFromParcel(Parcel in) {
            return new ImageResult(in);
        }

        @Override
        public ImageResult[] newArray(int size) {
            return new ImageResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (width == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(width);
        }
        if (height == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(height);
        }
        parcel.writeString(url);
    }
}
