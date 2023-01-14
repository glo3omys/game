// Generated by view binder compiler. Do not edit!
package com.example.myapplication.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.myapplication.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityScavengerHuntBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button btnHome;

  @NonNull
  public final Button btnItemlist;

  @NonNull
  public final Button btnPause;

  @NonNull
  public final Button btnStart;

  @NonNull
  public final RelativeLayout layMenu;

  @NonNull
  public final TextView tvItemName;

  @NonNull
  public final TextView tvItemScore;

  private ActivityScavengerHuntBinding(@NonNull ConstraintLayout rootView, @NonNull Button btnHome,
      @NonNull Button btnItemlist, @NonNull Button btnPause, @NonNull Button btnStart,
      @NonNull RelativeLayout layMenu, @NonNull TextView tvItemName,
      @NonNull TextView tvItemScore) {
    this.rootView = rootView;
    this.btnHome = btnHome;
    this.btnItemlist = btnItemlist;
    this.btnPause = btnPause;
    this.btnStart = btnStart;
    this.layMenu = layMenu;
    this.tvItemName = tvItemName;
    this.tvItemScore = tvItemScore;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityScavengerHuntBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityScavengerHuntBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_scavenger_hunt, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityScavengerHuntBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_home;
      Button btnHome = ViewBindings.findChildViewById(rootView, id);
      if (btnHome == null) {
        break missingId;
      }

      id = R.id.btn_itemlist;
      Button btnItemlist = ViewBindings.findChildViewById(rootView, id);
      if (btnItemlist == null) {
        break missingId;
      }

      id = R.id.btn_pause;
      Button btnPause = ViewBindings.findChildViewById(rootView, id);
      if (btnPause == null) {
        break missingId;
      }

      id = R.id.btn_start;
      Button btnStart = ViewBindings.findChildViewById(rootView, id);
      if (btnStart == null) {
        break missingId;
      }

      id = R.id.lay_menu;
      RelativeLayout layMenu = ViewBindings.findChildViewById(rootView, id);
      if (layMenu == null) {
        break missingId;
      }

      id = R.id.tv_item_name;
      TextView tvItemName = ViewBindings.findChildViewById(rootView, id);
      if (tvItemName == null) {
        break missingId;
      }

      id = R.id.tv_item_score;
      TextView tvItemScore = ViewBindings.findChildViewById(rootView, id);
      if (tvItemScore == null) {
        break missingId;
      }

      return new ActivityScavengerHuntBinding((ConstraintLayout) rootView, btnHome, btnItemlist,
          btnPause, btnStart, layMenu, tvItemName, tvItemScore);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
