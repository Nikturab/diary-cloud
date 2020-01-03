package com.null_pointer.diarycloud.view.messages.holder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.null_pointer.diarycloud.MessageInfoFragment;
import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.lib.GenderDetector;
import com.null_pointer.diarycloud.model.response.MessagesResponse;

/**
 * Created by Ayili Nikturab on 18.05.2017.
 */

public class MessageHolder extends RecyclerView.ViewHolder {

    private TextView mUserId;
    private TextView mText;
    private TextView mDate;
    private ImageButton mIsNew;
    private ImageView mIcon;
    private MessagesResponse.Page.Message message;

    private Context context;
    public MessageHolder(View view, Context context, final MessageHolder.Clickable listener) {
        super(view);
        this.context = context;

        mUserId = (TextView) view.findViewById(R.id.list_item_message__user_id);
        mIcon = (ImageView) view.findViewById(R.id.list_item_message__person_icon);
        mText = (TextView) view.findViewById(R.id.list_item_message__content);
        mDate = (TextView) view.findViewById(R.id.list_item_message__date);
        mIsNew = (ImageButton) view.findViewById(R.id.list_item_message__is_new);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view, message);
            }
        });
    }

    public void bindMessage(MessagesResponse.Page.Message message) {
        this.message = message;
        String full = this.message.getSender();
        String patronymic = full.split(" ")[2];
        boolean gender = GenderDetector.getGenderByPatronymic( patronymic);

        mIcon.setImageResource(gender ? R.drawable.man : R.drawable.woman);

        mUserId.setText(this.message.getSender());
        mText.setText(
                ( this.message.getContent().length() >=50 ? this.message.getContent().substring(0, 50) + "...": this.message.getContent() )
        );
        mDate.setText(this.message.getSendDate());
        if (!message.isSeen()) mIsNew.setVisibility(View.VISIBLE);
    }

    public interface Clickable{
        public void onClick(View view, MessagesResponse.Page.Message message);
    }
}
