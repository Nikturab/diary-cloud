package com.null_pointer.diarycloud.view.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.null_pointer.diarycloud.R;

import java.util.Arrays;

    /*
        Адаптер для вывода элементов меню (иконка + текст)
     */
public class MenuAdapter extends ArrayAdapter<String> {
    private int[] icons;
    /*
        содержит id ресурсов с картиночками
     */

    public MenuAdapter(Context context, int resource, String[] items, int[] icons) {
        super(context, resource, Arrays.asList(items));
        this.icons = icons;
    }

    /*
     Получаем текст и соответствующую иконку
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v =  vi.inflate(R.layout.menu__row, null);
        }
        String p = getItem(position);
        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.menu__row__text);
            ImageView tt2 = (ImageView) v.findViewById(R.id.menu__row__icon);
            tt1.setText(p);
            tt2.setImageResource(icons[position]);

        }
        return v;
    }


}
