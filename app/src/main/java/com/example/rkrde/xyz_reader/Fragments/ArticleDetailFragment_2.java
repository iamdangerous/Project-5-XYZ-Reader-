package com.example.rkrde.xyz_reader.Fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.rkrde.xyz_reader.R;
import com.example.rkrde.xyz_reader.data.ArticleLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment_2 extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment_2";

    public static final String ARG_ITEM_ID = "item_id";
    private static final float PARALLAX_FACTOR = 1.25f;

    private Cursor mCursor;
    private long mItemId;
    private int darkMutedColor = 0xFF333333;

    private int mScrollY;
    private boolean mIsCard = false;
    private int mStatusBarFullOpacityBottom;

    @Bind(R.id.image_view)    AppCompatImageView imageView;
    @Bind(R.id.fab)    FloatingActionButton fab;
    @Bind(R.id.tv_title)    AppCompatTextView tvTitle;
    @Bind(R.id.tv_body)    AppCompatTextView tvBody;
    @Bind(R.id.tv_line)    AppCompatTextView tvLine;
    @Bind(R.id.root_view)    CoordinatorLayout mRootView;
    @Bind(R.id.appbar)    AppBarLayout appBarLayout;
    @Bind(R.id.meta_bar)    LinearLayout linearLayout;


    @OnClick(R.id.fab)
    void onClickFab(){
        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
    }



    public ArticleDetailFragment_2() {
    }

    public static ArticleDetailFragment_2 newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment_2 fragment = new ArticleDetailFragment_2();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        mIsCard = getResources().getBoolean(R.bool.detail_is_card);
        mStatusBarFullOpacityBottom = getResources().getDimensionPixelSize(
                R.dimen.detail_card_top_margin);
        setHasOptionsMenu(true);


    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_new, container, false);
        ButterKnife.bind(this,v);



        bindViews();
        updateStatusBar();
        return v;
    }

    private void updateStatusBar() {
        int color = 0;
//        if (mPhotoView != null && mTopInset != 0 && mScrollY > 0) {
//            float f = progress(mScrollY,
//                    mStatusBarFullOpacityBottom - mTopInset * 3,
//                    mStatusBarFullOpacityBottom - mTopInset);
//            color = Color.argb((int) (255 * f),
//                    (int) (Color.red(darkMutedColor) * 0.9),
//                    (int) (Color.green(darkMutedColor) * 0.9),
//                    (int) (Color.blue(darkMutedColor) * 0.9));
//        }
//        mStatusBarColorDrawable.setColor(color);
//        mDrawInsetsFrameLayout.setInsetBackground(mStatusBarColorDrawable);
    }

    static float progress(float v, float min, float max) {
        return constrain((v - min) / (max - min), 0, 1);
    }

    static float constrain(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    private void bindViews() {
//        if (mRootView == null) {
//            return;
//        }
        tvBody.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));
//
        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            tvTitle.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            tvLine.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by <font color='#ffffff'>"
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
                            + "</font>"));
            tvBody.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));


            Glide.with(this)
                    .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            imageView.setImageDrawable(resource);

                            updatePalete(imageView);
                            return false;
                        }
                    })
                    .into(imageView);


        } else {
            mRootView.setVisibility(View.GONE);
            tvTitle.setText("N/A");
            tvLine.setText("N/A" );
            tvBody.setText("N/A");
        }
    }

    private void updatePalete(AppCompatImageView imageView){

        imageView.buildDrawingCache();
        Bitmap bmap = imageView.getDrawingCache();
        // Asynchronous
        Palette.from(bmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance
                darkMutedColor = p.getDarkMutedColor(0xFF333333);
                int mutedLightColor = p.getLightMutedColor(ContextCompat.getColor(getActivity(),R.color.colorAccent));
                int mutedColor = p.getMutedColor(0xFF333333);
                tvBody.setTextColor(darkMutedColor);
                linearLayout.setBackgroundColor(darkMutedColor);
                fab.setBackgroundTintList(ColorStateList.valueOf(mutedLightColor));

                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    Window window = getActivity().getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(darkMutedColor);
                }
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

    public int getUpButtonFloor() {
//        if (mPhotoContainerView == null || mPhotoView.getHeight() == 0) {
//            return Integer.MAX_VALUE;
//        }
//
//        // account for parallax
//        return mIsCard
//                ? (int) mPhotoContainerView.getTranslationY() + mPhotoView.getHeight() - mScrollY
//                : mPhotoView.getHeight() - mScrollY;
        return 0;
    }
}
