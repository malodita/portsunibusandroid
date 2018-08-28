package com.malcolm.portsmouthunibus.ui.onboarding;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malcolm.portsmouthunibus.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OnboardingStopFragment extends BaseOnboardingFragment implements OnboardingStopAdapter.OnStopSelectedListener {

    @BindView(R.id.onboarding_0_list)
    RecyclerView recyclerView;
    @BindView(R.id.onboarding_0_title)
    TextView title;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_onboarding_stop, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        sharedPreferences = getContext().getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        title.setText(R.string.onboarding_select_stop);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(new OnboardingStopAdapter(getResources().getStringArray(R.array.bus_stops_home), this));
        recyclerView.addItemDecoration(new ItemDecorations(4));
        return rootView;
    }

    @Override
    public void onStopSelected(int position) {
        OnboardingStopAdapter adapter = (OnboardingStopAdapter) recyclerView.getAdapter();
        adapter.removeItems();
        ObjectAnimator list = ObjectAnimator.ofFloat(recyclerView, "alpha", 1, 0).setDuration(300);
        ObjectAnimator text = ObjectAnimator.ofFloat(title, "alpha", 1, 0).setDuration(300);
        list.setStartDelay(200);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(text, list);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                sharedPreferences.edit()
                        .putInt(getContext().getString(R.string.preferences_home_bus_stop), position)
                        .apply();
                recyclerView.setVisibility(View.GONE);
                listener.onPageFinished();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.setStartDelay(400);
        set.start();
    }

    static class ItemDecorations extends RecyclerView.ItemDecoration {
        private final int spacing;
        private int displayMode;

        static final int HORIZONTAL = 0;
        static final int VERTICAL = 1;
        static final int GRID = 2;

        ItemDecorations(int spacing) {
            this(spacing, -1);
        }

        ItemDecorations(int spacing, int displayMode) {
            this.spacing = spacing;
            this.displayMode = displayMode;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            int position = parent.getChildViewHolder(view).getAdapterPosition();
            int itemCount = state.getItemCount();
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            setSpacingForDirection(outRect, layoutManager, position, itemCount);
        }

        private void setSpacingForDirection(Rect outRect,
                                            RecyclerView.LayoutManager layoutManager,
                                            int position,
                                            int itemCount) {

            // Resolve display mode automatically
            if (displayMode == -1) {
                displayMode = resolveDisplayMode(layoutManager);
            }

            switch (displayMode) {
                case HORIZONTAL:
                    outRect.left = spacing;
                    outRect.right = position == itemCount - 1 ? spacing : 0;
                    outRect.top = spacing;
                    outRect.bottom = spacing;
                    break;
                case VERTICAL:
                    outRect.left = spacing;
                    outRect.right = spacing;
                    outRect.top = spacing;
                    outRect.bottom = position == itemCount - 1 ? spacing : 0;
                    break;
                case GRID:
                    if (layoutManager instanceof GridLayoutManager) {
                        GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                        int cols = gridLayoutManager.getSpanCount();
                        int rows = itemCount / cols;

                        outRect.left = spacing;
                        outRect.right = position % cols == cols - 1 ? spacing : 0;
                        outRect.top = spacing;
                        outRect.bottom = position / cols == rows - 1 ? spacing : 0;
                    }
                    break;
            }
        }

        private int resolveDisplayMode(RecyclerView.LayoutManager layoutManager) {
            if (layoutManager instanceof GridLayoutManager) return GRID;
            if (layoutManager.canScrollHorizontally()) return HORIZONTAL;
            return VERTICAL;
        }
    }

}
