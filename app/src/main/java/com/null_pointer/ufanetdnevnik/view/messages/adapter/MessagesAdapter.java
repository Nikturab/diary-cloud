package com.null_pointer.diarycloud.view.messages.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.null_pointer.diarycloud.MessageInfoFragment;
import com.null_pointer.diarycloud.R;
import com.null_pointer.diarycloud.model.response.Contact;
import com.null_pointer.diarycloud.model.response.MessagesResponse;
import com.null_pointer.diarycloud.view.messages.holder.MessageHolder;

import java.util.List;

/*
Адаптер сообщений одной страницы
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessageHolder> {
    private List<MessagesResponse.Page.Message> mMessages;
    private Contact[] contacts;

    private Fragment rootFragment;

    public MessagesAdapter(List<MessagesResponse.Page.Message> messages, Fragment rootFragment) {
        mMessages = messages;
        this.rootFragment = rootFragment;
    }

    public Contact[] getContacts(){
        return contacts;
    }

    public void setContacts( Contact[] contacts){
        this.contacts = contacts;
    }


    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        MessagesResponse.Page.Message message = mMessages.get(position);
        holder.bindMessage(message);
    }

    public void setMessages(List<MessagesResponse.Page.Message> messages){
        mMessages = messages;
    }


    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(rootFragment.getContext()).inflate(R.layout.list_item_message, parent, false);
        return new MessageHolder(view, rootFragment.getContext(), new MessageHolder.Clickable() {
            public void onClick(View view, MessagesResponse.Page.Message message) {
                // показываем сообщение с возможность ответить(фрагмент MessageInfo)
                Fragment mFragment = MessageInfoFragment.newInstance(message);
                if (mFragment != null) {
                    FragmentTransaction fragmentTransaction = rootFragment.getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.activity_main__content_frame, mFragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}
