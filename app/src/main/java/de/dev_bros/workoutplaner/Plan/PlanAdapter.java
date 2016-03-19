package de.dev_bros.workoutplaner.Plan;

import android.graphics.drawable.Icon;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import de.dev_bros.workoutplaner.R;

/**
 * Created by Marc on 09.03.2016.
 */
public class PlanAdapter  extends RecyclerView.Adapter<PlanAdapter.ViewHolder> implements View.OnClickListener{

    private OnControlCallbackListener_plan mCallback;
    private List<ParseObject> plans;
    private List<ParseObject> icons;

    public static final int PLAN = 0;
    public static final int FAVORIT_true = 1;
    public static final int FAVORIT_false = 2;






    public PlanAdapter(List<ParseObject> plans){
        this.plans = plans;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Icons");
        query.fromLocalDatastore();


        try {
            this.icons = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }




    @Override
    public void onClick(View v) {

        int id = Integer.parseInt(String.valueOf(v.getTag(R.string.ID_PLAN)));
        Log.i("App","ID: " + id);

        if(v.getId() == R.id.btnWorkout_Favorit_Plan){
            if(v.getTag(R.string.VALUE_PLAN).toString().equals("Y")){
                v.setTag(R.string.VALUE_PLAN, "N");
                ImageButton btn = (ImageButton) v;
                btn.setImageResource(R.drawable.black_gray);
                mCallback.onControlClickItem(plans.get(id),FAVORIT_false,id);

            }else{
                v.setTag(R.string.VALUE_PLAN, "Y");
                ImageButton btn = (ImageButton) v;
                btn.setImageResource(R.drawable.black);
                mCallback.onControlClickItem(plans.get(id), FAVORIT_true, id);
            }
        }else{
            mCallback.onControlClickItem(plans.get(id),PLAN,id);
        }
    }

    public interface OnControlCallbackListener_plan{
        public void onControlClickItem(ParseObject object,int mode,int position);
    }


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private ImageView imgPreviewPic;
        private ImageButton btnEdit;
        private ImageButton btnFavorit;
        private TextView lblTitel;
        private TextView lblCreatedAt;
        private RelativeLayout relWorkout_item;

        public ViewHolder(View view) {
            super(view);
            relWorkout_item = (RelativeLayout) view.findViewById(R.id.relativ_plan_item);
            imgPreviewPic = (ImageView) view.findViewById(R.id.imgWorkoutImg_Plan);
            btnFavorit = (ImageButton) view.findViewById(R.id.btnWorkout_Favorit_Plan);
            lblTitel = (TextView) view.findViewById(R.id.lblWorkout_Titel_Plan);
            lblCreatedAt = (TextView) view.findViewById(R.id.lblWorkout_Create_At_Plan);


        }

        public ImageView getImgPreviewPic() {
            return imgPreviewPic;
        }

        public ImageButton getBtnEdit() {
            return btnEdit;
        }

        public ImageButton getBtnFavorit() {
            return btnFavorit;
        }

        public TextView getLblTitel() {
            return lblTitel;
        }

        public TextView getLblCreatedAt() {
            return lblCreatedAt;
        }

        public RelativeLayout getRelWorkout_item() {
            return relWorkout_item;
        }
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_plan,parent,false);

        mCallback = (OnControlCallbackListener_plan) view.getContext();



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParseObject plan = plans.get(position);
        int iconID = plan.getInt("Icon");
        int planID = plan.getInt("objectId");


        boolean favorit = plan.getBoolean("Favorit");
        if(favorit == true){
            holder.btnFavorit.setImageResource(R.drawable.black);
            holder.btnFavorit.setTag(R.string.VALUE_PLAN,"Y");
            holder.btnFavorit.setTag(R.string.ID_PLAN,position);
        }else{
            holder.btnFavorit.setImageResource(R.drawable.black_gray);
            holder.btnFavorit.setTag(R.string.VALUE_PLAN,"N");
            holder.btnFavorit.setTag(R.string.ID_PLAN,position);
        }
        holder.btnFavorit.setOnClickListener(this);

        holder.relWorkout_item.setOnClickListener(this);
        holder.relWorkout_item.setTag(R.string.ID_PLAN, position);

        holder.lblCreatedAt.setOnClickListener(this);
        holder.lblCreatedAt.setTag(R.string.ID_PLAN, position);

        holder.lblTitel.setOnClickListener(this);
        holder.lblTitel.setTag(R.string.ID_PLAN,position);

        holder.imgPreviewPic.setOnClickListener(this);
        holder.imgPreviewPic.setTag(R.string.ID_PLAN, position);

    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

}
