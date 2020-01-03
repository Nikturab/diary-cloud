package com.null_pointer.diarycloud;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.null_pointer.diarycloud.exception.InternetConnectionRequiredException;
import com.null_pointer.diarycloud.exception.MessageReplyException;
import com.null_pointer.diarycloud.exception.DiaryCloudLoginRequiredException;
import com.null_pointer.diarycloud.model.DiaryCloud;
import com.null_pointer.diarycloud.model.response.MessageReplyResponse;
import com.null_pointer.diarycloud.model.response.MessagesResponse;

import java.io.IOException;

/*
    Показывает сообщение с возможностью ответить
 */
public class MessageInfoFragment extends Fragment {

    private TextView mUserId; // имя написавшего
    private TextView mText; // текст сообщения
    private TextView mSubject; // тема
    private EditText mReplyText; // ответ
    private ImageButton mReplyBtn;
    private DiaryCloud client;

    private MainActivity mActivity;

    private MainActivity getMainActivity(){
        return mActivity;
    }

    public static MessageInfoFragment newInstance(MessagesResponse.Page.Message message) {
        MessageInfoFragment messageInfoFragment = new MessageInfoFragment();

        Bundle args = new Bundle();
        args.putString("sender", message.getSender());
        args.putString("content", message.getContent());
        args.putString("subject", message.getSubject());
        args.putInt("id", message.getId());
        messageInfoFragment.setArguments(args);

        return messageInfoFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages__fragment_message_info, container, false);
        mActivity = (MainActivity) getActivity();

        final String username = getArguments().getString("sender");
        final String text     = getArguments().getString("content");
        final String subject  = getArguments().getString("subject");
        final int    id       = getArguments().getInt("id");
        client = getMainActivity().getClient();

        mUserId = (TextView) view.findViewById(R.id.fragment_messages__fragment_message_info__user_id);
        mUserId.setText(username);

        mText = (TextView) view.findViewById(R.id.fragment_messages__fragment_message_info__content);
        mText.setText(text);

        mSubject = (TextView) view.findViewById(R.id.fragment_messages__fragment_message_info__subject);
        mSubject.setText(subject);

        mReplyText = (EditText) view.findViewById(R.id.fragment_messages__fragment_message_info__reply_text);
        mReplyBtn = (ImageButton) view.findViewById(R.id.fragment_messages__fragment_message_info__reply_button);

        new Thread(new Runnable() {
            @Override
            public void run() {
                setMessageRead(id);
            }
            // делаем сообщение "прочитанным"
        }).start();

        mReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String reply = mReplyText.getText().toString();
                if(reply.isEmpty()){
                    Toast.makeText(getContext(), R.string.validation_message_content_missed, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getContext(), R.string.success_message_sent, Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getFragmentManager().popBackStack();
                        MessageReplyResponse response;
                        try {
                            response = client.replyMessage(id, reply, getString(R.string.reply_add) + subject);
                            if(response.isMessageSent()){
                                // TODO: 18.05.2017 проверять статус отправки
                            }
                        } catch (IOException | MessageReplyException e) {
                            e.printStackTrace();
                        } catch (DiaryCloudLoginRequiredException e) {
                           getMainActivity().showLoginRequiredWarning(null);
                        }catch (InternetConnectionRequiredException e){
                            getMainActivity().showConnectionWarning(null);
                        }

                    }
                }).start();
            }
        });
        return view;
    }
    private void setMessageRead(int mid){
        try {
            client.setMessageRead(mid);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
