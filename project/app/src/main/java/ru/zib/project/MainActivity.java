package ru.zib.project;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import ru.zib.project.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements AdFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // сразу же при старте активити запускаем наш списковый фрагмент
        Fragment fragment = new AdFragment();
        FragmentManager frm = getSupportFragmentManager();
        if (frm.findFragmentById(R.id.content_frame) == null) {
            FragmentTransaction ft = frm.beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            // это первое заполнение активити (которая служит лишь для хранения фрагментов)
            // поэтому при нажатии кнопки назад появляется сначала пустая активити и только при повторном нажатии назад выходим из приложения
            // чтобы этого не было, просто не добавляем в стек этот фрагмент. тогда мы выйдем при первом же нажатии на назад
            // ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ViewPagerFragment();
        Bundle arguement = new Bundle();
        arguement.putInt(ViewPagerFragment.CURRENT_PAGE_NUMBER, Integer.valueOf(item.id));
        fragment.setArguments(arguement);
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}
