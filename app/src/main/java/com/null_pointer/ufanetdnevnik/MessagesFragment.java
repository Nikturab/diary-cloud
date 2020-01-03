package com.null_pointer.diarycloud;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.null_pointer.diarycloud.exception.InboxException;
import com.null_pointer.diarycloud.exception.InternetConnectionRequiredException;
import com.null_pointer.diarycloud.exception.DiaryCloudLoginRequiredException;
import com.null_pointer.diarycloud.model.DiaryCloud;
import com.null_pointer.diarycloud.model.response.Contact;
import com.null_pointer.diarycloud.model.response.MessagesResponse;
import com.null_pointer.diarycloud.view.messages.adapter.MessagesAdapter;
import com.null_pointer.diarycloud.view.messages.adapter.PageNumbersAdapter;
import com.null_pointer.diarycloud.view.messages.holder.MessageHolder;
import com.null_pointer.diarycloud.view.statistics.adapter.MarksAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MessagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private RecyclerView mPaginationRecyclerView;
    private ImageButton mBtnAddMessage;
    private SwipeRefreshLayout mRefreshLayout;

    private MessagesAdapter messagesAdapter;
    private View mMessagesLoader;
    private PageNumbersAdapter pageNumbersAdapter;
    private DiaryCloud client;

    private MainActivity mActivity;

    private MainActivity getMainActivity(){
        return mActivity;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        mActivity = (MainActivity) getActivity();

        client = getMainActivity().getClient();

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_messages__refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_messages__refresh_layout__list_message);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//прогрессбар для страницы сообщений
        mMessagesLoader =  view.findViewById(R.id.fragment_messages__refresh_layout__messagesLoader);

        mPaginationRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_messages__refresh_layout__pagination);
        mPaginationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

//написать сообщение
        mBtnAddMessage = (ImageButton) view.findViewById(R.id.fragment_messages__button_add_message);
        mBtnAddMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                    Если сообщение не загрузились, то отправить сообщения нельзя,
                    т.к. список контактов приходит вместе с ними
                 */
                if(messagesAdapter.getContacts() == null) return;
                Fragment fragment = new AddMessageFragment();
                Bundle args = new Bundle();
                HashMap<Integer, Contact[]> contacts = new HashMap<>();
                contacts.put(0,  messagesAdapter.getContacts());
                args.putSerializable("users", contacts);
                fragment.setArguments(args);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main__content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();

            }
        });

        messagesAdapter = new MessagesAdapter(new ArrayList<MessagesResponse.Page.Message>(), MessagesFragment.this);
        mRecyclerView.setAdapter(messagesAdapter);

        /*
        В будущем будет поддерживаться удаление сообщений.
        Сейчас это невозможно всвязи с техническими неполадками на стороне Уфанета.
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallbackItemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        */
        // номера страниц
        pageNumbersAdapter = new PageNumbersAdapter(new int[]{}, getContext(), new PageNumbersAdapter.Clickable() {

            public void onClick(View view, final int pageNo) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(pageNo);
                    }
                }).start();
            }
        });
        mPaginationRecyclerView.setAdapter(pageNumbersAdapter);


        new Thread(new Runnable() {
            @Override
            public void run() {
                updateUI(1);
            }
            //ставим на загрузку первую страницу
        }).start();
        return view;
    }

    private void updateUI(final int pageNo) {
        client.setConnectionStatus(getMainActivity().checkInternet());
        getMainActivity().sendMessage(new MainActivity.MainActivityMessage(){
            @Override
            public void runOperation() {
                mRecyclerView.setVisibility(View.GONE);
                mMessagesLoader.setVisibility(View.VISIBLE);
                /*
                    показываем progressbar
                 */
            }
        });
        MessagesResponse.Page.Message[]  inbox;
        MessagesResponse.Page page;

        try {
            page = client.getInbox(pageNo).getPage();
            inbox = page.getInbox();
            messagesAdapter.setContacts( page.getContacts() );

            final MessagesResponse.Page.Pagination pagination = page.getPagination();

            final boolean has_next = pagination.hasNext();
            final int last_page = Integer.valueOf(pagination.getLastPage());
            ArrayList<MessagesResponse.Page.Message> messages = new ArrayList<>();
            Collections.addAll(messages, inbox);
            messagesAdapter.setMessages(messages);

            getMainActivity().sendMessage(new MainActivity.MainActivityMessage() {
                @Override
                public void runOperation() {
                    pageNumbersAdapter.setPage(pageNo);
                    mMessagesLoader.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    //скрыли прогресбар
                    pageNumbersAdapter.setHasNext(has_next);
                    pageNumbersAdapter.setLastPage(last_page);
                    pageNumbersAdapter.notifyDataSetChanged();
                    messagesAdapter.notifyDataSetChanged();
                }
            });
        }catch (IOException | InboxException e) {
            e.printStackTrace();
        } catch (DiaryCloudLoginRequiredException e) {
            if(client != null) client.signOut(getContext());
            getMainActivity().showLoginRequiredWarning(null);
        }catch (InternetConnectionRequiredException e){
            getMainActivity().showConnectionWarning(null);
        } catch(Exception e){
            e.printStackTrace();
            getMainActivity().showLoginRequiredWarning(null);
        }

    }

    // при обновлении свайпом сверху
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateUI(pageNumbersAdapter.getPage());
            }
        }).start();

        mRefreshLayout.setRefreshing(false);
    }

// будет реализовано. Невозможно в связи с тех. неполадками уфанета
      ItemTouchHelper.SimpleCallback simpleCallbackItemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT){

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();

            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
        }
    };
//
}
