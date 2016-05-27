package ru.zib.project;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.zib.project.dummy.DummyContent;
import ru.zib.project.dummy.DummyContent.DummyItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AdFragment extends Fragment {

    final String LOG_TAG = "myLogs";

    // заводим кеш под картиночки
    private static LruCache<String, Bitmap> mMemoryCache;


    // TODO: Customize parameters
    private int mColumnCount = 1;

    private OnListFragmentInteractionListener mListener;    //насколько я понимаю, это та активити, которая вызывает фрагмент (содержит его)

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AdFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ad_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Log.d(LOG_TAG, "вьюшка - инстанс рецайклера");

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            // инициализиуем кеш одной восьмой от общей памяти, доступной приложению
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
            final int cacheSize = maxMemory / 8;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount();
                }
            };


            // этот пустое создание объекта нужно только для того, чтобы сработа конструктор, внутри которого заполняется коллекция ITEMS
            DummyContent d = new DummyContent(getActivity());
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(DummyContent.ITEMS, mListener, context));
        }
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG, "onAttach");
        super.onAttach(activity);
        if (activity instanceof OnListFragmentInteractionListener) {
            Log.d(LOG_TAG, "всё хорошо с контекстом");
            mListener = (OnListFragmentInteractionListener) activity;
        } else {
            Log.d(LOG_TAG, "всё плохо с контекстом");
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");

        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(LOG_TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            Log.d(LOG_TAG, "всё хорошо с контекстом");
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            Log.d(LOG_TAG, "всё плохо с контекстом");
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }


    // реализуем работу с изображениями


    /**
     * Добавляет в кеш bitmap по ключу key
     *
     * @param key    ключ, по которому добавится bitmap
     * @param bitmap это положится в кеш
     */
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && key != null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
            Log.d("my_logs", "add");
        }
    }

    /**
     * Получает bitmap из кеша по ключу key.
     *
     * @param key ключ, по которому достаем bitmap
     * @return запрашиваемый bitmap, если есть ключ. Иначе - NULL
     */
    public static Bitmap getBitmapFromMemCache(String key) {
        Log.d("my_logs", "get");
        return mMemoryCache.get(key);
    }

    /**
     * Результат работы этого метода - отображение в iv картинки с именем name
     *
     * @param name имя файла, который нужно загрузить из интернета, диска или кеша
     * @param iv   ссылка на ту вьюшку, куда скачанное изображение надо поместить
     */
    public static void loadBitmap(String name, ImageView iv, Context context, int size) {
        final Bitmap bm;
        switch (iv.getId()) {
            case R.id.item_img:
                bm = getBitmapFromMemCache(name);
                break;
            case R.id.ivPage:
                bm = getBitmapFromMemCache(name.concat("big"));
                break;
            default:
                bm = getBitmapFromMemCache(name);
                break;
        }
        if (bm == null) {
            LoadImageTask loadImageTask = new LoadImageTask(name, iv, context, size);
            DownloadDrawable dd = new DownloadDrawable(loadImageTask);
            iv.setImageDrawable(dd);
            loadImageTask.execute();
        } else {
            cancelDownload(name, iv);
            iv.setImageBitmap(bm);
        }
    }


    public static class DownloadDrawable extends ColorDrawable {
        private final WeakReference<LoadImageTask> loadTaskWeak;

        private DownloadDrawable(LoadImageTask loadImageTask) {
            super(Color.DKGRAY);
            loadTaskWeak = new WeakReference<>(loadImageTask);
        }

        public LoadImageTask getTask() {
            return loadTaskWeak.get();
        }
    }


    private static void cancelDownload(String currentImageName, ImageView imageView) {
        LoadImageTask task = getTaskFromIV(imageView);
        if (task != null) {
            String imageNameFromTask = task._name;
            if (imageNameFromTask == null || !imageNameFromTask.equals(currentImageName)) {
                task.cancel(true);
            }
        }

    }

    private static LoadImageTask getTaskFromIV(ImageView imageView) {
        if (imageView != null) {
            Drawable d = imageView.getDrawable();
            if (d instanceof DownloadDrawable) {
                DownloadDrawable dd = (DownloadDrawable) d;
                return dd.getTask();
            }
        }
        return null;
    }


    /**
     * Подсчёт, в какое количество раз нужно уменьшить размеры файла, чтобы он стал минимальным, но
     * не меньшим req-размеров.
     *
     * @param options   опции BitmapFactory
     * @param reqWidth  требуемая ширина
     * @param reqHeight требуемая высота
     * @return непосредственно inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Фоново грузит изображение с диска, а если нет там, то с сети.
     */
    private static class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
        private final WeakReference<ImageView> _weakIv;
        private final WeakReference<Context> _context;
        private final String _name;
        private final int _size;

        public LoadImageTask(String name, ImageView iv, Context context, int size) {
            super();
            _weakIv = new WeakReference<>(iv);
            _context = new WeakReference<>(context);
            _name = name;
            _size = size;
        }

        /**
         * Копирует информацию из is в os.
         *
         * @param is поток, из которого копируем информацию
         * @param os поток, в который копируем
         */
        public void CopyStream(InputStream is, OutputStream os) {
            final int buffer_size = 1024;
            try {
                byte[] bytes = new byte[buffer_size];
                for (; ; ) {
                    int count = is.read(bytes, 0, buffer_size);
                    if (count == -1)
                        break;
                    os.write(bytes, 0, count);
                }
            } catch (Exception ex) {
            }
        }


        /**
         * Получение bitmap с размерами size*size из файла file
         *
         * @param file откуда нужно грузить изображение
         * @param size какого размера нужно грузить изображение
         * @return bitmap
         */
        protected static Bitmap decodeFile(File file, int size, String name) {
            try {
                InputStream is = new FileInputStream(file);
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, opt);
                int sc = calculateInSampleSize(opt, size, size);
                //is.reset();
                opt.inSampleSize = sc;
                opt.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, opt);
                if (bitmap != null) {
                    Log.d("LOAD_IMAGE", " name = " + name + " w = " + bitmap.getWidth() + " h = " + bitmap.getHeight());
                }
                return bitmap;
            } catch (IOException e) {
                //Log.e("LoadImageTask", "LoadImageTask.LoadBitmap IOException " + e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Context context = _context.get();
                Bitmap bitmap;
                File file;
                if (context != null) {
                    //InputStream is = context.getAssets().open(_name);
                    file = new File(context.getCacheDir(), _name.replace("/", ""));
                    bitmap = decodeFile(file, 64, _name);
                    if (null == bitmap) {
                        URL url = new URL(_name);
                        InputStream is = url.openConnection().getInputStream();
                        OutputStream os = new FileOutputStream(file);
                        CopyStream(is, os);
                        os.close();
                        bitmap = decodeFile(file, 64, _name);
                    }
                    return bitmap;
                }
            } catch (IOException e) {
                Log.e("LoadImageTask", "LoadImageTask.LoadBitmap IOException " + e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled())
                bitmap = null;

            Bitmap bm = getBitmapFromMemCache(_name);
            if (bm == null && bitmap != null) {
                addBitmapToMemoryCache(_name, bitmap);
                bm = bitmap;
            }
            ImageView iv = _weakIv.get();
            if (iv != null && this == getTaskFromIV(iv)) {

                iv.setImageBitmap(bm);

            }
        }
    }
}
