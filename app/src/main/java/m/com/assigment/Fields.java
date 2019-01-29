package m.com.assigment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Fields implements Parcelable {

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @SerializedName("field-name")
    @Expose
    private String fieldName;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("options")
    @Expose
    private List<String> options = null;

    @SerializedName("min")
    @Expose
    private int min=0;


    @SerializedName("max")
    @Expose

    private int max=0;


    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @SerializedName("required")
    @Expose
    private boolean required=false;



    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<Fields> getFields() {
        return fields;
    }

    public void setFields(List<Fields> fields) {
        this.fields = fields;
    }

    @SerializedName("fields")
    @Expose
    private List<Fields> fields=new ArrayList<>();



    protected Fields(Parcel in) {
        fieldName = in.readString();
        type = in.readString();
        if (in.readByte() == 0x01) {
            options = new ArrayList<String>();
            in.readList(options, String.class.getClassLoader());
        } else {
            options = null;
        }
        min = in.readInt();
        max = in.readInt();
        required = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            fields = new ArrayList<Fields>();
            in.readList(fields, Fields.class.getClassLoader());
        } else {
            fields = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fieldName);
        dest.writeString(type);
        if (options == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(options);
        }
        dest.writeInt(min);
        dest.writeInt(max);
        dest.writeByte((byte) (required ? 0x01 : 0x00));
        if (fields == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(fields);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Fields> CREATOR = new Parcelable.Creator<Fields>() {
        @Override
        public Fields createFromParcel(Parcel in) {
            return new Fields(in);
        }

        @Override
        public Fields[] newArray(int size) {
            return new Fields[size];
        }
    };
}