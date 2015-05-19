package com.bohn.boomesh.wbcdifferentialcounter.homescreen;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bohn.boomesh.wbcdifferentialcounter.R;
import com.bohn.boomesh.wbcdifferentialcounter.models.WhiteBloodCell;
import com.bohn.boomesh.wbcdifferentialcounter.models.WhiteBloodCell.WBCType;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeActivityFragment extends Fragment implements HomeActivity.OptionsItemSelectedListener {

    private final String KEY_WBC_LIST = "WHITE_BLOOD_CELL_LIST";
    private final String KEY_WBC_UNDO_STACK = "UNDO_STACK";

    private Animation mMaxCountAnim;
    private final AtomicBoolean mIsMaxCountAnimEnded = new AtomicBoolean(true);

    private TextView mTotalCountTxt;
    private View mBasoCellView;
    private View mEosineCellView;
    private View mMonoCellView;
    private View mLymphoCellView;
    private View mNeutroCellView;
    private View mMaxCountReachedView;

    private RecyclerView mWBCCountersRecyclerView;
    private WBCCounterAdapter mWBCCounterAdapter;
    private final WBCCounterAdapter.OnItemClickedListener mOnItemClickedListener = new WBCCounterAdapter.OnItemClickedListener() {
        @Override
        public void onItemClicked(int pPosition, View pView) {
            if (pPosition != RecyclerView.NO_POSITION) {
                final WBCType type = mListOfWBC.get(pPosition).getType();
                final TextView countTextView = (TextView) pView.findViewById(R.id.cell_count_text_view);
                onCellClicked(type, countTextView);
            }
        }
    };

    private ArrayList<WhiteBloodCell> mListOfWBC;
    private ArrayList<WhiteBloodCell> mUndoStack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (savedInstanceState != null) {
            mListOfWBC = savedInstanceState.getParcelableArrayList(KEY_WBC_LIST);
            mUndoStack = savedInstanceState.getParcelableArrayList(KEY_WBC_UNDO_STACK);
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
            mUndoStack = new ArrayList<>();
        }


        mTotalCountTxt = (TextView) view.findViewById(R.id.total_count_textview);
        mWBCCountersRecyclerView = (RecyclerView) view.findViewById(R.id.counter_recycler_view);

        mMaxCountReachedView = view.findViewById(R.id.max_count_reached_view);
        mMaxCountReachedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaPlayer mCoinSound = MediaPlayer.create(getActivity(), R.raw.coin__timgormly__8bitcoin);
                mCoinSound.start();
                mCoinSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mp != null) {
                            mp.release();
                        }
                    }
                });

                animateMaxCountReached();
            }
        });

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
            mWBCCountersRecyclerView.setHasFixedSize(true);
            mWBCCountersRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        }

        if (mWBCCountersRecyclerView != null) {
            mWBCCounterAdapter = new WBCCounterAdapter(mListOfWBC, mOnItemClickedListener);
            mWBCCountersRecyclerView.setAdapter(mWBCCounterAdapter);
        }

        updateTotalCount();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMaxCountAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.max_count_bounce);
        mMaxCountAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsMaxCountAnimEnded.set(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsMaxCountAnimEnded.set(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //do nothing
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_WBC_LIST, mListOfWBC);
        outState.putParcelableArrayList(KEY_WBC_UNDO_STACK, mUndoStack);

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
        final MediaPlayer mClickSound = MediaPlayer.create(getActivity(), pCellType.mClickSoundResourceId);
        mClickSound.start();
        mClickSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp != null) {
                    mp.release();
                }
            }
        });

        final WhiteBloodCell whiteBloodCell = getCell(pCellType);
        mUndoStack.add(new WhiteBloodCell(whiteBloodCell));

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

        final boolean isMaximumReached = totalCount >= TOTAL_WBC_COUNT;
        mTotalCountTxt.setText(String.format(getString(R.string.total_count_format), totalCount));
        mMaxCountReachedView.setVisibility(isMaximumReached ? View.VISIBLE : View.GONE);

        if (mWBCCountersRecyclerView == null) {
            mBasoCellView.setEnabled(!isMaximumReached);
            mEosineCellView.setEnabled(!isMaximumReached);
            mMonoCellView.setEnabled(!isMaximumReached);
            mLymphoCellView.setEnabled(!isMaximumReached);
            mNeutroCellView.setEnabled(!isMaximumReached);
        } else {
            mWBCCounterAdapter.setItemClickedListener(isMaximumReached ? null : mOnItemClickedListener);
        }

        if (isMaximumReached) {
            animateMaxCountReached();
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
                    final WhiteBloodCell prevWBC = mUndoStack.remove(mUndoStack.size() - 1);
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

    private void animateMaxCountReached() {
        if (mMaxCountAnim != null && mIsMaxCountAnimEnded.get()) {
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.max_count_color_animator);
            set.setTarget(mTotalCountTxt);
            set.start();
            mTotalCountTxt.startAnimation(mMaxCountAnim);
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
     * used for in the landscape layout
     *
     * @param pCellType of type WBCType in WhiteBloodCell
     * @param pCount    the integer valyout you would like the count textview in the landscape layout to be
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
