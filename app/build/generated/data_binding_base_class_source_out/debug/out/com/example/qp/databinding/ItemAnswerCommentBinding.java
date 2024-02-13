// Generated by view binder compiler. Do not edit!
package com.example.qp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public final class ItemAnswerCommentBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView commentContentTv;

  @NonNull
  public final ImageButton commentMoreBtn;

  @NonNull
  public final ImageView commentUserIv;

  @NonNull
  public final TextView commentUserNameTv;

  private ItemAnswerCommentBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView commentContentTv, @NonNull ImageButton commentMoreBtn,
      @NonNull ImageView commentUserIv, @NonNull TextView commentUserNameTv) {
    this.rootView = rootView;
    this.commentContentTv = commentContentTv;
    this.commentMoreBtn = commentMoreBtn;
    this.commentUserIv = commentUserIv;
    this.commentUserNameTv = commentUserNameTv;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemAnswerCommentBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemAnswerCommentBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_answer_comment, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemAnswerCommentBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.comment_content_tv;
      TextView commentContentTv = ViewBindings.findChildViewById(rootView, id);
      if (commentContentTv == null) {
        break missingId;
      }

      id = R.id.comment_more_btn;
      ImageButton commentMoreBtn = ViewBindings.findChildViewById(rootView, id);
      if (commentMoreBtn == null) {
        break missingId;
      }

      id = R.id.comment_user_iv;
      ImageView commentUserIv = ViewBindings.findChildViewById(rootView, id);
      if (commentUserIv == null) {
        break missingId;
      }

      id = R.id.comment_user_name_tv;
      TextView commentUserNameTv = ViewBindings.findChildViewById(rootView, id);
      if (commentUserNameTv == null) {
        break missingId;
      }

      return new ItemAnswerCommentBinding((ConstraintLayout) rootView, commentContentTv,
          commentMoreBtn, commentUserIv, commentUserNameTv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
