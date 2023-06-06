package com.example.autotrackerca400;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class JourneyCardManager extends RecyclerView.Adapter<JourneyCardManager.ViewHolder>{
    private JourneyCard[] journeyCardData;
    IndividualJourneysActivity individualJourneysActivity;
    ViewHolder thisHolder;



    // RecyclerView recyclerView;
    public JourneyCardManager(JourneyCard[] JourneyData, IndividualJourneysActivity IndividualJourneysActivityHolder) {
        individualJourneysActivity = IndividualJourneysActivityHolder;
        this.journeyCardData = JourneyData;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup Parent, int ViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(Parent.getContext());
        @SuppressLint("ResourceType") View listItem= layoutInflater.inflate(R.xml.individualjourney, Parent, false);
        this.thisHolder = new ViewHolder(listItem);
        return this.thisHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder Holder, int Position) {
        this.thisHolder = Holder;
        // set journeyName text
        this.thisHolder.journeyNameView.setText(journeyCardData[Position].getDescription());

        // change color of card based on index
        if(Position % 2 == 0) {
            this.thisHolder.layoutView.setBackgroundColor(Color.parseColor("#D8AC76"));
        } else{
            this.thisHolder.layoutView.setBackgroundColor(Color.parseColor("#CFA573"));
        }

        // add functionality when buttons are clicked
        buttonFunctionality(Position);
    }

    public void buttonFunctionality(int Position){

        // delete journey
        this.thisHolder.imageDelete.setOnClickListener(view -> {
            individualJourneysActivity.deleteViewOn();
            individualJourneysActivity.setDeleteVar(Position + 1);
        });

        // rename journey
        this.thisHolder.imageRename.setOnClickListener(view -> individualJourneysActivity.renameViewOn());

        // individual journey details
        this.thisHolder.imageNext.setOnClickListener(view -> {
            individualJourneysActivity.individualJourneyOn();
            individualJourneysActivity.journeyStats(Position + 1);
        });
    }

    @Override
    public int getItemCount() {
        return journeyCardData.length;
    }

    public String getJourneyTitle(int index){
        return journeyCardData[index].getDescription();
    }

    public ViewHolder getViewHolder() {
        return this.thisHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageDelete;
        public ImageView imageNext;
        public ImageView imageRename;
        public TextView journeyNameView;
        public RelativeLayout relativeLayout;
        public LinearLayout layoutView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageDelete = itemView.findViewById(R.id.image_delete);
            this.imageNext = itemView.findViewById(R.id.image_next);
            this.imageRename = itemView.findViewById(R.id.write_rename);
            this.journeyNameView = itemView.findViewById(R.id.title_journey);
            this.layoutView = itemView.findViewById(R.id.m_linearLayoutHorizontal);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }
}