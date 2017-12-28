package com.teknokrait.tomatoclassification.view.trainning;

/**
 * Created by Aprilian Nur Wakhid Daini on 12/25/2017.
 */

import com.teknokrait.tomatoclassification.R;
import com.teknokrait.tomatoclassification.adapters.RealmRecyclerViewAdapter;
import com.teknokrait.tomatoclassification.model.Tomato;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.teknokrait.tomatoclassification.realm.RealmController;

import io.realm.Realm;

public class TomatoesAdapter extends RealmRecyclerViewAdapter<Tomato> {

    final Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public TomatoesAdapter(Context context) {

        this.context = context;
    }

    // create new views (invoked by the layout manager)
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tomato, parent, false);
        return new CardViewHolder(view);
    }

    // replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        realm = RealmController.getInstance().getRealm();

        // get the article
        final Tomato tomato = getItem(position);
        // cast the generic view holder to our specific one
        final CardViewHolder holder = (CardViewHolder) viewHolder;

        // set the title and the snippet
        holder.idTextView.setText(String.valueOf(tomato.getId()));
        holder.statusTextView.setText(tomato.getStatus());
        holder.redTextView.setText(String.valueOf(tomato.getRed()));
        holder.greenTextView.setText(String.valueOf(tomato.getGreen()));
        holder.blueTextView.setText(String.valueOf(tomato.getBlue()));

        // load the background image
        if (tomato.getImageUrl() != null) {
            Glide.with(context)
                    .load(tomato.getImageUrl().replace("https", "http"))
                    .asBitmap()
                    .fitCenter()
                    .into(holder.imageImageView);
        }

        //remove single match from realm
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                /*RealmResults<Tomato> results = realm.where(Tomato.class).findAll();

                // Get the book title to show it in toast message
                Tomato t = results.get(position);
                String title = t.getTitle();

                // All changes to data must happen in a transaction
                realm.beginTransaction();

                // remove single match
                results.remove(position);
                realm.commitTransaction();

                if (results.size() == 0) {
                    Prefs.with(context).setPreLoad(false);
                }

                notifyDataSetChanged();

                Toast.makeText(context, title + " is removed from Realm", Toast.LENGTH_SHORT).show();*/
                return false;
            }
        });

        //update single match from realm
        holder.card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View content = inflater.inflate(R.layout.edit_item, null);
                final EditText editTitle = (EditText) content.findViewById(R.id.title);
                final EditText editAuthor = (EditText) content.findViewById(R.id.author);
                final EditText editThumbnail = (EditText) content.findViewById(R.id.thumbnail);

                editTitle.setText(book.getTitle());
                editAuthor.setText(book.getAuthor());
                editThumbnail.setText(book.getImageUrl());

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(content)
                        .setTitle("Edit Book")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RealmResults<Tomato> results = realm.where(Tomato.class).findAll();

                                realm.beginTransaction();
                                results.get(position).setAuthor(editAuthor.getText().toString());
                                results.get(position).setTitle(editTitle.getText().toString());
                                results.get(position).setImageUrl(editThumbnail.getText().toString());

                                realm.commitTransaction();

                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();*/
            }
        });
    }

    // return the size of your data set (invoked by the layout manager)
    public int getItemCount() {

        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public CardView card;
        public TextView idTextView;
        public TextView statusTextView;
        public TextView redTextView, greenTextView, blueTextView;
        public ImageView imageImageView;

        public CardViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.card_books);
            redTextView = (TextView) itemView.findViewById(R.id.red_textView);
            greenTextView = (TextView) itemView.findViewById(R.id.green_textView);
            blueTextView = (TextView) itemView.findViewById(R.id.blue_textView);
            statusTextView = (TextView) itemView.findViewById(R.id.status_textView);
            idTextView = (TextView) itemView.findViewById(R.id.id_textView);
            imageImageView = (ImageView) itemView.findViewById(R.id.image_imageView);
        }
    }
}
