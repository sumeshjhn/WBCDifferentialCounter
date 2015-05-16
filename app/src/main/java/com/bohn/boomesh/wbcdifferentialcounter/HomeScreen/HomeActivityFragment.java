package com.bohn.boomesh.wbcdifferentialcounter.HomeScreen;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bohn.boomesh.wbcdifferentialcounter.R;
import com.bohn.boomesh.wbcdifferentialcounter.models.WhiteBloodCell;
import com.bohn.boomesh.wbcdifferentialcounter.models.WhiteBloodCell.WBCType;

import java.util.ArrayList;
import java.util.Stack;

public class HomeActivityFragment extends Fragment implements HomeActivity.OptionsItemSelectedListener {

    private final String KEY_WBC_LIST = "WHITE_BLOOD_CELL_LIST";

    private TextView mTotalCountTxt;
    private View mBasoCellView;
    private View mEosineCellView;
    private View mMonoCellView;
    private View mLymphoCellView;
    private View mNeutroCellView;

    private RecyclerView mWBCCountersRecyclerView;
    private RecyclerView.Adapter mWBCCounterAdapter;

    private ArrayList<WhiteBloodCell> mListOfWBC;
    private final Stack<WhiteBloodCell> mUndoStack = new Stack<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (savedInstanceState != null) {
            mListOfWBC = savedInstanceState.getParcelableArrayList(KEY_WBC_LIST);
        } else {
            mListOfWBC = new ArrayList<WhiteBloodCell>() {
                {
                    add(new WhiteBloodCell(WBCType.BASO));
                    add(new WhiteBloodCell(WBCType.EOSINE));
                    add(new WhiteBloodCell(WBCType.MONO));
                    add(new WhiteBloodCell(WBCType.LYMPHO));
                    add(new WhiteBloodCell(WBCType.NEUTRO));
                }
            };
        }

        mTotalCountTxt = (TextView) view.findViewById(R.id.total_count_textview);
        mWBCCountersRecyclerView = (RecyclerView) view.findViewById(R.id.counter_recycler_view);

        if (mWBCCountersRecyclerView == null) {

            mBasoCellView = view.findViewById(R.id.baso_cell);
            setupCellView(mBasoCellView, WBCType.BASO);

            mEosineCellView = view.findViewById(R.id.eosino_cell);
            setupCellView(mEosineCellView, WBCType.EOSINE);

            mMonoCellView = view.findViewById(R.id.mono_cell);
            setupCellView(mMonoCellView, WBCType.MONO);

            mLymphoCellView = view.findViewById(R.id.lympho_cell);
            setupCellView(mLymphoCellView, WBCType.LYMPHO);

            mNeutroCellView = view.findViewById(R.id.neutro_cell);
            setupCellView(mNeutroCellView, WBCType.NEUTRO);
        } else {
            mWBCCounterAdapter = new WBCCounterAdapter(mListOfWBC, new WBCCounterAdapter.OnItemClickedListener() {
                @Override
                public void onItemClicked(int pPosition, View pView) {
                    if (pPosition != RecyclerView.NO_POSITION) {
                        final WBCType type = mListOfWBC.get(pPosition).getType();
                        final TextView countTextView = (TextView) pView.findViewById(R.id.cell_count_text_view);
                        onCellClicked(type, countTextView);
                    }
                }
            });

            mWBCCountersRecyclerView.setHasFixedSize(true);
            mWBCCountersRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            mWBCCountersRecyclerView.setAdapter(mWBCCounterAdapter);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_WBC_LIST, mListOfWBC);
        //TODO: save Undo stack
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

    private void setupCellView(@NonNull final View pView, final WBCType pCellType) {
        final TextView cellCountTextView = (TextView) pView.findViewById(R.id.cell_count_text_view);
        cellCountTextView.setText(WhiteBloodCell.getFormattedCellCount(getCell(pCellType).getCount()));

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

    private WhiteBloodCell getCell(WBCType pCellType) {
        WhiteBloodCell whiteBloodCell = null;
        for (WhiteBloodCell wbc : mListOfWBC) {
            if (wbc.getType() == pCellType) {
                whiteBloodCell = wbc;
                break;
            }
        }
        return whiteBloodCell;
    }

    private void onCellClicked(WBCType pCellType, TextView pCellCountEditText) {
        final WhiteBloodCell whiteBloodCell = getCell(pCellType);
        mUndoStack.push(new WhiteBloodCell(whiteBloodCell));

        int cellCount = whiteBloodCell.getCount();
        whiteBloodCell.setCount(++cellCount);
        pCellCountEditText.setText(WhiteBloodCell.getFormattedCellCount(cellCount));
        updateTotalCount();
    }

    private void updateTotalCount() {
        final int TOTAL_WBC_COUNT = 100;

        int totalCount = 0;
        for (WhiteBloodCell wbc : mListOfWBC) {
            totalCount += wbc.getCount();
        }

        final boolean isMaximumReached = totalCount == TOTAL_WBC_COUNT;
        mTotalCountTxt.setText(String.format(getString(R.string.total_count_format), isMaximumReached ? TOTAL_WBC_COUNT : totalCount));

        if (mWBCCountersRecyclerView == null) {
            mBasoCellView.setEnabled(!isMaximumReached);
            mEosineCellView.setEnabled(!isMaximumReached);
            mMonoCellView.setEnabled(!isMaximumReached);
            mLymphoCellView.setEnabled(!isMaximumReached);
            mNeutroCellView.setEnabled(!isMaximumReached);
        } else {
            mWBCCountersRecyclerView.setEnabled(!isMaximumReached);
        }
    }

    @Override
    public void onOptionsItemSelected(int pMenuItemId) {
        switch (pMenuItemId) {
            case R.id.action_reset_all:
                resetAllCounters();
                break;
            case R.id.action_undo:
                if (!mUndoStack.isEmpty()) {
                    final WhiteBloodCell prevWBC = mUndoStack.pop();
                    for (WhiteBloodCell wbc : mListOfWBC) {
                        if (wbc.getType().equals(prevWBC.getType())) {
                            wbc.setCount(prevWBC.getCount());
                        }
                        updateCounterWithCellType(wbc.getType(), wbc.getCount());
                    }

                    if (mWBCCountersRecyclerView != null) {
                        mWBCCounterAdapter.notifyDataSetChanged();
                    }
                    updateTotalCount();

                }
                break;
            default:
                throw new IllegalArgumentException("unknown menu option clicked");
        }
    }

    private void resetAllCounters() {
        mUndoStack.clear();

        for (WhiteBloodCell wbc : mListOfWBC) {
            wbc.setCount(0);
        }

        if (mWBCCountersRecyclerView == null) {
            updateCounterWithCellType(WBCType.BASO, 0);
            updateCounterWithCellType(WBCType.EOSINE, 0);
            updateCounterWithCellType(WBCType.MONO, 0);
            updateCounterWithCellType(WBCType.LYMPHO, 0);
            updateCounterWithCellType(WBCType.NEUTRO, 0);
        } else {
            mWBCCounterAdapter.notifyDataSetChanged();
        }
        updateTotalCount();
    }

    /**
     * used for in the landscape orientation
     *
     * @param pCellType
     * @param pCount
     */
    private void updateCounterWithCellType(WBCType pCellType, int pCount) {
        if (mBasoCellView == null || mEosineCellView == null || mMonoCellView == null || mLymphoCellView == null || mNeutroCellView == null) {
            return;
        }

        final String formattedCountString = WhiteBloodCell.getFormattedCellCount(pCount);
        switch (pCellType) {
            case BASO:
                ((TextView) mBasoCellView.findViewById(R.id.cell_count_text_view)).setText(formattedCountString);
                break;
            case EOSINE:
                ((TextView) mEosineCellView.findViewById(R.id.cell_count_text_view)).setText(formattedCountString);
                break;
            case MONO:
                ((TextView) mMonoCellView.findViewById(R.id.cell_count_text_view)).setText(formattedCountString);
                break;
            case LYMPHO:
                ((TextView) mLymphoCellView.findViewById(R.id.cell_count_text_view)).setText(formattedCountString);
                break;
            case NEUTRO:
                ((TextView) mNeutroCellView.findViewById(R.id.cell_count_text_view)).setText(formattedCountString);
                break;
            case ALL:
                ((TextView) mBasoCellView.findViewById(R.id.cell_count_text_view)).setText(formattedCountString);
                ((TextView) mEosineCellView.findViewById(R.id.cell_count_text_view)).setText(formattedCountString);
                ((TextView) mMonoCellView.findViewById(R.id.cell_count_text_view)).setText(formattedCountString);
                ((TextView) mLymphoCellView.findViewById(R.id.cell_count_text_view)).setText(formattedCountString);
                ((TextView) mNeutroCellView.findViewById(R.id.cell_count_text_view)).setText(formattedCountString);
                break;
            default:
                throw new IllegalArgumentException("invalid cell reset from options menu");
        }
    }
}