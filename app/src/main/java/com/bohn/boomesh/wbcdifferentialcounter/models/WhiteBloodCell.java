package com.bohn.boomesh.wbcdifferentialcounter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bohn.boomesh.wbcdifferentialcounter.R;

/**
 * Created by Sumesh on 15-05-16.
 */
public class WhiteBloodCell implements Parcelable {
    public enum WBCType {
        BASO(R.drawable.baso, R.string.Baso),
        EOSINE(R.drawable.eosine, R.string.Eosine),
        MONO(R.drawable.mono, R.string.Mono),
        LYMPHO(R.drawable.lympho, R.string.Lympho),
        NEUTRO(R.drawable.neutro, R.string.Neutro),
        ALL(0, 0);

        public final int mImageResourceId;
        public final int mStringResourceId;

        WBCType(int pImgResId, int pStrResId) {
            mImageResourceId = pImgResId;
            mStringResourceId = pStrResId;
        }
    }

    private WBCType mType;

    private int mCount;

    public WhiteBloodCell(WBCType pType) {
        mType = pType;
        mCount = 0;
    }

    public WhiteBloodCell(WBCType pType, int pCount) {
        mType = pType;
        mCount = pCount;
    }

    public WhiteBloodCell(WhiteBloodCell pOther) {
        this(pOther.getType(), pOther.getCount());
    }

    public WBCType getType() {
        return mType;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int pCount) {
        mCount = pCount;
    }


    /*
        Parcelable methods
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType == null ? -1 : this.mType.ordinal());
        dest.writeInt(this.mCount);
    }

    private WhiteBloodCell(Parcel in) {
        int tmpMType = in.readInt();
        this.mType = tmpMType == -1 ? null : WBCType.values()[tmpMType];
        this.mCount = in.readInt();
    }

    public static final Parcelable.Creator<WhiteBloodCell> CREATOR = new Parcelable.Creator<WhiteBloodCell>() {
        public WhiteBloodCell createFromParcel(Parcel source) {
            return new WhiteBloodCell(source);
        }

        public WhiteBloodCell[] newArray(int size) {
            return new WhiteBloodCell[size];
        }
    };
}