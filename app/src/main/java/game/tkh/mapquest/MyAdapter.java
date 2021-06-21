package game.tkh.mapquest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    LayoutInflater inflater;
    ArrayList<Quest> readNovels;
    OnQuestListener mOnQuestListener;
    Quest quest;

    public MyAdapter(Context context, ArrayList<Quest> readNovels, OnQuestListener onQuestListener) {
        inflater = LayoutInflater.from(context);
        this.readNovels = readNovels;
        mOnQuestListener = onQuestListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        OnQuestListener onQuestListener;
        Quest quest;

        public ViewHolder(View itemView, OnQuestListener onQuestListener) {
            super(itemView);
            title = itemView.findViewById(R.id.title);

            this.onQuestListener = onQuestListener;
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            quest = new Quest(0,0,"", false, false, 0);
            title.setText(quest.getHeading(readNovels.get(listIndex).tag.replaceAll("[^A-Za-z]","")));
        }

        @Override
        public void onClick(View v) {
            mOnQuestListener.onQuestCLick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view, mOnQuestListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return readNovels.size();
    }

    public interface OnQuestListener {

        void onQuestCLick(int position);
    }

}
