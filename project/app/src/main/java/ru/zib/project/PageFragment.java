package ru.zib.project;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.zib.project.dummy.DummyContent;


/**
 * A simple {@link Fragment} subclass.
 */
public class PageFragment extends Fragment {

    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    int pageNumber;

    public PageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        TextView tvPage = (TextView)view.findViewById(R.id.tvPage);
        ImageView ivPage = (ImageView)view.findViewById(R.id.ivPage);

        DummyContent.DummyItem item = DummyContent.ITEM_MAP.get(String.valueOf(pageNumber));
        tvPage.setText(item.info);
        AdFragment.loadBitmap(item.img, ivPage, getContext(), 200);


        return view;
    }


    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }
}
