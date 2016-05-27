package ru.zib.project;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.zib.project.dummy.DummyContent;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {
    ViewPager pager;
    PagerAdapter pagerAdapter;
    int currentItem = 0;

    static final String CURRENT_PAGE_NUMBER = "arg_page_number";

    public ViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);

        // работаем с PagerView
        pager = (ViewPager)view.findViewById(R.id.pager_frag);
        // скармливаю ChildFragmentManager, потому что внутри фрагмента вложенный фрагмент

        pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return PageFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return DummyContent.ITEM_MAP.size();
            }
        };

        pager.setAdapter(pagerAdapter);

        pager.setCurrentItem(getArguments().getInt(CURRENT_PAGE_NUMBER));

        return view;
    }

}
