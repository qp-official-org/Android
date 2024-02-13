// Generated by view binder compiler. Do not edit!
package com.example.qp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public final class ItemWriteAnswerBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView writeAnswerBtn;

  @NonNull
  public final EditText writeAnswerEdit;

  @NonNull
  public final ConstraintLayout writeAnswerLayout;

  @NonNull
  public final ImageView writeAnswerUserImg;

  @NonNull
  public final TextView writeAnswerUserNameTv;

  private ItemWriteAnswerBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView writeAnswerBtn, @NonNull EditText writeAnswerEdit,
      @NonNull ConstraintLayout writeAnswerLayout, @NonNull ImageView writeAnswerUserImg,
      @NonNull TextView writeAnswerUserNameTv) {
    this.rootView = rootView;
    this.writeAnswerBtn = writeAnswerBtn;
    this.writeAnswerEdit = writeAnswerEdit;
    this.writeAnswerLayout = writeAnswerLayout;
    this.writeAnswerUserImg = writeAnswerUserImg;
    this.writeAnswerUserNameTv = writeAnswerUserNameTv;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemWriteAnswerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemWriteAnswerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_write_answer, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemWriteAnswerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.write_answer_btn;
      TextView writeAnswerBtn = ViewBindings.findChildViewById(rootView, id);
      if (writeAnswerBtn == null) {
        break missingId;
      }

      id = R.id.write_answer_edit;
      EditText writeAnswerEdit = ViewBindings.findChildViewById(rootView, id);
      if (writeAnswerEdit == null) {
        break missingId;
      }

      id = R.id.write_answer_layout;
      ConstraintLayout writeAnswerLayout = ViewBindings.findChildViewById(rootView, id);
      if (writeAnswerLayout == null) {
        break missingId;
      }

      id = R.id.write_answer_user_img;
      ImageView writeAnswerUserImg = ViewBindings.findChildViewById(rootView, id);
      if (writeAnswerUserImg == null) {
        break missingId;
      }

      id = R.id.write_answer_user_name_tv;
      TextView writeAnswerUserNameTv = ViewBindings.findChildViewById(rootView, id);
      if (writeAnswerUserNameTv == null) {
        break missingId;
      }

      return new ItemWriteAnswerBinding((ConstraintLayout) rootView, writeAnswerBtn,
          writeAnswerEdit, writeAnswerLayout, writeAnswerUserImg, writeAnswerUserNameTv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
