package ru.zib.project.dummy;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    private Activity mActivity;

    public DummyContent(Activity activity) {
        mActivity = activity;

        // тут попробуем реализовать заполнение из файла
        try {
            InputStream is = mActivity.openFileInput("technology");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int read;
            byte[] data = new byte[16384];

            while ((read = is.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, read);
            }

            is.close();

            outputStream.flush();
            String information = outputStream.toString();
            JSONObject jsonObject = new JSONObject(information);
            jsonObject = jsonObject.getJSONObject("technology");
            JSONArray jsonArray = jsonObject.names();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject temp = jsonObject.getJSONObject((String) jsonArray.get(i));
                String[] itemInfo = new String[3];
                String[] characters = {"title", "info", "picture"};
                for (int j = 0; j < 3; j++) {
                    if (temp.isNull(characters[j])) {
                        itemInfo[j] = "Информация отсутствует.";
                    } else {
                        itemInfo[j] = (String)temp.get(characters[j]);
                    }
                    Log.d("my_logs", itemInfo[j]);
                }
                addItem(createDummyItem(i, itemInfo[0], itemInfo[1], itemInfo[2]));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

//    private static final int COUNT = 25;
//
//    static {
//
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
//    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position), "img");
    }

    private static DummyItem createDummyItem(int position, String title, String info, String img) {
        return new DummyItem(String.valueOf(position), title, info, img);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String title;
        public final String info;
        public final String img;

        public DummyItem(String id, String title, String info, String img) {
            this.id = id;
            this.title = title;
            this.info = info;
            this.img = img;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}