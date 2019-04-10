package com.example.prueba1.Challenges;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.prueba1.R;

import java.util.ArrayList;

public class Challenges_adapter extends ArrayAdapter {

    private ArrayList<Challenges> list_challenges;

    private Context mContext;
    int mResource;


    public Challenges_adapter(Context context,int resource,  ArrayList<Challenges> objects) {
        super(context ,resource, objects);
        this.list_challenges = objects;
        mResource = resource;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        Challenges challenges = list_challenges.get(position);

        TextView challenge_name = convertView.findViewById(R.id.itemChallenge_nameChallenge);
        TextView challenge_date = convertView.findViewById(R.id.itemChallenge_dateChallenge);
        TextView challenge_mode = convertView.findViewById(R.id.itemChallenge_modeChallenge);

        challenge_name.setText(challenges.getName());
        challenge_date.setText(challenges.getExpiration_date());
        challenge_mode.setText(challenges.getGroup_general());

        return convertView;

    }
}