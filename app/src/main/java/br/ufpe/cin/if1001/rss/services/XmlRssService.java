package br.ufpe.cin.if1001.rss.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;
import br.ufpe.cin.if1001.rss.domain.ItemRSS;
import br.ufpe.cin.if1001.rss.util.ParserRSS;

public class XmlRssService extends IntentService {

    public static final String COMPLETE = "br.ufpe.cin.if1001.rss.action.COMPLETE";
    public static final String NEW = "br.ufpe.cin.if1001.rss.NEW";
    SQLiteRSSHelper db;


    public XmlRssService() {
        super("XmlRssService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        db = SQLiteRSSHelper.getInstance(getApplicationContext());
        boolean flag_problema = false;
        List<ItemRSS> items = null;
        try {
            String feed = getRssFeed(intent.getStringExtra("url"));
            items = ParserRSS.parse(feed);
            for (ItemRSS i : items) {
                Log.d("DB", "Buscando no Banco por link: " + i.getLink());
                ItemRSS item = db.getItemRSS(i.getLink());
                if (item == null) {
                    sendBroadcast(new Intent(NEW));
                    Log.d("DB", "Encontrado pela primeira vez: " + i.getTitle());

                    db.insertItem(i);
                }
            }

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
            flag_problema = true;
        }

        if (!flag_problema) {
            Log.d("FEED", "Houve algum problema ao carregar o feed.");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(COMPLETE));
        }
    }

    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return rssFeed;
    }
}
