package com.newtpond.filterlistviewtutorial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

final class GravatarAdapter extends BaseAdapter {
  private final String mStyle;
  private final Context mContext;
  private final LayoutInflater mLayoutInflater;
  private final int mAvatarImageViewPixelSize;
  private List<ParseObject> mProfiles = Collections.emptyList();
  private ArrayList<ParseObject> arraylist;

  public GravatarAdapter(Context context, String style) {
    mContext = context;
    mStyle = style;
    mLayoutInflater = LayoutInflater.from(context);
    mAvatarImageViewPixelSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_image_view_size);
    this.arraylist = new ArrayList<ParseObject>();
  }

  public void updateUsers(List<ParseObject> profiles) {
    mProfiles = profiles;
    arraylist.addAll(mProfiles);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return mProfiles.size();
  }

  @Override
  public ParseObject getItem(int position) {
    return mProfiles.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

      if (convertView == null) {
          if(mStyle == "item") {
              convertView = mLayoutInflater.inflate(R.layout.listview_item, null);
          } else if(mStyle == "item_checkbox") {
              convertView = mLayoutInflater.inflate(R.layout.user_list_item_checkbox, null);
          }
      }

    ParseObject profile = getItem(position);

    String gravatarUrl = "";

    if(profile.getString("emailHash") != null) {
        gravatarUrl = "http://www.gravatar.com/avatar/"
            + profile.getString("emailHash") /* use emailHash from profile */
            + "?s=" + mAvatarImageViewPixelSize /* set avatar size */
            + "&d=404" /* force 404 */;
    } else {
        gravatarUrl = "https://graph.facebook.com/"
            + profile.getString("facebookId")
            + "/picture?height=" + mAvatarImageViewPixelSize;
    }

    Transformation transformation = new RoundedTransformationBuilder()
            .cornerRadiusDp(mAvatarImageViewPixelSize/2)
            .oval(false)
            .build();

    Picasso.with(mContext)
            .load(gravatarUrl)
            .placeholder(R.mipmap.ic_contact_picture)
            .error(R.mipmap.ic_contact_picture)
            .resize(mAvatarImageViewPixelSize, mAvatarImageViewPixelSize)
            .transform(transformation)
            .into((ImageView) convertView.findViewById(R.id.user_avatar));

    ((TextView) convertView.findViewById(R.id.user_name)).setText(profile.getString("displayName"));

    return convertView;
  }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mProfiles.clear();
        if (charText.length() == 0) {
            mProfiles.addAll(arraylist);
        } else {
            for (ParseObject profile : arraylist) {
                if (profile.getString("displayName").toLowerCase(Locale.getDefault()).contains(charText)) {
                    mProfiles.add(profile);
                }
            }
        }
        notifyDataSetChanged();
    }
}
