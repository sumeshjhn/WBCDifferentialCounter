package com.bohn.boomesh.wbcdifferentialcounter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bohn.boomesh.wbcdifferentialcounter.R;

public class WhiteBloodCell implements Parcelable {
    public enum WBCType {
        BASO(R.drawable.baso, R.string.Baso, R.raw.btn1_fins__button5),
        EOSINE(R.drawable.eosine, R.string.Eosine, R.raw.btn2_fins__button),
        MONO(R.drawable.mono, R.string.Mono, R.raw.btn3_fins__gamemenuclick),
        LYMPHO(R.drawable.lympho, R.string.Lympho, R.raw.btn4_junggle__btn232),
        NEUTRO(R.drawable.neutro, R.string.Neutro, R.raw.btn5__greencouch__beeps5),
        ALL(0, 0, 0);

        public final int mImageResourceId;
        public final int mStringResourceId;
        public final int mClickSoundResourceId;

        WBCType(int pImgResId, int pStrResId, int pClickSoundResourceId) {
            mImageResourceId = pImgResId;
            mStringResourceId = pStrResId;
            mClickSoundResourceId = pClickSoundResourceId;
        }
    }

    private final WBCType mType;

    private int mCount;

    public WhiteBloodCell(WBCType pType) {
        mType = pType;
        mCount = 0;
    }

    private WhiteBloodCell(WBCType pType, int pCount) {
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

    public static String getFormattedCellCount(int cellCount) {
        return String.format("%03d", cellCount);
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
