package com.alexvasilkov.gestures.sample.ex.animations;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alexvasilkov.gestures.sample.R;
import com.alexvasilkov.gestures.sample.base.BaseSettingsActivity;
import com.alexvasilkov.gestures.sample.ex.utils.GlideHelper;
import com.alexvasilkov.gestures.sample.ex.utils.Painting;
import com.alexvasilkov.gestures.transition.GestureTransitions;
import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator;
import com.alexvasilkov.gestures.views.GestureImageView;

/**
 * This example demonstrates image animation from small mode into a full one.
 */
public class ImageAnimationActivity extends BaseSettingsActivity {

    private static final int PAINTING_ID = 2;

    private ImageView image, image2;
    private GestureImageView fullImage;
    private View fullBackground;
    private ViewsTransitionAnimator<?> animator, animator2, cur;

    private Painting painting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initContentView();

        image = findViewById(R.id.single_image);
        image2 = findViewById(R.id.single_image2);
        fullImage = findViewById(R.id.single_image_full);
        fullBackground = findViewById(R.id.single_image_back);

        //设置最大缩放
        fullImage.getController().getSettings().setMaxZoom(12f);

        // Loading image
        painting = Painting.list(getResources())[PAINTING_ID];
        GlideHelper.loadThumb(image, painting.thumbId);
        GlideHelper.loadThumb(image2, painting.thumbId);

        // We will expand image on click
        image.setOnClickListener(view -> {
            openFullImage(animator);
        });
        image2.setOnClickListener(view -> openFullImage(animator2));

        // Initializing image animator
        animator = GestureTransitions.from(image).into(fullImage);
        animator.addPositionUpdateListener(this::applyImageAnimationState);

        animator2 = GestureTransitions.from(image2).into(fullImage);
        animator2.addPositionUpdateListener(this::applyImageAnimationState);
    }

    /**
     * Override this method if you want to provide slightly different layout.
     */
    protected void initContentView() {
        setContentView(R.layout.image_animation_screen);
        setTitle(R.string.example_image_animation);
    }

    @Override
    public void onBackPressed() {
        // We should leave full image mode instead of closing the screen
        if (!cur.isLeaving()) {
            cur.exit(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSettingsChanged() {
        // Applying settings from toolbar menu, see BaseExampleActivity
        getSettingsController().apply(fullImage);
        // Resetting to initial image state
        fullImage.getController().resetState();
    }

    private void openFullImage(ViewsTransitionAnimator<?> animator2) {
        // Setting image drawable from 'from' view to 'to' to prevent flickering
        if (fullImage.getDrawable() == null) {
            fullImage.setImageDrawable(image.getDrawable());
        }

        // Updating gesture image settings
        getSettingsController().apply(fullImage);
        // Resetting to initial image state
        fullImage.getController().resetState();

        animator2.enterSingle(true);
        cur = animator2;
        GlideHelper.loadFull(fullImage, painting.imageId, painting.thumbId);
    }

    private void applyImageAnimationState(float position, boolean isLeaving) {
        Log.e("TESTTEST", "POS:" + position);
        fullBackground.setAlpha(0.5f * position);
        fullBackground.setVisibility(position == 0f && isLeaving ? View.INVISIBLE : View.VISIBLE);
        fullImage.setVisibility(position == 0f && isLeaving ? View.INVISIBLE : View.VISIBLE);
    }

}
