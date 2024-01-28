// Generated by view binder compiler. Do not edit!
package com.example.qp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public final class ItemNoticeBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ConstraintLayout noticeView;

  @NonNull
  public final TextView notifyBtn;

  @NonNull
  public final TextView notifyTv;

  private ItemNoticeBinding(@NonNull ConstraintLayout rootView,
      @NonNull ConstraintLayout noticeView, @NonNull TextView notifyBtn,
      @NonNull TextView notifyTv) {
    this.rootView = rootView;
    this.noticeView = noticeView;
    this.notifyBtn = notifyBtn;
    this.notifyTv = notifyTv;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemNoticeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemNoticeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_notice, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemNoticeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      ConstraintLayout noticeView = (ConstraintLayout) rootView;

      id = R.id.notify_btn;
      TextView notifyBtn = ViewBindings.findChildViewById(rootView, id);
      if (notifyBtn == null) {
        break missingId;
      }

      id = R.id.notify_tv;
      TextView notifyTv = ViewBindings.findChildViewById(rootView, id);
      if (notifyTv == null) {
        break missingId;
      }

      return new ItemNoticeBinding((ConstraintLayout) rootView, noticeView, notifyBtn, notifyTv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}