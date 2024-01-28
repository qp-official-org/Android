// Generated by view binder compiler. Do not edit!
package com.example.qp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.qp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemQuestionBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView itemCategory1Tv;

  @NonNull
  public final TextView itemCategory2Tv;

  @NonNull
  public final TextView itemCategory3Tv;

  @NonNull
  public final TextView itemQuestionTv;

  @NonNull
  public final TextView itemTimeTv;

  @NonNull
  public final ImageView itemUserIv;

  private ItemQuestionBinding(@NonNull ConstraintLayout rootView, @NonNull TextView itemCategory1Tv,
      @NonNull TextView itemCategory2Tv, @NonNull TextView itemCategory3Tv,
      @NonNull TextView itemQuestionTv, @NonNull TextView itemTimeTv,
      @NonNull ImageView itemUserIv) {
    this.rootView = rootView;
    this.itemCategory1Tv = itemCategory1Tv;
    this.itemCategory2Tv = itemCategory2Tv;
    this.itemCategory3Tv = itemCategory3Tv;
    this.itemQuestionTv = itemQuestionTv;
    this.itemTimeTv = itemTimeTv;
    this.itemUserIv = itemUserIv;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemQuestionBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemQuestionBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_question, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemQuestionBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.item_category1_tv;
      TextView itemCategory1Tv = ViewBindings.findChildViewById(rootView, id);
      if (itemCategory1Tv == null) {
        break missingId;
      }

      id = R.id.item_category2_tv;
      TextView itemCategory2Tv = ViewBindings.findChildViewById(rootView, id);
      if (itemCategory2Tv == null) {
        break missingId;
      }

      id = R.id.item_category3_tv;
      TextView itemCategory3Tv = ViewBindings.findChildViewById(rootView, id);
      if (itemCategory3Tv == null) {
        break missingId;
      }

      id = R.id.item_question_tv;
      TextView itemQuestionTv = ViewBindings.findChildViewById(rootView, id);
      if (itemQuestionTv == null) {
        break missingId;
      }

      id = R.id.item_time_tv;
      TextView itemTimeTv = ViewBindings.findChildViewById(rootView, id);
      if (itemTimeTv == null) {
        break missingId;
      }

      id = R.id.item_user_iv;
      ImageView itemUserIv = ViewBindings.findChildViewById(rootView, id);
      if (itemUserIv == null) {
        break missingId;
      }

      return new ItemQuestionBinding((ConstraintLayout) rootView, itemCategory1Tv, itemCategory2Tv,
          itemCategory3Tv, itemQuestionTv, itemTimeTv, itemUserIv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}