package com.teknokrait.tomatoclassification.view.trainning;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.teknokrait.tomatoclassification.R;
import com.teknokrait.tomatoclassification.adapters.RealmTomatoesAdapter;
import com.teknokrait.tomatoclassification.app.Prefs;
import com.teknokrait.tomatoclassification.model.Tomato;
import com.teknokrait.tomatoclassification.realm.RealmController;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class DataTrainingActivity extends AppCompatActivity {

    private TomatoesAdapter adapter;
    private Realm realm;
    private LayoutInflater inflater;
    private FloatingActionButton fab;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_training);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recycler = (RecyclerView) findViewById(R.id.recycler);

        //get realm instance
        this.realm = RealmController.with(this).getRealm();

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupRecycler();

        if (!Prefs.with(this).getPreLoad()) {
            //setRealmData();
        }

        // refresh the realm instance
        RealmController.with(this).refresh();
        // get all persisted objects
        // create the helper adapter and notify data set changes
        // changes will be reflected automatically
        setRealmAdapter(RealmController.with(this).getTomatoes());

        Toast.makeText(this, "Press card item for edit, long press to remove item", Toast.LENGTH_LONG).show();

        //add new item
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DataTrainingActivity.this, TrainingResultActivity.class);
                startActivity(intent);

                /*inflater = TrainingResultActivity.this.getLayoutInflater();
                View content = inflater.inflate(R.layout.edit_item, null);
                final EditText editTitle = (EditText) content.findViewById(R.id.title);
                final EditText editAuthor = (EditText) content.findViewById(R.id.author);
                final EditText editThumbnail = (EditText) content.findViewById(R.id.thumbnail);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(content)
                        .setTitle("Add book")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Book book = new Book();
                                //book.setId(RealmController.getInstance().getBooks().size() + 1);
                                book.setId(RealmController.getInstance().getBooks().size() + System.currentTimeMillis());
                                book.setTitle(editTitle.getText().toString());
                                book.setAuthor(editAuthor.getText().toString());
                                book.setImageUrl(editThumbnail.getText().toString());

                                if (editTitle.getText() == null || editTitle.getText().toString().equals("") || editTitle.getText().toString().equals(" ")) {
                                    Toast.makeText(TrainingResultActivity.this, "Entry not saved, missing title", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Persist your data easily
                                    realm.beginTransaction();
                                    realm.copyToRealm(book);
                                    realm.commitTransaction();

                                    adapter.notifyDataSetChanged();

                                    // scroll the recycler view to bottom
                                    recycler.scrollToPosition(RealmController.getInstance().getTomatoes().size() - 1);
                                }
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

    public void setRealmAdapter(RealmResults<Tomato> tomatoes) {

        RealmTomatoesAdapter realmAdapter = new RealmTomatoesAdapter(this.getApplicationContext(), tomatoes, true);
        // Set the data and tell the RecyclerView to draw
        adapter.setRealmAdapter(realmAdapter);
        adapter.notifyDataSetChanged();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recycler.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        // create an empty adapter and add it to the recycler view
        adapter = new TomatoesAdapter(this);
        recycler.setAdapter(adapter);
    }



    private void setRealmData() {

        ArrayList<Tomato> tomatos = new ArrayList<>();

        Tomato tomato = new Tomato();
        tomato.setId(1);
        tomato.setImageUrl("https://halosehat.com/wp-content/uploads/2015/03/buah-berbahaya-tomat-mentah.jpg");
        tomato.setImagePath(null);
        tomato.setStatus("Mentah");
        tomato.setRed(186);
        tomato.setGreen(200);
        tomato.setBlue(50);
        tomatos.add(tomato);

        /*tomato = new Tomato();
        tomato.setId(2);
        tomato.setImageUrl("http://www.nomeatathlete.com/wp-content/uploads/2011/07/iStock_000007185653XSmall.jpg");
        tomato.setImagePath(null);
        tomato.setStatus("Matang");
        tomato.setRed(251);
        tomato.setGreen(171);
        tomato.setBlue(50);
        tomatos.add(tomato);*/


        for (Tomato t : tomatos) {
            // Persist your data easily
            realm.beginTransaction();
            realm.copyToRealm(t);
            realm.commitTransaction();
        }

        Prefs.with(this).setPreLoad(true);

    }


    @Override
    protected void onResume() {
        super.onResume();
        RealmController.with(this).refresh();
        // get all persisted objects
        // create the helper adapter and notify data set changes
        // changes will be reflected automatically
        setRealmAdapter(RealmController.with(this).getTomatoes());
    }
}
