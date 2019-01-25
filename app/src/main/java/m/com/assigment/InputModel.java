package m.com.assigment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class InputModel implements Parcelable {
    @SerializedName("data")
    @Expose
    private List<Fields> data = null;

    public List<Fields> getData() {
        return data;
    }

    public void setData(List<Fields> data) {
        this.data = data;
    }

    protected InputModel(Parcel in) {
        if (in.readByte() == 0x01) {
            data = new ArrayList<Fields>();
            in.readList(data, Fields.class.getClassLoader());
        } else {
            data = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (data == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(data);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<InputModel> CREATOR = new Parcelable.Creator<InputModel>() {
        @Override
        public InputModel createFromParcel(Parcel in) {
            return new InputModel(in);
        }

        @Override
        public InputModel[] newArray(int size) {
            return new InputModel[size];
        }
    };
}