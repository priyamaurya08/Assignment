package m.com.assigment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ReportsRecyclerViewAdapter extends RecyclerView.Adapter<ReportsRecyclerViewAdapter.ViewHolder> {

    ArrayList<Result> list;

    public ReportsRecyclerViewAdapter(ArrayList<Result> list) {
        this.list=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.total_result_layout,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        if(list.get(i).getField1()!=null &&!TextUtils.isEmpty(list.get(i).getField1().getKey())){
            viewHolder.field1.setVisibility(View.VISIBLE);
            viewHolder.val1.setVisibility(View.VISIBLE);
            viewHolder.field1.setText(list.get(i).getField1().getKey().substring(0,1)+list.get(i).getField1().getKey().substring(1));
            viewHolder.val1.setText(list.get(i).getField1().getValue());
        }else {
            viewHolder.field1.setVisibility(View.GONE);
            viewHolder.val1.setVisibility(View.GONE);

        }

        if(list.get(i).getField2()!=null && !TextUtils.isEmpty(list.get(i).getField2().getKey())){
            viewHolder.field2.setVisibility(View.VISIBLE);
            viewHolder.val2.setVisibility(View.VISIBLE);
            viewHolder.field2.setText(list.get(i).getField2().getKey().substring(0,1)+list.get(i).getField2().getKey().substring(1));
            viewHolder.val2.setText(list.get(i).getField2().getValue());
        }else {
            viewHolder.field2.setVisibility(View.GONE);
            viewHolder.val2.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView field1,field2,val1,val2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            field1=itemView.findViewById(R.id.field1);
            field2=itemView.findViewById(R.id.field2);

            val1=itemView.findViewById(R.id.val1);
            val2=itemView.findViewById(R.id.val2);
        }
    }
}
