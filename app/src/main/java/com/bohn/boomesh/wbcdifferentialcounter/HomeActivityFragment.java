package com.bohn.boomesh.wbcdifferentialcounter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivityFragment extends Fragment implements HomeActivity.OptionsItemSelectedListener {

    public enum WBC {
        BASO(R.drawable.baso, R.string.Baso),
        EOSINE(R.drawable.eosine, R.string.Eosine),
        MONO(R.drawable.mono, R.string.Mono),
        LYMPHO(R.drawable.lympho, R.string.Lympho),
        NEUTRO(R.drawable.neutro, R.string.Neutro),
        ALL(0, 0);

        public int mImageResourceId;
        public int mStringResourceId;

        WBC(int pImgResId, int pStrResId) {
            mImageResourceId = pImgResId;
            mStringResourceId = pStrResId;
        }
    }

    private final String KEY_BASO_COUNT = "BASO_COUNT";
    private final String KEY_EOS_COUNT = "EOS_COUNT";
    private final String KEY_MONO_COUNT = "MONO_COUNT";
    private final String KEY_LYMPHO_COUNT = "LYMPHO_COUNT";
    private final String KEY_NEUTRO_COUNT = "NEUTRO_COUNT";

    private final int TOTAL_WBC_COUNT = 100;

    private TextView mTotalCountTxt;
    private View mBasoCellView;
    private View mEosineCellView;
    private View mMonoCellView;
    private View mLymphoCellView;
    private View mNeutroCellView;

    private int mBasoCount;
    private int mEosCount;
    private int mMonoCount;
    private int mLymphoCount;
    private int mNeutroCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (savedInstanceState != null) {
            mBasoCount = savedInstanceState.getInt(KEY_BASO_COUNT, 0);
            mEosCount = savedInstanceState.getInt(KEY_EOS_COUNT, 0);
            mMonoCount = savedInstanceState.getInt(KEY_MONO_COUNT, 0);
            mLymphoCount = savedInstanceState.getInt(KEY_LYMPHO_COUNT, 0);
            mNeutroCount = savedInstanceState.getInt(KEY_NEUTRO_COUNT, 0);
        }

        if (view.findViewById(R.id.dummy_text) == null) {
            mTotalCountTxt = (TextView) view.findViewById(R.id.total_count_textview);

            mBasoCellView = view.findViewById(R.id.baso_cell);
            setupCellView(mBasoCellView, WBC.BASO, mBasoCount);

            mEosineCellView = view.findViewById(R.id.eosino_cell);
            setupCellView(mEosineCellView, WBC.EOSINE, mEosCount);

            mMonoCellView = view.findViewById(R.id.mono_cell);
            setupCellView(mMonoCellView, WBC.MONO, mMonoCount);

            mLymphoCellView = view.findViewById(R.id.lympho_cell);
            setupCellView(mLymphoCellView, WBC.LYMPHO, mLymphoCount);

            mNeutroCellView = view.findViewById(R.id.neutro_cell);
            setupCellView(mNeutroCellView, WBC.NEUTRO, mNeutroCount);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_BASO_COUNT, mBasoCount);
        outState.putInt(KEY_EOS_COUNT, mEosCount);
        outState.putInt(KEY_MONO_COUNT, mMonoCount);
        outState.putInt(KEY_LYMPHO_COUNT, mLymphoCount);
        outState.putInt(KEY_NEUTRO_COUNT, mNeutroCount);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof HomeActivity) {
            ((HomeActivity) activity).setOptionsMenuListener(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FragmentActivity activity = getActivity();
        if (activity instanceof HomeActivity) {
            ((HomeActivity) activity).setOptionsMenuListener(null);
        }
    }

    private void setupCellView(@NonNull final View pView, final WBC pCellType, int cellCount) {
        final TextView cellCountTextView = (TextView) pView.findViewById(R.id.cell_count_text_view);
        cellCountTextView.setText(getFormattedCellCount(cellCount));

        final ImageView cellImage = (ImageView) pView.findViewById(R.id.cell_img);
        cellImage.setBackgroundResource(pCellType.mImageResourceId);
        pView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCellClicked(pCellType, cellCountTextView);
            }
        });

        ((TextView) pView.findViewById(R.id.cell_title)).setText(pCellType.mStringResourceId);
    }

    private String getFormattedCellCount(int cellCount) {
        return String.format("%03d", cellCount);
    }

    private void onCellClicked(WBC pCellType, TextView pCellCountEditText) {
        int cellCount;
        switch (pCellType) {
            case BASO:
                cellCount = ++mBasoCount;
                break;
            case EOSINE:
                cellCount = ++mEosCount;
                break;
            case MONO:
                cellCount = ++mMonoCount;
                break;
            case LYMPHO:
                cellCount = ++mLymphoCount;
                break;
            case NEUTRO:
                cellCount = ++mNeutroCount;
                break;
            default:
                throw new IllegalArgumentException("invalid cell type");
        }
        pCellCountEditText.setText(getFormattedCellCount(cellCount));
        updateTotalCount();
    }

    private void updateTotalCount() {
        final int totalCount = mBasoCount + mEosCount + mMonoCount + mLymphoCount + mNeutroCount;
        final boolean isMaximumReached = totalCount == TOTAL_WBC_COUNT;

        mTotalCountTxt.setText(String.format(getString(R.string.total_count_format), isMaximumReached ? TOTAL_WBC_COUNT : totalCount));
        mBasoCellView.setEnabled(!isMaximumReached);
        mEosineCellView.setEnabled(!isMaximumReached);
        mMonoCellView.setEnabled(!isMaximumReached);
        mLymphoCellView.setEnabled(!isMaximumReached);
        mNeutroCellView.setEnabled(!isMaximumReached);
    }

    @Override
    public void onOptionsItemSelected(WBC pCellType) {
        switch (pCellType) {
            case BASO:
                mBasoCount = 0;
                ((TextView) mBasoCellView.findViewById(R.id.cell_count_text_view)).setText(getFormattedCellCount(mBasoCount));
                break;
            case EOSINE:
                mEosCount = 0;
                ((TextView) mEosineCellView.findViewById(R.id.cell_count_text_view)).setText(getFormattedCellCount(mEosCount));
                break;
            case MONO:
                mMonoCount = 0;
                ((TextView) mMonoCellView.findViewById(R.id.cell_count_text_view)).setText(getFormattedCellCount(mMonoCount));
                break;
            case LYMPHO:
                mLymphoCount = 0;
                ((TextView) mLymphoCellView.findViewById(R.id.cell_count_text_view)).setText(getFormattedCellCount(mLymphoCount));
                break;
            case NEUTRO:
                mNeutroCount = 0;
                ((TextView) mNeutroCellView.findViewById(R.id.cell_count_text_view)).setText(getFormattedCellCount(mNeutroCount));
                break;
            case ALL:
                mBasoCount = 0;
                ((TextView) mBasoCellView.findViewById(R.id.cell_count_text_view)).setText(getFormattedCellCount(mBasoCount));
                mEosCount = 0;
                ((TextView) mEosineCellView.findViewById(R.id.cell_count_text_view)).setText(getFormattedCellCount(mEosCount));
                mMonoCount = 0;
                ((TextView) mMonoCellView.findViewById(R.id.cell_count_text_view)).setText(getFormattedCellCount(mMonoCount));
                mLymphoCount = 0;
                ((TextView) mLymphoCellView.findViewById(R.id.cell_count_text_view)).setText(getFormattedCellCount(mLymphoCount));
                mNeutroCount = 0;
                ((TextView) mNeutroCellView.findViewById(R.id.cell_count_text_view)).setText(getFormattedCellCount(mNeutroCount));
                break;
            default:
                throw new IllegalArgumentException("invalid cell reset from options menu");
        }
        updateTotalCount();
    }
}
