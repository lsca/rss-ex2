package br.ufpe.cin.if1001.rss.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.ufpe.cin.if1001.rss.R;
import br.ufpe.cin.if1001.rss.db.RssProviderContract;
import br.ufpe.cin.if1001.rss.db.SQLiteRSSHelper;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RssAdapter extends RecyclerView.Adapter<RssAdapter.ItemRSSHolder> {

    private Context c;
    private SQLiteRSSHelper db;

    private Cursor cursor;

    public RssAdapter(Context c, Cursor cursor) {
        this.c = c;
        this.cursor = cursor;
        db = SQLiteRSSHelper.getInstance(c);
    }

    @Override
    public ItemRSSHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item, parent, false);
        ItemRSSHolder holder = new ItemRSSHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemRSSHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.title.setText(cursor.getString(cursor.getColumnIndexOrThrow(RssProviderContract.TITLE)));
        holder.pubDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(RssProviderContract.DATE)));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class ItemRSSHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView pubDate;

        public ItemRSSHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.itemTitulo);
            this.pubDate = itemView.findViewById(R.id.itemData);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            cursor.moveToPosition(getAdapterPosition());
            db.markAsRead(cursor.getString(cursor.getColumnIndexOrThrow(RssProviderContract.LINK)));
            Intent it = new Intent(Intent.ACTION_VIEW);
            Uri itemUrl = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(RssProviderContract.LINK)));
            it.setData(itemUrl);
            it.setFlags(FLAG_ACTIVITY_NEW_TASK);

            if (it.resolveActivity(c.getPackageManager()) != null) {
                c.startActivity(it);
            }

        }
    }
}
