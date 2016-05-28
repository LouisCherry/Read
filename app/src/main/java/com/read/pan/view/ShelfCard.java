package com.read.pan.view;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.read.pan.R;

import butterknife.BindView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;

/**
 * Created by pan on 2016/5/27.
 */
public class ShelfCard extends Card {
    protected int resourceIdThumbnail = -1;
    protected int count;
    protected String headerTitle;
    protected String secondaryTitle;
    protected float rating;
    protected String thirdTitle;
    @BindView(R.id.card_shelf_remark)
    TextView cardShelfRemark;
    @BindView(R.id.card_shelf_size)
    TextView cardShelfSize;

    public String getThirdTitle() {
        return thirdTitle;
    }

    public void setThirdTitle(String thirdTitle) {
        this.thirdTitle = thirdTitle;
    }

    public int getResourceIdThumbnail() {
        return resourceIdThumbnail;
    }

    public void setResourceIdThumbnail(int resourceIdThumbnail) {
        this.resourceIdThumbnail = resourceIdThumbnail;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public String getSecondaryTitle() {
        return secondaryTitle;
    }

    public void setSecondaryTitle(String secondaryTitle) {
        this.secondaryTitle = secondaryTitle;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public ShelfCard(Context context) {
        super(context, R.layout.card_shelf);
    }

    public ShelfCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void init() {
        CardHeader header = new CardHeader(getContext(), R.layout.native_inner_gplay_header);
        header.setButtonOverflowVisible(true);
        header.setTitle(headerTitle);
        header.setPopupMenu(R.menu.shelf_book, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {

            }
        });

        addCardHeader(header);

        GplayGridThumb thumbnail = new GplayGridThumb(getContext());
        if (resourceIdThumbnail > -1)
            thumbnail.setDrawableResource(resourceIdThumbnail);
        else
            thumbnail.setDrawableResource(R.drawable.ab_bottom_solid_light_holo);
        addCardThumbnail(thumbnail);

        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                //Do something
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        cardShelfRemark.setText(thirdTitle);
        cardShelfSize.setText(secondaryTitle);

    }

    class GplayGridThumb extends CardThumbnail {

        public GplayGridThumb(Context context) {
            super(context);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {
            //viewImage.getLayoutParams().width = 196;
            //viewImage.getLayoutParams().height = 196;

        }
    }

}
