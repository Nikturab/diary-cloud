package com.null_pointer.diarycloud;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.null_pointer.diarycloud.exception.InternetConnectionRequiredException;
import com.null_pointer.diarycloud.exception.MessageWriteException;
import com.null_pointer.diarycloud.exception.DiaryCloudLoginRequiredException;
import com.null_pointer.diarycloud.model.DiaryCloud;
import com.null_pointer.diarycloud.model.response.Contact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddMessageFragment extends Fragment {

    private Spinner mSUserId;
    private EditText mETText;
    private EditText mSubject;
    private Button mBtnSend;
    private Contact[] users;
    private MainActivity getMainActivity(){
        return (MainActivity) getActivity();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_message, container, false);
        HashMap<Integer, Contact[]> raw = (HashMap<Integer, Contact[]>) getArguments().get("users");
        users = raw.get(0);

        List<String> list = new ArrayList<>();
        for(Contact c:users){
            String e = c.getName();
            list.add(e);
        }
        mSUserId = (Spinner) view.findViewById(R.id.sUserId);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, list);
        mSUserId.setAdapter(arrayAdapter);

        mETText = (EditText) view.findViewById(R.id.etAddMessage);
        mSubject = (EditText) view.findViewById(R.id.etAddMessageSubject);
        mBtnSend = (Button) view.findViewById(R.id. btnAddMessage);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mETText.getText().toString().isEmpty() || mSubject.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Заполните необходимые поля", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = (String) mSUserId.getSelectedItem();
                        String mid="";
                        boolean teacher=false;
                        for(Contact c:users){
                            if(name.equals(c.getName())){
                                mid = c.getId();
                                teacher = c.isTeacher();
                            }
                        }
                        if(mid.isEmpty())return;
                        DiaryCloud client = getMainActivity().getClient();
                        try {
                            client.writeMessage(mid, mETText.getText().toString(), mSubject.getText().toString(), teacher);
                        } catch (IOException | MessageWriteException  e) {
                            e.printStackTrace();
                            getMainActivity().showSnackbar(getString(R.string.error_try_later), Snackbar.LENGTH_SHORT);
                        }catch (DiaryCloudLoginRequiredException e){
                            getMainActivity().showSnackbar(getString(R.string.error_try_relogin), Snackbar.LENGTH_SHORT);
                        }catch (InternetConnectionRequiredException e){
                            getMainActivity().showConnectionWarning(null);
                        }
                    }
                }).start();
                Toast.makeText(getContext(), "Письмо было отправлено!" , Toast.LENGTH_SHORT).show();
                Fragment fragment = new MessagesFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main__content_frame, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        return view;
    }


}
