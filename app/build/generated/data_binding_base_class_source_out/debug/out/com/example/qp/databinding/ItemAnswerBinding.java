// Generated by view binder compiler. Do not edit!
package com.example.qp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.qp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemAnswerBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final LinearLayout answerCommentBtnLayout;

  @NonNull
  public final TextView answerCommentBtnTv;

  @NonNull
  public final LinearLayout answerCommentLikeLayout;

  @NonNull
  public final RecyclerView answerCommentRv;

  @NonNull
  public final TextView answerContentTv;

  @NonNull
  public final ImageButton answerLikeBtn;

  @NonNull
  public final TextView answerLikeTv;

  @NonNull
  public final ImageButton answerMoreBtn;

  @NonNull
  public final ImageView answerUserImg;

  @NonNull
  public final TextView answerUserNameTv;

  @NonNull
  public final ConstraintLayout commentLayout;

  @NonNull
  public final ConstraintLayout previewContainer;

  @NonNull
  public final TextView writeCommentBtn;

  @NonNull
  public final EditText writeCommentEdit;

  @NonNull
  public final ConstraintLayout writeCommentLayout;

  @NonNull
  public final ImageButton writeCommentMoreBtn;

  @NonNull
  public final ImageView writeCommentUserImg;

  @NonNull
  public final TextView writeCommentUserNameTv;

  private ItemAnswerBinding(@NonNull ConstraintLayout rootView,
      @NonNull LinearLayout answerCommentBtnLayout, @NonNull TextView answerCommentBtnTv,
      @NonNull LinearLayout answerCommentLikeLayout, @NonNull RecyclerView answerCommentRv,
      @NonNull TextView answerContentTv, @NonNull ImageButton answerLikeBtn,
      @NonNull TextView answerLikeTv, @NonNull ImageButton answerMoreBtn,
      @NonNull ImageView answerUserImg, @NonNull TextView answerUserNameTv,
      @NonNull ConstraintLayout commentLayout, @NonNull ConstraintLayout previewContainer,
      @NonNull TextView writeCommentBtn, @NonNull EditText writeCommentEdit,
      @NonNull ConstraintLayout writeCommentLayout, @NonNull ImageButton writeCommentMoreBtn,
      @NonNull ImageView writeCommentUserImg, @NonNull TextView writeCommentUserNameTv) {
    this.rootView = rootView;
    this.answerCommentBtnLayout = answerCommentBtnLayout;
    this.answerCommentBtnTv = answerCommentBtnTv;
    this.answerCommentLikeLayout = answerCommentLikeLayout;
    this.answerCommentRv = answerCommentRv;
    this.answerContentTv = answerContentTv;
    this.answerLikeBtn = answerLikeBtn;
    this.answerLikeTv = answerLikeTv;
    this.answerMoreBtn = answerMoreBtn;
    this.answerUserImg = answerUserImg;
    this.answerUserNameTv = answerUserNameTv;
    this.commentLayout = commentLayout;
    this.previewContainer = previewContainer;
    this.writeCommentBtn = writeCommentBtn;
    this.writeCommentEdit = writeCommentEdit;
    this.writeCommentLayout = writeCommentLayout;
    this.writeCommentMoreBtn = writeCommentMoreBtn;
    this.writeCommentUserImg = writeCommentUserImg;
    this.writeCommentUserNameTv = writeCommentUserNameTv;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemAnswerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemAnswerBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_answer, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemAnswerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.answer_comment_btn_layout;
      LinearLayout answerCommentBtnLayout = ViewBindings.findChildViewById(rootView, id);
      if (answerCommentBtnLayout == null) {
        break missingId;
      }

      id = R.id.answer_comment_btn_tv;
      TextView answerCommentBtnTv = ViewBindings.findChildViewById(rootView, id);
      if (answerCommentBtnTv == null) {
        break missingId;
      }

      id = R.id.answer_comment_like_layout;
      LinearLayout answerCommentLikeLayout = ViewBindings.findChildViewById(rootView, id);
      if (answerCommentLikeLayout == null) {
        break missingId;
      }

      id = R.id.answer_comment_rv;
      RecyclerView answerCommentRv = ViewBindings.findChildViewById(rootView, id);
      if (answerCommentRv == null) {
        break missingId;
      }

      id = R.id.answer_content_tv;
      TextView answerContentTv = ViewBindings.findChildViewById(rootView, id);
      if (answerContentTv == null) {
        break missingId;
      }

      id = R.id.answer_like_btn;
      ImageButton answerLikeBtn = ViewBindings.findChildViewById(rootView, id);
      if (answerLikeBtn == null) {
        break missingId;
      }

      id = R.id.answer_like_tv;
      TextView answerLikeTv = ViewBindings.findChildViewById(rootView, id);
      if (answerLikeTv == null) {
        break missingId;
      }

      id = R.id.answer_more_btn;
      ImageButton answerMoreBtn = ViewBindings.findChildViewById(rootView, id);
      if (answerMoreBtn == null) {
        break missingId;
      }

      id = R.id.answer_user_img;
      ImageView answerUserImg = ViewBindings.findChildViewById(rootView, id);
      if (answerUserImg == null) {
        break missingId;
      }

      id = R.id.answer_user_name_tv;
      TextView answerUserNameTv = ViewBindings.findChildViewById(rootView, id);
      if (answerUserNameTv == null) {
        break missingId;
      }

      id = R.id.comment_layout;
      ConstraintLayout commentLayout = ViewBindings.findChildViewById(rootView, id);
      if (commentLayout == null) {
        break missingId;
      }

      id = R.id.preview_container;
      ConstraintLayout previewContainer = ViewBindings.findChildViewById(rootView, id);
      if (previewContainer == null) {
        break missingId;
      }

      id = R.id.write_comment_btn;
      TextView writeCommentBtn = ViewBindings.findChildViewById(rootView, id);
      if (writeCommentBtn == null) {
        break missingId;
      }

      id = R.id.write_comment_edit;
      EditText writeCommentEdit = ViewBindings.findChildViewById(rootView, id);
      if (writeCommentEdit == null) {
        break missingId;
      }

      id = R.id.write_comment_layout;
      ConstraintLayout writeCommentLayout = ViewBindings.findChildViewById(rootView, id);
      if (writeCommentLayout == null) {
        break missingId;
      }

      id = R.id.write_comment_more_btn;
      ImageButton writeCommentMoreBtn = ViewBindings.findChildViewById(rootView, id);
      if (writeCommentMoreBtn == null) {
        break missingId;
      }

      id = R.id.write_comment_user_img;
      ImageView writeCommentUserImg = ViewBindings.findChildViewById(rootView, id);
      if (writeCommentUserImg == null) {
        break missingId;
      }

      id = R.id.write_comment_user_name_tv;
      TextView writeCommentUserNameTv = ViewBindings.findChildViewById(rootView, id);
      if (writeCommentUserNameTv == null) {
        break missingId;
      }

      return new ItemAnswerBinding((ConstraintLayout) rootView, answerCommentBtnLayout,
          answerCommentBtnTv, answerCommentLikeLayout, answerCommentRv, answerContentTv,
          answerLikeBtn, answerLikeTv, answerMoreBtn, answerUserImg, answerUserNameTv,
          commentLayout, previewContainer, writeCommentBtn, writeCommentEdit, writeCommentLayout,
          writeCommentMoreBtn, writeCommentUserImg, writeCommentUserNameTv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
